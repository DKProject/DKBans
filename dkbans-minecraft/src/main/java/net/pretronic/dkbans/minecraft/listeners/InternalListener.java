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

import net.pretronic.dkbans.api.event.DKBansChannelBroadcastMessageReceiveEvent;
import net.pretronic.dkbans.api.event.DKBansJoinMeCreateEvent;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishEvent;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishUpdateEvent;
import net.pretronic.dkbans.api.event.report.DKBansPlayerReportAcceptEvent;
import net.pretronic.dkbans.api.event.report.DKBansPlayerReportCreateEvent;
import net.pretronic.dkbans.api.event.report.DKBansPlayerReportDeclineEvent;
import net.pretronic.dkbans.api.event.report.DKBansPlayerReportTakeEvent;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.minecraft.BroadcastMessageChannels;
import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.dkbans.minecraft.integration.LabyModIntegration;
import net.pretronic.dkbans.minecraft.joinme.MinecraftJoinMe;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.event.network.NetworkListener;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.util.List;

public class InternalListener {

    @NetworkListener
    public void onChannelBroadcastMessageReceive(DKBansChannelBroadcastMessageReceiveEvent event) {
        if(event.getChannel().equals(BroadcastMessageChannels.TEAM_CHAT)){
            MessageComponent<?> result = Messages.TEAMCHAT_MESSAGE_FORMAT;
            for (OnlineMinecraftPlayer staff : McNative.getInstance().getLocal().getOnlinePlayers()) {
                if(staff.hasPermission(Permissions.TEAM)
                        && staff.hasSetting("DKBans", PlayerSettingsKey.TEAM_CHAT_LOGIN,true)){
                    staff.sendMessage(result,VariableSet.create()
                            .add("message",event.getMessage())
                            .addDescribed("sender",event.getExecutor()));
                }
            }
        }
    }

    @Listener
    public void onPlayerReportCreate(DKBansPlayerReportCreateEvent event) {
        for (ConnectedMinecraftPlayer player : McNative.getInstance().getLocal().getConnectedPlayers()) {
            if(player.hasPermission(Permissions.COMMAND_REPORT_STUFF)) {
                player.sendMessage(Messages.REPORT_NOTIFY, VariableSet.create()
                        .addDescribed("player",event.getPlayer())
                        .addDescribed("report", event.getReportEntry()));
            }
        }
    }

    @Listener
    public void onPlayerReportTake(DKBansPlayerReportTakeEvent event) {
        OnlineMinecraftPlayer watcher = McNative.getInstance().getLocal().getOnlinePlayer(event.getReport().getWatcherId());
        if(watcher == null) return;
        OnlineMinecraftPlayer target = McNative.getInstance().getNetwork().getOnlinePlayer(event.getReport().getPlayerId());
        if(target != null && !watcher.getServer().getName().equalsIgnoreCase(target.getName())){
            watcher.connect(target.getServer());
        }
    }

    @Listener
    public void onPlayerReportAccept(DKBansPlayerReportAcceptEvent event) {
        for (PlayerReportEntry entry : event.getReport().getEntries()) {
            OnlineMinecraftPlayer player = McNative.getInstance().getLocal().getOnlinePlayer(entry.getReporterId());
            if(player != null){
                player.sendMessage(Messages.REPORT_ACCEPTED,VariableSet.create()
                        .addDescribed("report",event.getReport()));
            }
        }
    }

    @Listener
    public void onPlayerReportDecline(DKBansPlayerReportDeclineEvent event) {
        for (PlayerReportEntry entry : event.getReport().getEntries()) {
            OnlineMinecraftPlayer player = McNative.getInstance().getLocal().getOnlinePlayer(entry.getReporterId());
            if(player != null){
                player.sendMessage(Messages.REPORT_DECLINED,VariableSet.create()
                        .addDescribed("report",event.getReport()));
            }
        }
    }

    @Listener
    public void onPlayerPunish(DKBansPlayerPunishEvent event){
        ConnectedMinecraftPlayer player = McNative.getInstance().getLocal().getConnectedPlayer(event.getPlayer().getUniqueId());
        if(player == null) return;
        if(event.getSnapshot().getPunishmentType() == PunishmentType.BAN) handleBan(event, player);
        else if(event.getSnapshot().getPunishmentType() == PunishmentType.MUTE) handleMute(event, player);
        else if(event.getSnapshot().getPunishmentType() == PunishmentType.KICK) handleKick(event, player);
        else if(event.getSnapshot().getPunishmentType() == PunishmentType.WARN) handleWarn(event, player);
    }

