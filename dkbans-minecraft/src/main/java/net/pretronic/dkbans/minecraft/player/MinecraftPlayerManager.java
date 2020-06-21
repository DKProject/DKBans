/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.06.20, 17:19
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

package net.pretronic.dkbans.minecraft.player;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.DKBansPlayerManager;
import net.pretronic.dkbans.common.player.DefaultDKBansPlayer;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.utility.Iterators;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MinecraftPlayerManager implements DKBansPlayerManager {

    private final Cache<DKBansPlayer> players;

    public MinecraftPlayerManager() {
        this.players = new ArrayCache<>();
        this.players.setMaxSize(1024);
        this.players.setExpireAfterAccess(10, TimeUnit.MINUTES);
    }

    @Override
    public DKBansPlayer getPlayer(UUID uniqueId) {
        return getPlayer(McNative.getInstance().getPlayerManager().getPlayer(uniqueId));
    }

    @Override
    public DKBansPlayer getPlayer(String name) {
        return getPlayer(McNative.getInstance().getPlayerManager().getPlayer(name));
    }

    public DKBansPlayer getPlayer(MinecraftPlayer player){//@Todo temporary
        if(player == null) return null;
        System.out.println("getPlayer:"+player.getUniqueId());
        System.out.println(Iterators.map(players.getCachedObjects(), DKBansPlayer::getUniqueId));
        System.out.println("---");
        DKBansPlayer player1 = players.get(player0 -> player0.getUniqueId().equals(player.getUniqueId()));
        if(player1 == null) {
            player1 = new DefaultDKBansPlayer(player.getUniqueId(), player.getName());
            this.players.insert(player1);
        }
        return player1;
    }
}
