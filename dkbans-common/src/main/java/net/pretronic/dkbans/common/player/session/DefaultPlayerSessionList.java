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

package net.pretronic.dkbans.common.player.session;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.api.player.session.PlayerSessionList;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.List;

public class DefaultPlayerSessionList implements PlayerSessionList {

    private final DKBansPlayer player;
    private PlayerSession active;

    public DefaultPlayerSessionList(DKBansPlayer player) {
        this.player = player;
    }

    @Override
    public List<PlayerSession> getAll() {
        return DKBans.getInstance().getStorage().getPlayerSessions(player);
    }

    @Override
    public List<PlayerSession> getLast(int amount) {
        return DKBans.getInstance().getStorage().getLastPlayerSessions(player, amount);
    }

    @Override
    public PlayerSession getLast() {
        List<PlayerSession> last = getLast(1);
        if(!last.isEmpty()) return last.get(0);
        return null;
    }

    @Override
    public List<PlayerSession> getFirst(int amount) {
        return DKBans.getInstance().getStorage().getFirstPlayerSessions(player, amount);
    }

    @Override
    public PlayerSession getByIndex(int index) {
        return DKBans.getInstance().getStorage().getPlayerSessionByIndex(player, index);
    }

    @Override
    public List<PlayerSession> getByIndexRange(int startIndex, int endIndex) {
        return DKBans.getInstance().getStorage().getPlayerSessionByIndexRange(player, startIndex, endIndex);
    }

    @Override
    public List<PlayerSession> getSince(long time) {
        return DKBans.getInstance().getStorage().getSincePlayerSessions(player, time);
    }

    @Override
    public List<PlayerSession> getUntil(long time) {
        return DKBans.getInstance().getStorage().getUntilPlayerSessions(player, time);
    }

    @Override
    public List<PlayerSession> getBetween(long startTime, long endTime) {
        return DKBans.getInstance().getStorage().getBetweenPlayerSessions(player, startTime, endTime);
    }

    @Override
    public List<PlayerSession> getPage(int page, int sizePerPage) {
        return DKBans.getInstance().getStorage().getPageSessions(player,page,sizePerPage);
    }

    @Override
    public PlayerSession getActive() {
        if(active == null){
            return DKBans.getInstance().getStorage().getActiveSession(player);
        }
        return this.active;
    }

    @Internal
    public void setActive(PlayerSession session) {
        this.active = session;
    }
}
