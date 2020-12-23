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

package net.pretronic.dkbans.api.storage;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.filter.Filter;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.chatlog.ChatLogEntry;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.dkbans.api.player.ipblacklist.IpAddressBlock;
import net.pretronic.dkbans.api.player.ipblacklist.IpAddressBlockType;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.TemplateType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.libraries.utility.map.Pair;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    List<PlayerHistoryEntry> loadEntries(PlayerHistory history);

    PlayerHistoryEntry loadEntry(int id);


    List<PlayerHistoryEntry> getActiveEntries(PunishmentType type);

    List<PlayerHistoryEntry> getActiveEntriesOnPage(PunishmentType type, int page, int pageSize);


    List<PlayerHistoryEntrySnapshot> loadSnapshots(PlayerHistoryEntry entry);


    PlayerHistoryType createPlayerHistoryType(String name);

    Collection<PlayerHistoryType> loadPlayerHistoryTypes();


    void clearHistory(UUID uniqueId);

    void clearHistoryEntry(int id);

    void clearHistoryEntry(Collection<PlayerHistoryEntry> entries);


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

    List<PlayerSession> getPageSessions(DKBansPlayer player,int page, int pageSize);


    long getOnlineTime(UUID playerId);

    void updateOnlineTime(UUID playerId, long onlineTime);


    List<PlayerNote> getPlayerNotes(DKBansPlayer player);

    List<PlayerNote> getLastPlayerNotes(DKBansPlayer player, int amount);

    List<PlayerNote> getFirstPlayerNotes(DKBansPlayer player, int amount);

    PlayerNote getPlayerNoteByIndex(DKBansPlayer player, int index);

    List<PlayerNote> getPlayerNotesByIndexRange(DKBansPlayer player, int startIndex, int lastIndex);

    List<PlayerNote> getSincePlayerNotes(DKBansPlayer player, long time);

    List<PlayerNote> getUntilPlayerNotes(DKBansPlayer player, long time);

    List<PlayerNote> getBetweenPlayerNotes(DKBansPlayer player, long startTime, long endTime);

    List<PlayerNote> getPagePlayerNotes(DKBansPlayer player,int page, int sizePerPage);


    //Filter

    Collection<Filter> loadFilters();

    int createFilter(String area, String operation, String value);

    void deleteFilter(int id);


    //Report

    PlayerReport createPlayerReport(DKBansPlayer player, ReportState state);

    PlayerReportEntry createPlayerReportEntry(PlayerReport report, DKBansExecutor reporter,String reason, ReportTemplate template, String serverName, UUID serverId);


    //Chat log

    ChatLogEntry createChatLogEntry(UUID playerId, String message, long time, String serverName, UUID serverId, String filterAffiliationArea);

    CompletableFuture<ChatLogEntry> createChatLogEntryAsync(UUID playerId, String message, long time, String serverName, UUID serverId, String filterAffiliationArea);



    //Ip blacklist

    IpAddressBlock getIpAddressBlock(String ipAddress);

    IpAddressBlock blockIpAddress(String ipAddress, IpAddressBlockType type, DKBansExecutor staff, String reason, long timeout, String forReason, long forDuration);

    IpAddressBlock blockIpAddress(String ipAddress, IpAddressBlockType type, DKBansExecutor staff, String reason, long timeout, PunishmentTemplate forTemplate);

    void unblockIpAddress(IpAddressBlock addressBlock);



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
