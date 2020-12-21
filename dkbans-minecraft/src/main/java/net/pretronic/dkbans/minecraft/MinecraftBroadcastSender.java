/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.12.20, 13:12
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

package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.broadcast.Broadcast;
import net.pretronic.dkbans.api.broadcast.BroadcastAssignment;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.common.broadcast.BroadcastSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.common.McNative;
import org.mcnative.common.player.OnlineMinecraftPlayer;
import org.mcnative.common.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class MinecraftBroadcastSender implements BroadcastSender {


    @Override
    public Collection<DKBansPlayer> sendBroadcast(Broadcast broadcast) {
        return sendBroadcast(onlinePlayer -> {
            onlinePlayer.sendMessage(Text.of(broadcast.getText()), VariableSet.create().addDescribed("player", onlinePlayer));
            return true;
        });
    }

    @Override
    public Collection<DKBansPlayer> sendBroadcast(BroadcastAssignment broadcast) {
        return sendBroadcast(onlinePlayer -> {
            if(onlinePlayer.hasPermission(broadcast.getGroup().getPermission())) {
                onlinePlayer.sendMessage(Text.of(broadcast.getBroadcast().getText()), VariableSet.create().addDescribed("player", onlinePlayer));
                return true;
            }
            return false;
        });
    }

    private Collection<DKBansPlayer> sendBroadcast(Predicate<OnlineMinecraftPlayer> playerConsumer) {
        Collection<DKBansPlayer> sent = new ArrayList<>();

        for (OnlineMinecraftPlayer onlinePlayer : McNative.getInstance().getNetwork().getOnlinePlayers()) {
            if(playerConsumer.test(onlinePlayer)) sent.add(DKBans.getInstance().getPlayerManager().getPlayer(onlinePlayer.getUniqueId()));
        }

        return sent;
    }
}
