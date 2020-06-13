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

package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.Iterator;
import java.util.List;

public class DefaultPlayerHistoryEntry implements PlayerHistoryEntry {

    private final PlayerHistory history;
    private final int id;
    private final long created;

    private PlayerHistoryEntrySnapshot current;
    private List<PlayerHistoryEntrySnapshot> snapshots;

    public DefaultPlayerHistoryEntry(PlayerHistory history, int id, PlayerHistoryEntrySnapshot current, long created) {
        this.history = history;
        this.id = id;
        this.created = created;
        this.current = current;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public long getCreated() {
        return this.created;
    }

    @Override
    public PlayerHistory getHistory() {
        return history;
    }

    @Override
    public PlayerSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerHistoryEntrySnapshot getCurrent() {
        return current;
    }

    @Override
    public PlayerHistoryEntrySnapshot getFirst() {
        return getAll().get(0);
    }

    @Override
    public List<PlayerHistoryEntrySnapshot> getAll() {
        if(snapshots == null){
            //@Todo load
        }
        return snapshots;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder newSnapshot(DKBansExecutor executor) {
        return new DefaultPlayerHistoryEntrySnapshotBuilder(history.getPlayer(),this);
    }

    @Override
    public PlayerNoteList getNotes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerNote createNote(DKBansExecutor creator, String message, PlayerNoteType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<PlayerHistoryEntrySnapshot> iterator() {
        return getAll().iterator();
    }

    @Internal
    public void setCurrent(PlayerHistoryEntrySnapshot current){
        this.current = current;
    }
}
