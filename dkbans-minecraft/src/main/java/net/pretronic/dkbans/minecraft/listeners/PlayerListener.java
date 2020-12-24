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
import net.pretronic.dkbans.api.filter.FilterAffiliationArea;
import net.pretronic.dkbans.api.filter.FilterManager;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlock;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlockType;
import net.pretronic.dkbans.common.DKBansUtil;
import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.common.McNative;
import org.mcnative.common.event.player.MinecraftPlayerChatEvent;
import org.mcnative.common.event.player.MinecraftPlayerCommandPreprocessEvent;
import org.mcnative.common.event.player.MinecraftPlayerLogoutEvent;
import org.mcnative.common.event.player.login.MinecraftPlayerLoginEvent;
import org.mcnative.common.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.common.network.component.server.ProxyServer;
import org.mcnative.common.player.ConnectedMinecraftPlayer;
import org.mcnative.common.player.OnlineMinecraftPlayer;
import org.mcnative.common.text.Text;
import org.mcnative.common.text.components.MessageComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

        if(player.hasActivePunish(PunishmentType.BAN)){

            PlayerHistoryEntry ban = player.getHistory().getActiveEntry(PunishmentType.BAN);
            event.setCancelled(true);
            MessageComponent<?> message = ban.getCurrent().isPermanently()
                    ? Messages.PUNISH_BAN_MESSAGE_PERMANENTLY : Messages.PUNISH_BAN_MESSAGE_TEMPORARY;
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
        PlayerHistoryEntrySnapshotBuilder builder = player.punish().stuff(DKBansExecutor.IP_ADDRESS_BLOCK);
        if(ipAddressBlock.getForTemplate() != null) {
            builder.template(ipAddressBlock.getForTemplate());
        } else {
            builder.reason(ipAddressBlock.getForReason())
                    .timeout(System.currentTimeMillis()+ipAddressBlock.getForDuration());
        }
        PlayerHistoryEntrySnapshot result = builder.execute();
        MessageComponent<?> message = result.isPermanently()
                ? Messages.PUNISH_BAN_MESSAGE_PERMANENTLY : Messages.PUNISH_BAN_MESSAGE_TEMPORARY;
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

    @Listener(priority = EventPriority.HIGHEST)//@Todo async
    public void onPlayerPostLogin(MinecraftPlayerPostLoginEvent event){
        OnlineMinecraftPlayer player = event.getOnlinePlayer();
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

        if(DKBansConfig.PLAYER_ON_JOIN_PUNISH_NOTIFY && player.hasPermission(Permissions.PUNISH_NOTIFY)){
            boolean teamChat = event.getPlayer().hasSetting("DKBans",PlayerSettingsKey.PUNISH_NOTIFY_LOGIN,true);
            player.sendMessage(Messages.STAFF_STATUS_NOW,VariableSet.create()
                    .add("prefix",Messages.PREFIX)
                    .add("status",teamChat)
                    .add("statusFormatted", teamChat ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
        }

        if(DKBansConfig.PLAYER_ON_JOIN_INFO_TEAMCHAT && player.hasPermission(Permissions.TEAM)){
            boolean teamChat = event.getPlayer().hasSetting("DKBans",PlayerSettingsKey.TEAM_CHAT_LOGIN,true);
            player.sendMessage(Messages.STAFF_STATUS_NOW,VariableSet.create()
                    .add("prefix",Messages.PREFIX_TEAMCHAT)
                    .add("status",teamChat)
                    .add("statusFormatted", teamChat ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
        }

        if(DKBansConfig.PLAYER_ON_JOIN_INFO_REPORT && player.hasPermission(Permissions.COMMAND_REPORT_STUFF)){
            boolean report = event.getPlayer().hasSetting("DKBans", PlayerSettingsKey.REPORT_CHAT_LOGIN,true);
            player.sendMessage(Messages.STAFF_STATUS_NOW,VariableSet.create()
                    .add("prefix",Messages.PREFIX_REPORT)
                    .add("status",report)
                    .add("statusFormatted", report ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
        }

        if(DKBansConfig.PLAYER_ON_JOIN_LIST_REPORTS && player.hasPermission(Permissions.COMMAND_REPORT_STUFF)){
            int openReports = DKBans.getInstance().getReportManager().getOpenReports().size();
            player.sendMessage(Messages.REPORT_COUNT_INFO,VariableSet.create().add("openReports",openReports));
        }

        if(DKBansConfig.PLAYER_SESSION_LOGGING){
            ConnectedMinecraftPlayer connectedPlayer = McNative.getInstance().getLocal().getConnectedPlayer(player.getUniqueId());

            Pair<String,String> locationLookup = DKBansUtil.lookupLocation(player.getAddress().getAddress().getHostAddress());

            ProxyServer proxy = player.getProxy();

            dkBansPlayer.startSession(player.getName(),player.getAddress().getAddress(), locationLookup.getKey(), locationLookup.getValue(),
                    proxy != null ? proxy.getName() : null, proxy != null ? proxy.getUniqueId() : null
                    ,connectedPlayer.getProtocolVersion().getEdition().getName()
                    ,connectedPlayer.getProtocolVersion().getNumber());
        }
    }

    @Listener//@Todo async
    public void onPlayerDisconnect(MinecraftPlayerLogoutEvent event) {
        event.getPlayer().getAs(DKBansPlayer.class).finishSession(event.getOnlinePlayer().getServer().getName(),
                event.getOnlinePlayer().getServer().getIdentifier().getUniqueId());
    }

    @Listener(priority = EventPriority.HIGHEST)
    public void onPlayerChat(MinecraftPlayerChatEvent event){
        if(event.isCancelled()) return;
        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);
        boolean bypass = event.getPlayer().hasPermission(Permissions.CHAT_BYPASS);

        if(DKBansConfig.CHAT_FILTER_ENABLED && !bypass && checkBasicFilters(event, player)) return;

        PlayerHistoryEntry mute = player.getHistory().getActiveEntry(PunishmentType.MUTE);
        if(mute != null){
            event.setCancelled(true);
            sendMutedMessage(event.getOnlinePlayer(), mute);
        }else{
            String filterAffiliationArea = checkMessageBlocked(event,player);
            DKBans.getInstance().getChatLogManager().createChatLogEntryAsync(event.getPlayer().getUniqueId(),
                    event.getMessage(),
                    System.currentTimeMillis(),
                    event.getOnlinePlayer().getServer().getIdentifier().getName(),
                    event.getOnlinePlayer().getServer().getIdentifier().getUniqueId(),
                    filterAffiliationArea);
        }
    }

    private boolean checkBasicFilters(MinecraftPlayerChatEvent event, DKBansPlayer player){
        LastMessage lastMessage = this.lastMessages.get(player.getUniqueId());
        if(lastMessage != null){
            if(lastMessage.time+DKBansConfig.CHAT_FILTER_REPEAT_DELAY >= System.currentTimeMillis()){
                event.setCancelled(true);
                event.getOnlinePlayer().sendMessage(Messages.CHAT_FILTER_SPAM_TOFAST);
                return true;
            }else if(lastMessage.time < (System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(1))
                    && event.getMessage().equalsIgnoreCase(lastMessage.message)){
                event.setCancelled(true);
                event.getOnlinePlayer().sendMessage(Messages.CHAT_FILTER_SPAM_REPEAT);
                return true;
            }
        }
        this.lastMessages.put(player.getUniqueId(),new LastMessage(event.getMessage(),System.currentTimeMillis()));
        return false;
    }

    private String checkMessageBlocked(MinecraftPlayerChatEvent event, DKBansPlayer player) {
        FilterManager filterManager = DKBans.getInstance().getFilterManager();
        if(filterManager.checkFilter(FilterAffiliationArea.CHAT_INSULT,event.getMessage())){
            event.getOnlinePlayer().sendMessage(Messages.FILTER_BLOCKED_INSULTING);
            event.setCancelled(true);
            return FilterAffiliationArea.CHAT_INSULT;
        }

        if(filterManager.checkFilter(FilterAffiliationArea.CHAT_ADVERTISING,event.getMessage())){
            event.getOnlinePlayer().sendMessage(Messages.FILTER_BLOCKED_ADVERTISING);
            event.setCancelled(true);
            return FilterAffiliationArea.CHAT_ADVERTISING;
        }
        return null;
    }

    @Listener(priority = EventPriority.HIGHEST)//@Todo async
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

    private void sendMutedMessage(OnlineMinecraftPlayer player, PlayerHistoryEntry mute) {
        MessageComponent<?> message = mute.getCurrent().isPermanently()
                ? Messages.PUNISH_MUTE_MESSAGE_PERMANENTLY : Messages.PUNISH_MUTE_MESSAGE_TEMPORARY;
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
