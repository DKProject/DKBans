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

package net.pretronic.dkbans.minecraft.player;

import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.DKBansPlayerManager;
import net.pretronic.dkbans.api.player.OnlineTimeTopResult;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.player.DefaultDKBansPlayer;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.caching.ShadowArrayCache;
import net.pretronic.libraries.caching.synchronisation.ShadowArraySynchronizableCache;
import net.pretronic.libraries.caching.synchronisation.SynchronizableCache;
import net.pretronic.libraries.synchronisation.SynchronisationCaller;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MinecraftPlayerManager implements DKBansPlayerManager {

    private final SynchronizableCache<DKBansPlayer,UUID> players;
    private final Collection<DKBansExecutor> specialExecutors;

    public MinecraftPlayerManager() {
        this.players = new ShadowArraySynchronizableCache<>();
        this.players.setMaxSize(1024);
        this.players.setExpireAfterAccess(10, TimeUnit.MINUTES);
        this.players.registerQuery("get", new PlayerGetter());
        this.players.setClearOnDisconnect(true);
        this.players.setSkipOnDisconnect(true);

        this.specialExecutors = new ArrayList<>();
        this.specialExecutors.add(DKBansExecutor.CONSOLE);
        this.specialExecutors.add(DKBansExecutor.IP_ADDRESS_BLOCK);
    }

    public SynchronizableCache<DKBansPlayer,UUID> getPlayerCache() {
        return players;
    }

    @Override
    public DKBansPlayer getPlayer(UUID uniqueId) {
        Validate.notNull(uniqueId);
        return this.players.get("get", uniqueId);
    }

    @Override
    public DKBansPlayer getPlayer(String name) {
        Validate.notNull(name);
        return this.players.get("get", name);
    }

    @Override
    public Collection<DKBansPlayer> getPlayers(String address) {
        QueryResult result = DefaultDKBans.getInstance().getStorage().getPlayerSessions().find()
                .get("PlayerId")
                .where("ipAddress",address)
                .groupBy("PlayerId").execute();
        Collection<DKBansPlayer> players = new ArrayList<>();

        for (QueryResultEntry entry : result) {
            players.add(getPlayer(entry.getUniqueId("PlayerId")));
        }

        return players;
    }

    @Override
    public DKBansExecutor getExecutor(UUID uniqueId) {
        DKBansExecutor executor = Iterators.findOne(this.specialExecutors, o -> o.getUniqueId().equals(uniqueId));
        if(executor != null) return executor;
        return getPlayer(uniqueId);
    }

    @Override
    public DKBansExecutor getExecutor(String name) {
        DKBansExecutor executor = Iterators.findOne(this.specialExecutors, o -> o.getName().equalsIgnoreCase(name));
        if(executor != null) return executor;
        return getPlayer(name);
    }

    @Override
    public List<OnlineTimeTopResult> getTopOnlineTime(int page, int pageSize) {
        return DKBans.getInstance().getStorage().getTopOnlineTime(page,pageSize);
    }

    @Override
    public void registerSpecialExecutor(DKBansExecutor executor) {
        Validate.notNull(executor);
        if(Iterators.findOne(this.specialExecutors, o -> o.getUniqueId().equals(executor.getUniqueId())) != null){
            throw new IllegalArgumentException("Special executor already registered");
        }
        this.specialExecutors.add(executor);
    }

    @Override
    public Collection<DKBansPlayer> getLoadedPlayers() {
        return players.getCachedObjects();
    }

    @Override
    public DKBansPlayer getLoadedPlayer(UUID playerId) {
        return players.get(player -> player.getUniqueId().equals(playerId));
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
            if(player == null) return null;
            return new DefaultDKBansPlayer(player.getUniqueId(), player.getName());
        }
    }
}
