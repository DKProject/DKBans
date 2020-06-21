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

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.DKBansPlayerManager;
import net.pretronic.dkbans.common.player.DefaultDKBansPlayer;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.UUID;

public class MinecraftPlayerManager implements DKBansPlayerManager {

    private final Cache<DKBansPlayer> players;

    public MinecraftPlayerManager() {
        this.players = new ArrayCache<>();
        this.players.setMaxSize(1024);
        //this.players.setExpireAfterAccess(10, TimeUnit.MINUTES);
        this.players.registerQuery("get", new PlayerGetter());
    }

    @Override
    public DKBansPlayer getPlayer(UUID uniqueId) {
        Validate.notNull(uniqueId);
        System.out.println("getPlayer:"+uniqueId);
        System.out.println(Iterators.map(players.getCachedObjects(), DKBansPlayer::getUniqueId));
        System.out.println("---");
        return this.players.get("get", uniqueId);
    }

    @Override
    public DKBansPlayer getPlayer(String name) {
        Validate.notNull(name);
        System.out.println("getPlayer:"+name);
        System.out.println(Iterators.map(players.getCachedObjects(), DKBansPlayer::getUniqueId));
        System.out.println("---");
        return this.players.get("get", name);
    }

    private static class PlayerGetter implements CacheQuery<DKBansPlayer> {

        @Override
        public boolean check(DKBansPlayer player, Object[] objects) {
            Object identifier = objects[0];
            if(identifier instanceof String) return player.getName().equalsIgnoreCase((String) identifier);
            return player.getUniqueId().equals(identifier);
        }

        @Override
        public void validate(Object[] identifiers) {
            Validate.isTrue(identifiers.length == 1 && (identifiers[0] instanceof UUID || identifiers[0] instanceof String));
        }

        @Override
        public DKBansPlayer load(Object[] identifiers) {
            Object identifier = identifiers[0];
            MinecraftPlayer player;
            if(identifier instanceof String) player = McNative.getInstance().getPlayerManager().getPlayer((String) identifier);
            else player = McNative.getInstance().getPlayerManager().getPlayer((UUID) identifier);
            return new DefaultDKBansPlayer(player.getUniqueId(), player.getName());
        }
    }
}
