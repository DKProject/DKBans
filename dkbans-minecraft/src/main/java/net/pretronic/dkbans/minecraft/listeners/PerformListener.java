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
import net.pretronic.dkbans.api.event.DKBansChannelBroadcastMessageReceiveEvent;
import net.pretronic.dkbans.api.event.DKBansJoinMeCreateEvent;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishEvent;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishUpdateEvent;
import net.pretronic.dkbans.api.event.report.DKBansReportCreateEvent;
import net.pretronic.dkbans.api.event.report.DKBansReportStateChangedEvent;
import net.pretronic.dkbans.api.event.report.DKBansReportWatchEvent;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.minecraft.BroadcastMessageChannels;
import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.joinme.MinecraftJoinMe;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.event.execution.EventExecution;
import net.pretronic.libraries.event.network.NetworkListener;
import net.pretronic.libraries.message.MessageProvider;
import net.pretronic.libraries.message.bml.Message;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.api.text.components.MessageKeyComponent;

import java.util.List;

public class PerformListener {

    private final MessageProvider messageProvider;

    public PerformListener(){
        messageProvider = McNative.getInstance().getRegistry().getService(MessageProvider.class);
    }

    @Listener
    @NetworkListener
    public void onChannelBroadcastMessageReceive(DKBansChannelBroadcastMessageReceiveEvent event) {
        if(event.getChannel().equals(BroadcastMessageChannels.TEAM_CHAT)){
            MessageComponent<?> result = Messages.TEAMCHAT_MESSAGE_FORMAT;
            for (OnlineMinecraftPlayer staff : McNative.getInstance().getLocal().getOnlinePlayers()) {
                DKBansExecutor executor = event.getExecutor();
                if(staff.hasPermission(CommandConfig.PERMISSION_COMMAND_TEAMCHAT_RECEIVE)
                        && staff.hasSetting("DKBans", PlayerSettingsKey.TEAM_CHAT_LOGIN,true)){
                    staff.sendMessage(result,VariableSet.create()
                            .add("message",event.getMessage())
                            .addDescribed("sender",executor));
                }
            }
        }
    }

    @Listener
    @NetworkListener
    public void onJoinMeCreate(DKBansJoinMeCreateEvent event) {
        MinecraftJoinMe joinMe = ((MinecraftJoinMe)event.getJoinMe());
        List<MessageComponent<?>> messageComponents = joinMe.create();

        for (MessageComponent<?> messageComponent : messageComponents) {
            McNative.getInstance().getLocal().broadcast(messageComponent,VariableSet.create()
                    .add("server",event.getJoinMe().getServer())
                    .addDescribed("player",event.getJoinMe().getPlayer()));
        }
    }

    @Listener
    @NetworkListener
    public void onPlayerReportCreate(DKBansReportCreateEvent event) {
        for (ConnectedMinecraftPlayer player : McNative.getInstance().getLocal().getConnectedPlayers()) {
            if(player.hasPermission(CommandConfig.PERMISSION_COMMAND_REPORT_STAFF)) {
                player.sendMessage(Messages.REPORT_NOTIFY, VariableSet.create()
                        .addDescribed("player",event.getPlayer())
                        .addDescribed("report", event.getReportEntry()));
            }
        }
    }

    @Listener
    @NetworkListener
    public void onPlayerReportWatch(DKBansReportWatchEvent event) {
        OnlineMinecraftPlayer watcher = McNative.getInstance().getLocal().getOnlinePlayer(event.getReport().getWatcherId());
        if(watcher == null) return;
        OnlineMinecraftPlayer target = McNative.getInstance().getNetwork().getOnlinePlayer(event.getReport().getPlayerId());
        if(target != null && !watcher.getServer().getName().equalsIgnoreCase(target.getServer().getName())){
            watcher.connect(target.getServer());
        }
    }

    @Listener
    @NetworkListener
    public void onPlayerReportStateChange(DKBansReportStateChangedEvent event) {
        MessageComponent<?> message = event.getNewState() == ReportState.ACCEPTED ? Messages.REPORT_ACCEPTED : Messages.REPORT_DECLINED;
        for (PlayerReportEntry entry : event.getReport().getEntries()) {
            OnlineMinecraftPlayer player = McNative.getInstance().getLocal().getOnlinePlayer(entry.getReporterId());
            if(player != null){
                player.sendMessage(message,VariableSet.create().addDescribed("report",event.getReport()));
            }
        }
    }

    @Listener
    @NetworkListener
    public void onPlayerPunish(DKBansPlayerPunishEvent event){
        ConnectedMinecraftPlayer player = McNative.getInstance().getLocal().getConnectedPlayer(event.getPlayerId());
        if(player != null) executePunishMessage(player,event.getEntry(),event.getSnapshot());
        sendPunishNotification(event);
    }

