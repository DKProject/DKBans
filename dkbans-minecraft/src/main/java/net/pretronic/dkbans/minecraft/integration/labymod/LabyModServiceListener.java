/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.02.21, 08:35
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

package net.pretronic.dkbans.minecraft.integration.labymod;

import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishEvent;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishUpdateEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.event.execution.ExecutionType;
import net.pretronic.libraries.event.network.NetworkListener;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;

public class LabyModServiceListener {

    @Listener(execution = ExecutionType.ASYNC)
    @NetworkListener(execution = ExecutionType.ASYNC)
    public void onPlayerLogin(MinecraftPlayerPostLoginEvent event){
        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);
        if(player.hasActivePunish(PunishmentType.MUTE)){
            LabyModIntegration.sendMutePlayer(McNative.getInstance().getLocal().getConnectedPlayers(),player.getUniqueId(),true);
        }
        for (ConnectedMinecraftPlayer target : McNative.getInstance().getLocal().getConnectedPlayers()) {
            System.out.println(target.getUniqueId()+" <- check");
            if(target.getAs(DKBansPlayer.class).hasActivePunish(PunishmentType.MUTE)){
                System.out.println("MUTED");
                LabyModIntegration.sendMutePlayer(event.getPlayer().getAsConnectedPlayer(),target.getUniqueId(),true);
            }
        }
    }

    @Listener(execution = ExecutionType.ASYNC)
    @NetworkListener(execution = ExecutionType.ASYNC)
    public void onPlayerPunish(DKBansPlayerPunishEvent event){
        if(event.getSnapshot().getPunishmentType().equals(PunishmentType.MUTE)){
            LabyModIntegration.sendMutePlayer(McNative.getInstance().getLocal().getConnectedPlayers(),event.getPlayerId(),true);
        }
    }

    @Listener(execution = ExecutionType.ASYNC)
    @NetworkListener(execution = ExecutionType.ASYNC)
    public void onPlayerPunishEdit(DKBansPlayerPunishUpdateEvent event){
        ConnectedMinecraftPlayer target = McNative.getInstance().getLocal().getConnectedPlayer(event.getPlayerId());
        if(target != null){
            boolean muted = target.getAs(DKBansPlayer.class).hasActivePunish(PunishmentType.MUTE);
            LabyModIntegration.sendMutePlayer(McNative.getInstance().getLocal().getConnectedPlayers(),event.getPlayerId(),muted);
        }
    }

}
