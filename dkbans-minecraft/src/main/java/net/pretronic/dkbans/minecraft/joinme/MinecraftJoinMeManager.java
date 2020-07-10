/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.07.20, 20:19
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

package net.pretronic.dkbans.minecraft.joinme;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.event.DKBansJoinMeCreateEvent;
import net.pretronic.dkbans.api.joinme.JoinMe;
import net.pretronic.dkbans.api.joinme.JoinMeManager;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.common.event.DefaultDKBansJoinMeCreateEvent;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class MinecraftJoinMeManager implements JoinMeManager {

    private final Collection<JoinMe> joinMes;

    public MinecraftJoinMeManager() {
        this.joinMes = new ArrayList<>();
    }

    @Override
    public JoinMe getJoinMe(DKBansPlayer player) {
        return Iterators.findOne(joinMes, joinMe -> joinMe.getPlayer().equals(player));
    }

    @Override
    public JoinMe getJoinMe(UUID playerId) {
        return Iterators.findOne(joinMes, joinMe -> joinMe.getPlayer().getUniqueId().equals(playerId));
    }

    @Override
    public JoinMe sendJoinMe(DKBansPlayer player, String server, long timeout) {
        MinecraftJoinMe joinMe = new MinecraftJoinMe(player, server, timeout);
        DKBans.getInstance().getEventBus().callEvent(DKBansJoinMeCreateEvent.class,new DefaultDKBansJoinMeCreateEvent(joinMe));
        joinMes.add(joinMe);
        return joinMe;
    }
}
