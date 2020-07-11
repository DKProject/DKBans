/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.07.20, 11:35
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

package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PunishmentList;
import net.pretronic.dkbans.api.player.history.PunishmentType;

import java.util.List;

public class DefaultPunishmentList implements PunishmentList {

    private final PunishmentType type;

    public DefaultPunishmentList(PunishmentType type) {
        this.type = type;
    }

    @Override
    public List<PlayerHistoryEntry> getAll() {
        return DKBans.getInstance().getStorage().getActiveEntries(type);
    }

    @Override
    public List<PlayerHistoryEntry> getLast(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerHistoryEntry getLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PlayerHistoryEntry> getFirst(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerHistoryEntry getByIndex(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PlayerHistoryEntry> getByIndexRange(int startIndex, int endIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PlayerHistoryEntry> getSince(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PlayerHistoryEntry> getUntil(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PlayerHistoryEntry> getBetween(long startTime, long endTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<PlayerHistoryEntry> getPage(int page, int sizePerPage) {
        return DKBans.getInstance().getStorage().getActiveEntriesOnPage(type,page,sizePerPage);
    }
}
