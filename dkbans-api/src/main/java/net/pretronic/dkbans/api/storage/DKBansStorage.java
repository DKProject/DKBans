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

package net.pretronic.dkbans.api.storage;

import net.pretronic.dkbans.api.filter.Filter;
import net.pretronic.dkbans.api.filter.operation.FilterOperation;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.TemplateType;
import net.pretronic.libraries.utility.map.Pair;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface DKBansStorage {

    /* Player */

    Collection<PlayerNote> loadPlayerNotes(UUID uniqueId);

    int createPlayerNote(UUID playerId, UUID creatorId,PlayerNoteType type,String message);


    TemplateCategory createTemplateCategory(String name, String displayName);

    Collection<TemplateCategory> loadTemplateCategories();



    Collection<TemplateGroup> loadTemplateGroups();

    void importTemplateGroup(TemplateGroup templateGroup);

    /* History */

    Pair<PlayerHistoryEntry, Integer> createHistoryEntry(DKBansPlayer player, PlayerHistoryEntrySnapshot snapshot);//Braucht player, session kann null sein

    int insertHistoryEntrySnapshot(PlayerHistoryEntrySnapshot snapshot);


    List<PlayerHistoryEntry> loadActiveEntries(PlayerHistory history);


    PlayerHistoryType createPlayerHistoryType(String name);

    Collection<PlayerHistoryType> loadPlayerHistoryTypes();



    TemplateGroup createTemplateGroup(String name, TemplateType templateType, CalculationType calculationType);


    int startPlayerSession(PlayerSession session);

    void completePlayerSession(PlayerSession session);

    List<PlayerSession> getPlayerSessions(DKBansPlayer player);

    List<PlayerSession> getLastPlayerSessions(DKBansPlayer player, int amount);

    List<PlayerSession> getFirstPlayerSessions(DKBansPlayer player, int amount);

    PlayerSession getPlayerSessionByIndex(DKBansPlayer player, int index);

    List<PlayerSession> getPlayerSessionByIndexRange(DKBansPlayer player, int startIndex, int lastIndex);

    List<PlayerSession> getSincePlayerSessions(DKBansPlayer player, long time);

    List<PlayerSession> getUntilPlayerSessions(DKBansPlayer player, long time);

    List<PlayerSession> getBetweenPlayerSessions(DKBansPlayer player, long startTime, long endTime);


    List<PlayerNote> getPlayerNotes(DKBansPlayer player);

    List<PlayerNote> getLastPlayerNotes(DKBansPlayer player, int amount);

    List<PlayerNote> getFirstPlayerNotes(DKBansPlayer player, int amount);

    PlayerNote getPlayerNoteByIndex(DKBansPlayer player, int index);

    List<PlayerNote> getPlayerNotesByIndexRange(DKBansPlayer player, int startIndex, int lastIndex);

    List<PlayerNote> getSincePlayerNotes(DKBansPlayer player, long time);

    List<PlayerNote> getUntilPlayerNotes(DKBansPlayer player, long time);

    List<PlayerNote> getBetweenPlayerNotes(DKBansPlayer player, long startTime, long endTime);


    //Filter

    Collection<Filter> loadFilters();

    int createFilter(String area, String operation, String value);

    void deleteFilter(int id);

      /*
    private final PlayerHistoryEntry entry;
    private final int id;
    private final PlayerHistoryType historyType;
    private final PunishmentType punishmentType;

    private final String reason;
    private final long timeout;

    private final Template template;

    private final DKBansExecutor stuff;
    private final DKBansScope scope;

    private final int points;
    private final boolean active;

    private final Document properties;
    private final String revokeMessage;
    private final Template revokeTemplate;

    private final long modifyTime;
    private final DKBansExecutor modifier;
     */
}