    @Listener
    @NetworkListener
    public void onPlayerPunishUpdate(DKBansPlayerPunishUpdateEvent event){
        OnlineMinecraftPlayer player = McNative.getInstance().getLocal().getConnectedPlayer(event.getPlayer().getUniqueId());
        if(player != null){
            if(event.getNewSnapshot().getPunishmentType().equals(PunishmentType.BAN)){
                MessageComponent<?> message = event.getNewSnapshot().isPermanently()
                        ? Messages.PUNISH_MESSAGE_BAN_PERMANENTLY : Messages.PUNISH_MESSAGE_BAN_TEMPORARY;

                player.kick(message, VariableSet.create()
                        .addDescribed("ban", event.getEntry())
                        .addDescribed("punish", event.getEntry())
                        .addDescribed("player", event.getPlayer()));
            }
        }
        sendToStaff(Messages.PUNISH_NOTIFY_EDIT, VariableSet.create()
                .addDescribed("player",event.getPlayer())
                .addDescribed("entry",event.getEntry())
                .addDescribed("oldSnapshot",event.getOldSnapshot())
                .addDescribed("newSnapshot",event.getNewSnapshot()));
    }

    private void executePunishMessage(ConnectedMinecraftPlayer player, PlayerHistoryEntry entry, PlayerHistoryEntrySnapshot snapshot){
        MessageComponent<?> message = null;
        if(snapshot.getTemplateId() >= 0){
            Template template = snapshot.getTemplate();
            if(template instanceof PunishmentTemplate && ((PunishmentTemplate) template).getCustomMessageKey() != null){
                String key = ((PunishmentTemplate)template).getCustomMessageKey();
                Message raw = messageProvider.getMessage(key+"."+snapshot.getPunishmentType().getName().toLowerCase());
                if(raw != null) message = new MessageKeyComponent(raw);
            }
        }
        if(message == null){
            if(snapshot.getPunishmentType().equals(PunishmentType.BAN)){
                if(snapshot.isPermanently()) message = Messages.PUNISH_MESSAGE_BAN_PERMANENTLY;
                else message = Messages.PUNISH_MESSAGE_BAN_TEMPORARY;
            }else if(snapshot.getPunishmentType().equals(PunishmentType.MUTE)){
                if(snapshot.isPermanently()) message = Messages.PUNISH_MESSAGE_MUTE_PERMANENTLY;
                else message = Messages.PUNISH_MESSAGE_MUTE_TEMPORARY;
            }else if(snapshot.getPunishmentType().equals(PunishmentType.WARN)){
                message = Messages.PUNISH_MESSAGE_WARN_CHAT;
            }else if(snapshot.getPunishmentType().equals(PunishmentType.KICK)){
                message = Messages.PUNISH_MESSAGE_KICK;
            }else{
                DKBans.getInstance().getLogger().error("Punish message is null");
                return;
            }
        }

        VariableSet variables = VariableSet.create()
                .addDescribed(snapshot.getPunishmentType().getName().toLowerCase(), entry)
                .addDescribed("punish", snapshot)
                .addDescribed("player", player);

        boolean kick = snapshot.getPunishmentType().equals(PunishmentType.KICK) || snapshot.getPunishmentType().equals(PunishmentType.BAN);
        if(kick) player.kick(message, variables);
        else player.sendMessage(message, variables);
    }

    private void sendPunishNotification(DKBansPlayerPunishEvent event) {
        MessageComponent<?> message;

        if(event.getSnapshot().getPunishmentType().equals(PunishmentType.BAN)) message = Messages.PUNISH_NOTIFY_BAN;
        else if(event.getSnapshot().getPunishmentType().equals(PunishmentType.MUTE)) message = Messages.PUNISH_NOTIFY_MUTE;
        else if(event.getSnapshot().getPunishmentType().equals(PunishmentType.KICK)) message = Messages.PUNISH_NOTIFY_KICK;
        else if(event.getSnapshot().getPunishmentType().equals(PunishmentType.WARN)) message = Messages.PUNISH_NOTIFY_WARN;
        else return;//Unsupported

        sendToStaff(message, VariableSet.create()
                .addDescribed("player",event.getEntry().getHistory().getPlayer())
                .addDescribed("punish", event.getEntry())
                .addDescribed(event.getSnapshot().getPunishmentType().getName().toLowerCase(), event.getEntry()));
    }

    private void sendToStaff(MessageComponent<?> notification, VariableSet variables) {
        for (OnlineMinecraftPlayer staff : McNative.getInstance().getLocal().getOnlinePlayers()) {
            if (staff.hasPermission(CommandConfig.PERMISSION_PUNISH_NOTIFY)
                    && staff.hasSetting("DKBans", PlayerSettingsKey.PUNISH_NOTIFY_LOGIN, true)) {
                staff.sendMessage(notification, variables);
            }
        }
    }
}
