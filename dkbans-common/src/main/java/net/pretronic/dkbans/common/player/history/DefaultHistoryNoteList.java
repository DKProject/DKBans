/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 23.03.21, 18:50
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

import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.player.note.DefaultPlayerNote;

import java.util.ArrayList;
import java.util.List;

public class DefaultHistoryNoteList implements PlayerNoteList {

    private final DefaultPlayerHistoryEntry entry;

    public DefaultHistoryNoteList(DefaultPlayerHistoryEntry entry) {
        this.entry = entry;
    }

    @Override
    public List<PlayerNote> getAll() {
        return getPlayerNotesByResult(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .execute());
    }

    @Override
    public List<PlayerNote> getLast(int amount) {
        return getPlayerNotesByResult(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .orderBy("Time", SearchOrder.DESC)
                .limit(amount)
                .execute());
    }

    @Override
    public PlayerNote getLast() {
        List<PlayerNote> notes = getLast(1);
        if(!notes.isEmpty()) return notes.get(0);
        return null;
    }

    @Override
    public List<PlayerNote> getFirst(int amount) {
        return getPlayerNotesByResult(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .orderBy("Time", SearchOrder.ASC)
                .limit(amount)
                .execute());
    }

    @Override
    public PlayerNote getByIndex(int index) {
        return getPlayerNoteByResultEntry(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .orderBy("Time", SearchOrder.DESC)
                .index(index, index)
                .execute().firstOrNull());
    }

    @Override
    public List<PlayerNote> getByIndexRange(int startIndex, int lastIndex) {
        return getPlayerNotesByResult(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .orderBy("Time", SearchOrder.DESC)
                .index(startIndex, lastIndex)
                .execute());
    }

    @Override
    public List<PlayerNote> getSince(long time) {
        return getPlayerNotesByResult(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .orderBy("Time", SearchOrder.DESC)
                .whereHigher("Time", time)
                .orderBy("Time", SearchOrder.ASC)
                .execute());
    }

    @Override
    public List<PlayerNote> getUntil(long time) {
        return getPlayerNotesByResult(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .whereLower("Time", time)
                .orderBy("Time", SearchOrder.ASC)
                .execute());
    }

    @Override
    public List<PlayerNote> getBetween(long startTime, long endTime) {
        return getPlayerNotesByResult(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .whereBetween("Time", startTime, endTime)
                .orderBy("Time", SearchOrder.ASC)
                .execute());
    }

    @Override
    public List<PlayerNote> getPage(int page, int sizePerPage) {
        return getPlayerNotesByResult(DefaultDKBans.getInstance().getStorage().getHistoryNotes().find()
                .where("HistoryId",entry.getId())
                .orderBy("Time", SearchOrder.DESC)
                .page(page,sizePerPage)
                .orderBy("Time", SearchOrder.ASC)
                .execute());
    }


    @Override
    public PlayerNote createNote(DKBansExecutor creator, String message, PlayerNoteType type) {
        return entry.createNote(creator, message, type);
    }

    private List<PlayerNote> getPlayerNotesByResult(QueryResult result) {
        List<PlayerNote> sessions = new ArrayList<>();
        result.loadIn(sessions, this::getPlayerNoteByResultEntry);
        return sessions;
    }

    private PlayerNote getPlayerNoteByResultEntry(QueryResultEntry entry) {
        if(entry == null) return null;
        return new DefaultPlayerNote(entry.getInt("Id"),
                PlayerNoteType.byId(entry.getInt("TypeId")),
                entry.getLong("Time"),
                entry.getString("Message"),
                entry.getUniqueId("CreatorId"));
    }
}
