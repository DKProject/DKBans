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

package net.pretronic.dkbans.common.player.note;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultPlayerNoteList implements PlayerNoteList {

    private final DKBansPlayer player;

    public DefaultPlayerNoteList(DKBansPlayer player) {
        this.player = player;
    }

    @Override
    public List<PlayerNote> getAll() {
        return DKBans.getInstance().getStorage().getPlayerNotes(player);
    }

    @Override
    public List<PlayerNote> getLast(int amount) {
        return DKBans.getInstance().getStorage().getLastPlayerNotes(player, amount);
    }

    @Override
    public PlayerNote getLast() {
        List<PlayerNote> notes = getLast(1);
        if(!notes.isEmpty()) return notes.get(0);
        return null;
    }

    @Override
    public List<PlayerNote> getFirst(int amount) {
        return DKBans.getInstance().getStorage().getFirstPlayerNotes(player, amount);
    }

    @Override
    public PlayerNote getByIndex(int index) {
        return DKBans.getInstance().getStorage().getPlayerNoteByIndex(player, index);
    }

    @Override
    public List<PlayerNote> getByIndexRange(int startIndex, int endIndex) {
        return DKBans.getInstance().getStorage().getPlayerNotesByIndexRange(player, startIndex, endIndex);
    }

    @Override
    public List<PlayerNote> getSince(long time) {
        return DKBans.getInstance().getStorage().getSincePlayerNotes(player, time);
    }

    @Override
    public List<PlayerNote> getUntil(long time) {
        return DKBans.getInstance().getStorage().getUntilPlayerNotes(player, time);
    }

    @Override
    public List<PlayerNote> getBetween(long startTime, long endTime) {
        return DKBans.getInstance().getStorage().getBetweenPlayerNotes(player, startTime, endTime);
    }
}
