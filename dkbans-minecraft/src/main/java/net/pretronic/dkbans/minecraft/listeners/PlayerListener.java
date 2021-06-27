/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 17:26
 * @web %web%
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.pretronic.dkbans.minecraft.listeners;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.event.DKBansBypassCheckEvent;
import net.pretronic.dkbans.api.filter.FilterAffiliationArea;
import net.pretronic.dkbans.api.filter.FilterManager;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlock;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlockType;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.dkbans.common.DKBansUtil;
import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.Command;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.event.execution.ExecutionType;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.event.player.MinecraftPlayerChatEvent;
import org.mcnative.runtime.api.event.player.MinecraftPlayerCommandPreprocessEvent;
import org.mcnative.runtime.api.event.player.MinecraftPlayerLogoutEvent;
import org.mcnative.runtime.api.event.player.MinecraftPlayerTabCompleteResponseEvent;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerLoginConfirmEvent;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerLoginEvent;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.runtime.api.network.component.server.ProxyServer;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerListener {

    private final Map<UUID,LastMessage> lastMessages = new HashMap<>();

    @Listener(priority = EventPriority.HIGH)
    public void onPlayerLogin(MinecraftPlayerLoginEvent event){
        if(event.isCancelled()) return;

        if(DKBans.getInstance().getFilterManager().checkFilter(FilterAffiliationArea.PLAYER_NAME,event.getPlayer().getName())){
            event.setCancelled(true);
            event.setCancelReason(Messages.FILTER_BLOCKED_NAME,VariableSet.create()
                    .add("name",event.getPlayer().getName()));
            return;
        }

        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        PlayerHistoryEntry ban = player.getHistory().getActiveEntry(PunishmentType.BAN,DKBansScope.GLOBAL);
        if(ban != null){
            event.setCancelled(true);
            MessageComponent<?> message = ban.getCurrent().isPermanently()
                    ? Messages.PUNISH_MESSAGE_BAN_PERMANENTLY : Messages.PUNISH_MESSAGE_BAN_TEMPORARY;
            event.setCancelReason(message,VariableSet.create()
                    .addDescribed("ban",ban)
                    .addDescribed("punish",ban)
                    .addDescribed("player",event.getPlayer()));
            return;
        }
        IpAddressBlock ipAddressBlock = DKBans.getInstance().getIpAddressManager().getIpAddressBlock(event.getOnlinePlayer().getAddress().getAddress().getHostAddress());
        if(ipAddressBlock != null) {
            if(ipAddressBlock.getType() == IpAddressBlockType.BLOCK){
                event.setCancelReason(Messages.PUNISH_ADDRESS_BLOCK,VariableSet.create()
                        .addDescribed("ban",ipAddressBlock)
                        .addDescribed("block",ipAddressBlock));
                event.setCancelled(true);
            }else if(ipAddressBlock.getType() == IpAddressBlockType.BAN){
                banPlayer(event,player,ipAddressBlock);
                event.setCancelled(true);
            }else if(ipAddressBlock.getType() == IpAddressBlockType.ALT_ACCOUNT){
                long minOnlineTime = DKBansConfig.IP_ADDRESS_BLOCK_ALT_MIN_PLAYTIME_TIME;
                if(event.getPlayer().getFirstPlayed()+minOnlineTime < System.currentTimeMillis()){
                    banPlayer(event,player,ipAddressBlock);
                    event.setCancelled(true);
                }
            }
        }
    }

    private void banPlayer(MinecraftPlayerLoginEvent event,DKBansPlayer player,IpAddressBlock ipAddressBlock){
        PlayerHistoryEntrySnapshot result;
        if(ipAddressBlock.getForTemplate() != null) {
            result = player.punish(DKBansExecutor.IP_ADDRESS_BLOCK,ipAddressBlock.getForTemplate());
        } else {
            PlayerHistoryEntrySnapshotBuilder builder = player.punish().staff(DKBansExecutor.IP_ADDRESS_BLOCK);
            builder.reason(ipAddressBlock.getForReason())
                    .staff(DKBansExecutor.IP_ADDRESS_BLOCK)
                    .historyType(DKBans.getInstance().getHistoryManager().getHistoryType(DKBansConfig.IP_ADDRESS_BLOCK_HISTORY_TYPE_NAME))
                    .timeout(System.currentTimeMillis()+ipAddressBlock.getForDuration());
            result = builder.execute();
        }
        MessageComponent<?> message = result.isPermanently()
                ? Messages.PUNISH_MESSAGE_BAN_PERMANENTLY : Messages.PUNISH_MESSAGE_BAN_TEMPORARY;
        event.setCancelReason(message,VariableSet.create()
                .addDescribed("ban",result)
                .addDescribed("punish",result)
                .addDescribed("player",event.getPlayer()));
    }

    @Listener(priority = EventPriority.LOWEST)
    public void onPlayerPostLoginLow(MinecraftPlayerPostLoginEvent event){
        if(DKBansConfig.PLAYER_ON_JOIN_CLEAR_CHAT){
            for(int i = 1; i < 120; i++) event.getOnlinePlayer().sendMessage(Text.EMPTY);
        }
    }

    @Listener(priority = EventPriority.HIGH,execution = ExecutionType.ASYNC)
    public void onPlayerPostLogin(MinecraftPlayerPostLoginEvent event){
        OnlineMinecraftPlayer player = event.getOnlinePlayer();

        if(DKBansConfig.PLAYER_ON_JOIN_PUNISH_NOTIFY && player.hasPermission(CommandConfig.PERMISSION_PUNISH_NOTIFY)){
            boolean teamChat = event.getPlayer().hasSetting("DKBans",PlayerSettingsKey.PUNISH_NOTIFY_LOGIN,true);
            player.sendMessage(Messages.STAFF_STATUS_NOW,VariableSet.create()
                    .add("prefix",Messages.PREFIX)
                    .add("status",teamChat)
                    .add("statusFormatted", teamChat ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
        }

        if(DKBansConfig.PLAYER_ON_JOIN_INFO_TEAMCHAT && player.hasPermission(CommandConfig.PERMISSION_COMMAND_TEAMCHAT_RECEIVE)){
            boolean teamChat = event.getPlayer().hasSetting("DKBans",PlayerSettingsKey.TEAM_CHAT_LOGIN,true);
            player.sendMessage(Messages.STAFF_STATUS_NOW,VariableSet.create()
                    .add("prefix",Messages.PREFIX_TEAMCHAT)
                    .add("status",teamChat)
                    .add("statusFormatted", teamChat ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
        }

        if(DKBansConfig.PLAYER_ON_JOIN_INFO_REPORT && player.hasPermission(CommandConfig.PERMISSION_COMMAND_REPORT_STAFF)){
            boolean report = event.getPlayer().hasSetting("DKBans", PlayerSettingsKey.REPORT_CHAT_LOGIN,true);
            player.sendMessage(Messages.STAFF_STATUS_NOW,VariableSet.create()
                    .add("prefix",Messages.PREFIX_REPORT)
                    .add("status",report)
                    .add("statusFormatted", report ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
        }

        if(DKBansConfig.PLAYER_ON_JOIN_LIST_REPORTS && player.hasPermission(CommandConfig.PERMISSION_COMMAND_REPORT_STAFF)){
            int openReports = DKBans.getInstance().getReportManager().getNewReports().size();
            player.sendMessage(Messages.REPORT_COUNT_INFO,VariableSet.create().add("openReports",openReports));
        }
    }

    @Listener
    public void onPlayerLoginConfirm(MinecraftPlayerLoginConfirmEvent event) {
        if(DKBansConfig.PLAYER_SESSION_LOGGING){
            OnlineMinecraftPlayer player = event.getOnlinePlayer();
            DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

            ConnectedMinecraftPlayer connectedPlayer = player.getAsConnectedPlayer();

            Pair<String,String> locationLookup = DKBansUtil.lookupLocation(player.getAddress().getAddress().getHostAddress());

            ProxyServer proxy = McNative.getInstance().isNetworkAvailable() ? player.getProxy() : null;

            dkBansPlayer.startSession(player.getName(),player.getAddress().getAddress(), locationLookup.getKey(), locationLookup.getValue(),
                    proxy != null ? proxy.getName() : null, proxy != null ? proxy.getUniqueId() : null
                    ,connectedPlayer.getProtocolVersion().getEdition().getName()
                    ,connectedPlayer.getProtocolVersion().getNumber());
        }
    }

    @Listener
    public void onPlayerDisconnect(MinecraftPlayerLogoutEvent event) {
        MinecraftPlayer mcPlayer = event.getPlayer();
        DKBansPlayer player = mcPlayer.getAs(DKBansPlayer.class);
        event.getPlayer().setSetting("DKBans", PlayerSettingsKey.BYPASS,mcPlayer.hasPermission(CommandConfig.PERMISSION_BYPASS));
        if(player == null || event.getOnlinePlayer().getServer() == null) return;
        player.finishSession(event.getOnlinePlayer().getServer().getName(),
                event.getOnlinePlayer().getServer().getIdentifier().getUniqueId());
        if(player.getWatchingReport() != null){
            player.getWatchingReport().setState(ReportState.NEW);
        }
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onPlayerChat(MinecraftPlayerChatEvent event){
        if(event.isCancelled()) return;
        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        PlayerHistoryEntry mute = player.getHistory().getActiveEntry(PunishmentType.MUTE,DKBansScope.GLOBAL);
        if(mute == null){
            mute = player.getHistory().getActiveEntry(PunishmentType.MUTE,DKBansScope.ofServer(event.getOnlinePlayer().getServer().getName()));
            if(mute == null){
                mute = player.getHistory().getActiveEntry(PunishmentType.MUTE,DKBansScope.ofServerGroup(event.getOnlinePlayer().getServer().getGroup()));
            }
        }

        if(mute != null){
            event.setCancelled(true);
            sendMutedMessage(event.getOnlinePlayer(), mute);
        }else{
            boolean bypass = event.getPlayer().hasPermission(CommandConfig.PERMISSION_CHAT_BYPASS);

            String filterAffiliationArea = null;
            if(DKBansConfig.CHAT_FILTER_ENABLED && !bypass){
                if(checkBasicFilters(event, player)) return;
                filterAffiliationArea = checkMessageBlocked(event);
            }

            DKBans.getInstance().getChatLogManager().createChatLogEntryAsync(event.getPlayer().getUniqueId(),
                    event.getMessage(),
                    System.currentTimeMillis(),
                    event.getOnlinePlayer().getServer().getIdentifier().getName(),
                    event.getOnlinePlayer().getServer().getIdentifier().getUniqueId(),
                    filterAffiliationArea);
        }
    }

    private boolean checkBasicFilters(MinecraftPlayerChatEvent event, DKBansPlayer player){
        if(DKBansConfig.CHAT_FILTER_BLOCK_CAPSLOCK){
            int index = 0;
            for (char character : event.getMessage().toCharArray()) {
                if(Character.isUpperCase(character)){
                    index++;
                    if(index == 4){
                        event.setCancelled(true);
                        event.getOnlinePlayer().sendMessage(Messages.CHAT_FILTER_SPAM_CAPSLOCK);
                        return true;
                    }
                }else index = 0;
            }
        }

        LastMessage lastMessage = this.lastMessages.get(player.getUniqueId());
        if(lastMessage != null){
            if(DKBansConfig.CHAT_FILTER_BLOCK_TOFAST && lastMessage.time+DKBansConfig.CHAT_FILTER_TOFAST_DELAY >= System.currentTimeMillis()){
                event.setCancelled(true);
                event.getOnlinePlayer().sendMessage(Messages.CHAT_FILTER_SPAM_TOFAST);
                return true;
            }else if(DKBansConfig.CHAT_FILTER_BLOCK_REPEAT && lastMessage.time < (System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(1))
                    && event.getMessage().equalsIgnoreCase(lastMessage.message)){
                event.setCancelled(true);
                event.getOnlinePlayer().sendMessage(Messages.CHAT_FILTER_SPAM_REPEAT);
                return true;
            }
        }
        this.lastMessages.put(player.getUniqueId(),new LastMessage(event.getMessage(),System.currentTimeMillis()));
        return false;
    }

    private String checkMessageBlocked(MinecraftPlayerChatEvent event) {
        FilterManager filterManager = DKBans.getInstance().getFilterManager();
        if(filterManager.checkFilter(FilterAffiliationArea.CHAT_INSULT,event.getMessage())){
            event.getOnlinePlayer().sendMessage(Messages.FILTER_BLOCKED_INSULTING);
            event.setCancelled(true);
            sendBlockNotification(event.getMessage(),event.getOnlinePlayer());
            return FilterAffiliationArea.CHAT_INSULT;
        }

        if(filterManager.checkFilter(FilterAffiliationArea.CHAT_ADVERTISING,event.getMessage())){
            event.getOnlinePlayer().sendMessage(Messages.FILTER_BLOCKED_ADVERTISING);
            event.setCancelled(true);
            sendBlockNotification(event.getMessage(), event.getOnlinePlayer());
            return FilterAffiliationArea.CHAT_ADVERTISING;
        }
        return null;
    }

    private void sendBlockNotification(String message, OnlineMinecraftPlayer player){//CHAT_FILTER_NOTIFICATION
        if(DKBansConfig.CHAT_FILTER_NOTIFICATION){
            MessageComponent<?> message0 = Messages.FILTER_BLOCKED_NOTIFICATION;
            VariableSet variables = VariableSet.create().add("message",message).addDescribed("player",player);

            Collection<OnlineMinecraftPlayer> players;
            if(McNative.getInstance().getPlatform().isProxy()){
                players = player.getServer().getOnlinePlayers();
            }else players = McNative.getInstance().getLocal().getOnlinePlayers();

            for (OnlineMinecraftPlayer staff : players) {
                if (staff.hasPermission(CommandConfig.PERMISSION_CHAT_NOTIFICATION)
                        && staff.hasSetting("DKBans", PlayerSettingsKey.PUNISH_NOTIFY_LOGIN, true)) {
                    staff.sendMessage(message0,variables);
                }
            }
        }
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(MinecraftPlayerCommandPreprocessEvent event){
        if(event.isCancelled()) return;
        FilterManager filterManager = DKBans.getInstance().getFilterManager();

        if(filterManager.checkFilter(FilterAffiliationArea.COMMAND,event.getCommand())){
            event.getOnlinePlayer().sendMessage(Messages.FILTER_BLOCKED_COMMAND,VariableSet.create()
                    .add("baseCommand",event.getBaseCommand())
                    .add("command",event.getCommand()));
            event.setCancelled(true);
            return;
        }

        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        PlayerHistoryEntry mute = player.getHistory().getActiveEntry(PunishmentType.MUTE);
        if(mute != null){
            if(filterManager.checkFilter(FilterAffiliationArea.COMMAND_MUTE,event.getCommand())){
                sendMutedMessage(event.getOnlinePlayer(),mute);
                event.setCancelled(true);
            }
        }
    }

    @Listener
    public void onTabComplete(MinecraftPlayerTabCompleteResponseEvent event){
        if(!DKBansConfig.CHAT_TAB_COMPLETE_ENABLED) return;
        if(event.getCursor() != null && event.getCursor().startsWith("/") && !event.getCursor().contains(" ")){
            boolean bypass = DKBansConfig.CHAT_TAB_COMPLETE_MODE_ALLOW_BYPASS && event.getPlayer().hasPermission(CommandConfig.PERMISSION_CHAT_BYPASS_TAB_COMPLETION);;
            if(!bypass){
                event.getSuggestions().clear();
                List<String> suggestions = null;
                if(DKBansConfig.CHAT_TAB_COMPLETE_MODE.equalsIgnoreCase("DYNAMIC")){
                    List<Command> commands = McNative.getInstance().getLocal().getCommandManager().getCommands();
                    suggestions = Iterators.map(commands, command -> command.getConfiguration().getName()
                            , command -> (command.getConfiguration().getPermission() == null || event.getPlayer().hasPermission(command.getConfiguration().getPermission()))
                                    && command.getConfiguration().getName().toLowerCase().startsWith(event.getCursor().substring(1).toLowerCase()));
                }else if(DKBansConfig.CHAT_TAB_COMPLETE_MODE.equalsIgnoreCase("SUGGESTED")){
                    suggestions = Iterators.map(DKBansConfig.CHAT_TAB_COMPLETE_SUGGESTIONS,Pair::getKey
                            ,command -> (command.getValue() == null || event.getPlayer().hasPermission(command.getValue()))
                                    && command.getKey().toLowerCase().startsWith(event.getCursor().toLowerCase()));
                }
                if(suggestions != null){
                    event.getSuggestions().addAll(suggestions);
                }
            }
        }
    }

    @Listener
    public void onBypassCheck(DKBansBypassCheckEvent event){
        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(event.getPlayerId());
        if(player != null){
            if(player.isConnected()){
                event.setBypass(player.hasPermission(CommandConfig.PERMISSION_BYPASS));
            }else{
                event.setBypass(player.hasSetting("DKBans", PlayerSettingsKey.BYPASS,true));
            }
        }
    }

    private void sendMutedMessage(OnlineMinecraftPlayer player, PlayerHistoryEntry mute) {
        MessageComponent<?> message = mute.getCurrent().isPermanently()
                ? Messages.PUNISH_MESSAGE_MUTE_PERMANENTLY : Messages.PUNISH_MESSAGE_MUTE_TEMPORARY;
        player.sendMessage(message, VariableSet.create()
                .addDescribed("mute",mute)
                .addDescribed("punish",mute)
                .addDescribed("player",player));
    }

    private final static class LastMessage {

        private final String message;
        private final long time;

        public LastMessage(String message, long time) {
            this.message = message;
            this.time = time;
        }
    }


}
