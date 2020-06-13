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

import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DefaultPlayerNoteList implements PlayerNoteList {

    private final Collection<PlayerNote> cached;

    public DefaultPlayerNoteList() {
        this.cached = new ArrayList<>();
    }

    @Override
    public Collection<PlayerNote> getAll() {
        return this.cached;
    }

    @Override
    public Collection<PlayerNote> getLast(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getFirst(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getByIndex(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getByIndexRange(int startIndex, int endIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getSince(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getUntil(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getBetween(long startTime, long endTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<PlayerNote> iterator() {
        throw new UnsupportedOperationException();
    }
}
