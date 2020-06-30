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
import net.pretronic.dkbans.api.event.DKBansPlayerPunishEvent;
import net.pretronic.dkbans.api.event.DKBansPlayerReportTeleportEvent;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.BroadcastMessageChannels;
import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.common.McNative;
import org.mcnative.common.network.event.NetworkListener;
import org.mcnative.common.player.OnlineMinecraftPlayer;
import org.mcnative.common.text.components.MessageComponent;

public class InternalListener {

    @NetworkListener
    public void onChannelBroadcastMessageReceive(DKBansChannelBroadcastMessageReceiveEvent event) {
        if(event.getChannel().equals(BroadcastMessageChannels.TEAM_CHAT)){
            String message = event.getMessage();

        }
    }

    @NetworkListener
    public void onPlayerPunish(DKBansPlayerPunishEvent event){
        OnlineMinecraftPlayer player = McNative.getInstance().getLocal().getConnectedPlayer(event.getPlayer().getUniqueId());
        if(event.getSnapshot().getPunishmentType() == PunishmentType.BAN) handleBan(event, player);
        else if(event.getSnapshot().getPunishmentType() == PunishmentType.MUTE) handleMute(event, player);
    }

    private void handleMute(DKBansPlayerPunishEvent event, OnlineMinecraftPlayer player) {
        if(player != null){
            MessageComponent<?> message = event.getSnapshot().isPermanently()
                    ? Messages.PUNISH_MUTE_MESSAGE_PERMANENTLY : Messages.PUNISH_MUTE_MESSAGE_TEMPORARY;
            player.sendMessage(message,VariableSet.create()
                    .addDescribed("mute",event.getEntry())
                    .addDescribed("punish",event.getEntry())
                    .addDescribed("player",event.getPlayer()));
        }

        for (OnlineMinecraftPlayer staff : McNative.getInstance().getLocal().getOnlinePlayers()) {
            if(staff.hasPermission(Permissions.PUNISH_NOTIFY)
                    && staff.hasSetting("DKBans", PlayerSettingsKey.PUNISH_NOTIFY_LOGIN,true)){
                staff.sendMessage(Messages.PUNISH_MUTE_NOTIFY,VariableSet.create()
                        .addDescribed("mute",event.getEntry()));
            }
        }
    }

    private void handleBan(DKBansPlayerPunishEvent event, OnlineMinecraftPlayer player) {
        if (player != null) {
            MessageComponent<?> message = event.getSnapshot().isPermanently()
                    ? Messages.PUNISH_BAN_MESSAGE_PERMANENTLY : Messages.PUNISH_BAN_MESSAGE_TEMPORARY;
            player.kick(message, VariableSet.create()
                    .addDescribed("ban", event.getEntry())
                    .addDescribed("punish", event.getEntry())
                    .addDescribed("player", event.getPlayer()));

            for (OnlineMinecraftPlayer staff : McNative.getInstance().getLocal().getOnlinePlayers()) {
                if(staff.hasPermission(Permissions.PUNISH_NOTIFY)
                        && staff.hasSetting("DKBans", PlayerSettingsKey.PUNISH_NOTIFY_LOGIN,true)){
                    staff.sendMessage(Messages.PUNISH_BAN_NOTIFY,VariableSet.create()
                            .addDescribed("ban",event.getEntry()));
                }
            }
        }
    }

    @Listener
    public void onPlayerReport(DKBansPlayerReportTeleportEvent event) {
        OnlineMinecraftPlayer player = McNative.getInstance().getLocal().getOnlinePlayer(event.getPlayer().getUniqueId());
        OnlineMinecraftPlayer target = McNative.getInstance().getLocal().getOnlinePlayer(event.getReport().getPlayer().getUniqueId());
        player.connect(target.getServer());
    }
}