    @Listener
    public void onPlayerPunishUpdate(DKBansPlayerPunishUpdateEvent event){
        OnlineMinecraftPlayer player = McNative.getInstance().getLocal().getConnectedPlayer(event.getPlayer().getUniqueId());
        if(player == null) return;
        if(event.getNewSnapshot().getPunishmentType() == PunishmentType.BAN){
            MessageComponent<?> message = event.getNewSnapshot().isPermanently()
                    ? Messages.PUNISH_BAN_MESSAGE_PERMANENTLY : Messages.PUNISH_BAN_MESSAGE_TEMPORARY;

            player.kick(message, VariableSet.create()
                    .addDescribed("ban", event.getEntry())
                    .addDescribed("punish", event.getEntry())
                    .addDescribed("player", event.getPlayer()));
        }
    }

    @Listener
    public void onJoinMeCreate(DKBansJoinMeCreateEvent event) {
        MinecraftJoinMe joinMe = ((MinecraftJoinMe)event.getJoinMe());
        List<MessageComponent<?>> messageComponents = joinMe.create();

        for (MessageComponent<?> messageComponent : messageComponents) {
            McNative.getInstance().getLocal().broadcast(messageComponent,VariableSet.create()
                    .add("server",event.getJoinMe().getServer())
                    .addDescribed("player",event.getJoinMe().getPlayer()));
        }
    }

    private void handleMute(DKBansPlayerPunishEvent event, ConnectedMinecraftPlayer player) {
        if(player != null){
            MessageComponent<?> message = event.getSnapshot().isPermanently()
                    ? Messages.PUNISH_MUTE_MESSAGE_PERMANENTLY : Messages.PUNISH_MUTE_MESSAGE_TEMPORARY;
            player.sendMessage(message,VariableSet.create()
                    .addDescribed("mute",event.getEntry())
                    .addDescribed("punish",event.getEntry())
                    .addDescribed("player",event.getPlayer()));

            for (ConnectedMinecraftPlayer onlinePlayer : McNative.getInstance().getLocal().getConnectedPlayers()) {
                LabyModIntegration.sendMutedPlayerTo(onlinePlayer,player.getUniqueId(),true);
            }
        }

        sendToStaff(event, Messages.PUNISH_MUTE_NOTIFY, "mute");
    }

    private void handleBan(DKBansPlayerPunishEvent event, ConnectedMinecraftPlayer player) {
        if (player != null) {
            MessageComponent<?> message = event.getSnapshot().isPermanently()
                    ? Messages.PUNISH_BAN_MESSAGE_PERMANENTLY : Messages.PUNISH_BAN_MESSAGE_TEMPORARY;

            player.kick(message, VariableSet.create()
                    .addDescribed("ban", event.getEntry())
                    .addDescribed("punish", event.getEntry())
                    .addDescribed("player", event.getPlayer()));

        }
        sendToStaff(event, Messages.PUNISH_BAN_NOTIFY, "ban");
    }

    private void handleKick(DKBansPlayerPunishEvent event, ConnectedMinecraftPlayer player) {
        if (player != null) {
            player.kick(Messages.PUNISH_KICK_MESSAGE, VariableSet.create()
                    .addDescribed("kick", event.getEntry())
                    .addDescribed("punish", event.getEntry())
                    .addDescribed("player", event.getPlayer()));
        }
        sendToStaff(event, Messages.PUNISH_KICK_NOTIFY, "kick");
    }

    private void handleWarn(DKBansPlayerPunishEvent event, ConnectedMinecraftPlayer player) {
        if (player != null) {
            player.sendMessage(Messages.PUNISH_WARN_MESSAGE_CHAT, VariableSet.create()
                    .addDescribed("warn", event.getEntry())
                    .addDescribed("punish", event.getEntry())
                    .addDescribed("player", event.getPlayer()));
        }
        sendToStaff(event, Messages.PUNISH_WARN_NOTIFY, "kick");
    }

    private void sendToStaff(DKBansPlayerPunishEvent event, MessageComponent<?> punishBanNotify, String ban) {
        for (OnlineMinecraftPlayer staff : McNative.getInstance().getLocal().getOnlinePlayers()) {
            if (staff.hasPermission(Permissions.PUNISH_NOTIFY)
                    && staff.hasSetting("DKBans", PlayerSettingsKey.PUNISH_NOTIFY_LOGIN, true)) {
                staff.sendMessage(punishBanNotify, VariableSet.create()
                        .addDescribed("player",event.getEntry().getHistory().getPlayer())
                        .addDescribed(ban, event.getEntry()));
            }
        }
    }
}
