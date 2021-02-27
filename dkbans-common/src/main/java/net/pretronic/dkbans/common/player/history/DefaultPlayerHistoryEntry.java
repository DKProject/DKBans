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

package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplate;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class DefaultPlayerHistoryEntry implements PlayerHistoryEntry {

    private final int id;
    private final UUID playerId;
    private final long created;

    private transient PlayerHistory history;
    private transient PlayerHistoryEntrySnapshot current;
    private transient List<PlayerHistoryEntrySnapshot> snapshots;

    public DefaultPlayerHistoryEntry(int id, UUID playerId, long created, PlayerHistory history
            ,PlayerHistoryEntrySnapshot current,List<PlayerHistoryEntrySnapshot> snapshots) {
        this.id = id;
        this.playerId = playerId;
        this.created = created;
        this.history = history;
        this.current = current;
        this.snapshots = snapshots;
        if(current != null && snapshots != null){
            snapshots.add(current);
        }
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
        if(history == null) history = DKBans.getInstance().getPlayerManager().getPlayer(this.playerId).getHistory();
        return history;
    }

    @Override
    public int getSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isActive() {
        return getCurrent().isActive() && !hasTimeout();
    }

    @Override
    public boolean hasTimeout() {
        if(getCurrent().getTimeout() < 0) return false;
        return getCurrent().getTimeout() < System.currentTimeMillis();
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
    public PlayerHistoryEntrySnapshot get(int id) {
        return Iterators.findOne(getAll(), snapshot -> snapshot.getId() == id);
    }

    @Override
    public List<PlayerHistoryEntrySnapshot> getAll() {
        if(snapshots == null){
            snapshots = DKBans.getInstance().getStorage().loadSnapshots(this);
        }
        return snapshots;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder newSnapshot(DKBansExecutor executor) {
        Validate.notNull(executor);
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
    public PlayerHistoryEntrySnapshot unpunish(DKBansExecutor executor, UnPunishmentTemplate template) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerHistoryEntrySnapshot unpunish(DKBansExecutor executor, String reason) {
        return newSnapshot(executor).active(false)
                .revokeReason(reason).execute();
    }

    @Override
    public Iterator<PlayerHistoryEntrySnapshot> iterator() {
        return getAll().iterator();
    }

    @Internal
    public void setCurrent(PlayerHistoryEntrySnapshot current){
        Validate.notNull(current);
        this.current = current;
        if(snapshots != null) snapshots.add(current);
    }
}
