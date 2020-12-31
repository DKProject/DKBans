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

package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplate;

import java.util.List;

public interface PlayerHistoryEntry extends Iterable<PlayerHistoryEntrySnapshot> {

    int getId();

    long getCreated();

    PlayerHistory getHistory();

    PlayerSession getSession();


    boolean isActive();

    boolean hasTimeout();


    PlayerHistoryEntrySnapshot getCurrent();

    PlayerHistoryEntrySnapshot getFirst();

    PlayerHistoryEntrySnapshot get(int id);

    List<PlayerHistoryEntrySnapshot> getAll();


    PlayerHistoryEntrySnapshotBuilder newSnapshot(DKBansExecutor executor);

    //update


    PlayerNoteList getNotes();

    default PlayerNote createNote(DKBansExecutor creator, String message){
        return createNote(creator,message, PlayerNoteType.NORMAL);
    }

    PlayerNote createNote(DKBansExecutor creator, String message, PlayerNoteType type);


    PlayerHistoryEntrySnapshot unpunish(DKBansExecutor executor, UnPunishmentTemplate template);

    PlayerHistoryEntrySnapshot unpunish(DKBansExecutor executor,String reason);

}
