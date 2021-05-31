/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 23.04.21, 17:34
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

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.runtime.api.proxy.event.player.MinecraftPlayerServerConnectEvent;
import org.mcnative.runtime.api.text.components.MessageComponent;

public class ProxyListener {

    @Listener(priority = EventPriority.LOWEST)
    public void onServerConnect(MinecraftPlayerServerConnectEvent event){
        if(event.isCancelled() || event.getTarget() == null) return;

        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        PlayerHistoryEntry ban = player.getHistory().getActiveEntry(PunishmentType.BAN,DKBansScope.ofServer(event.getTarget().getName()));
        if(ban == null){
            ban = player.getHistory().getActiveEntry(PunishmentType.BAN
                    , DKBansScope.ofServerGroup(event.getTarget().getGroup()));
        }

        if(ban != null){
            event.setCancelled(true);
            MessageComponent<?> message = ban.getCurrent().isPermanently()
                    ? Messages.PUNISH_MESSAGE_BAN_PERMANENTLY : Messages.PUNISH_MESSAGE_BAN_TEMPORARY;

            event.getOnlinePlayer().sendMessage(message, VariableSet.create()
                    .addDescribed("ban",ban)
                    .addDescribed("punish",ban)
                    .addDescribed("player",event.getPlayer()));
        }
    }

}
