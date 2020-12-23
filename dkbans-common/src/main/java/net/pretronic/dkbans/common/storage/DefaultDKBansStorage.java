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

package net.pretronic.dkbans.common.storage;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.databasequery.api.query.type.InsertQuery;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
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
import net.pretronic.dkbans.api.storage.DKBansStorage;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.common.filter.DefaultFilter;
import net.pretronic.dkbans.common.player.chatlog.DefaultChatLogEntry;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntry;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryType;
import net.pretronic.dkbans.common.player.ipblacklist.DefaultIpAddressBlock;
import net.pretronic.dkbans.common.player.note.DefaultPlayerNote;
import net.pretronic.dkbans.common.player.report.DefaultPlayerReport;
import net.pretronic.dkbans.common.player.report.DefaultPlayerReportEntry;
import net.pretronic.dkbans.common.player.session.DefaultPlayerSession;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.dkbans.common.template.DefaultTemplateCategory;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.map.Pair;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DefaultDKBansStorage implements DKBansStorage {

    private final DKBans dkBans;
    private final Database database;

    private final DatabaseCollection playerSessions;
    private final DatabaseCollection playerChatLog;
    private final DatabaseCollection playerSettings;
    private final DatabaseCollection playerNotes;
    private final DatabaseCollection historyType;
    private final DatabaseCollection history;
    private final DatabaseCollection historyVersion;
    private final DatabaseCollection historyNotes;
    private final DatabaseCollection reports;
    private final DatabaseCollection reportEntries;
    private final DatabaseCollection ipAddressBlacklist;
    private final DatabaseCollection templateCategories;
    private final DatabaseCollection templateGroups;
    private final DatabaseCollection template;

    private final DatabaseCollection filter;

    private final DatabaseCollection broadcast;
    private final DatabaseCollection broadcastGroupAssignment;
    private final DatabaseCollection broadcastGroup;

    public DefaultDKBansStorage(DKBans dkBans, Database database) {
        this.dkBans = dkBans;
        this.database = database;
        this.playerSessions = createPlayerSessionsCollection();
        this.playerSettings = createPlayerSettingsCollection();
        this.playerNotes = createPlayerNotesCollection();

        this.historyType = createHistoryTypeCollection();

        this.templateCategories = createTemplateCategoriesCollection();
        this.templateGroups = createTemplateGroupsCollection();
        this.template = createTemplateCollection();


        this.history = createHistoryCollection();
        this.historyVersion = createHistoryVersionCollection();
        this.historyNotes = createHistoryNotesCollection();
        this.reports = createReportsCollection();
        this.reportEntries = createReportEntriesCollection();
        this.ipAddressBlacklist = createIpAddressBlacklistCollection();


        this.filter = createFilterCollection();
        this.playerChatLog = createPlayerChatLogCollection();

        this.broadcast = createBroadcastCollection();
        this.broadcastGroup = createBroadcastGroupCollection();
        this.broadcastGroupAssignment = createBroadcastGroupAssignment();
    }

    @Override
    public Collection<PlayerNote> loadPlayerNotes(UUID uniqueId) {
        return null;
    }

    @Override
    public int createPlayerNote(UUID playerId, UUID creatorId, PlayerNoteType type, String message) {
        return this.playerNotes.insert()
                .set("PlayerId",playerId)
                .set("CreatorId",creatorId)
                .set("Time",System.currentTimeMillis())
                .set("Message",message)
                .set("TypeId",type.getId())
                .executeAndGetGeneratedKeyAsInt("Id");
    }

    @Override
    public TemplateCategory createTemplateCategory(String name, String displayName) {
        int id = this.templateCategories.insert().set("Name", name).set("DisplayName", displayName).executeAndGetGeneratedKeyAsInt("Id");
        return new DefaultTemplateCategory(id, name, displayName);
    }

    @Override
    public Collection<TemplateCategory> loadTemplateCategories() {
        Collection<TemplateCategory> categories = new ArrayList<>();
        for (QueryResultEntry resultEntry : this.templateCategories.find().execute()) {
            DefaultTemplateCategory category = new DefaultTemplateCategory(resultEntry.getInt("Id"),
                    resultEntry.getString("Name"), resultEntry.getString("DisplayName"));
            categories.add(category);
        }
        return categories;
    }

    @Override
    public Collection<TemplateGroup> loadTemplateGroups() {
        Collection<TemplateGroup> templateGroups = new ArrayList<>();
        for (QueryResultEntry resultEntry : this.templateGroups.find().execute()) {
            int groupId = resultEntry.getInt("Id");
            String groupName = resultEntry.getString("Name");

            String type = resultEntry.getString("TemplateType");
            TemplateType templateType = TemplateType.byName(type);

            CalculationType calculationType = CalculationType.byName("CalculationType");

            DefaultTemplateGroup templateGroup = new DefaultTemplateGroup(groupId, groupName, templateType, calculationType);

            for (QueryResultEntry subResultEntry : this.template.find().where("GroupId", groupId).execute()) {
                String name = subResultEntry.getString("Name");

                Collection<String> aliases = loadAliases(subResultEntry.getString("Aliases"));
                TemplateCategory category = dkBans.getTemplateManager().getTemplateCategory(subResultEntry.getInt("CategoryId"));
                PlayerHistoryType historyType = null;
                Object historyTypeId = subResultEntry.getObject("HistoryTypeId");
                if(historyTypeId instanceof Integer) {
                    historyType = dkBans.getHistoryManager().getHistoryType((int) historyTypeId);
                }
                templateGroup.addTemplateInternal(TemplateFactory.create(templateType,
                        subResultEntry.getInt("Id"),
                        subResultEntry.getInt("InGroupId"),
                        name,
                        templateGroup,
                        subResultEntry.getString("DisplayName"),
                        subResultEntry.getString("Permission"),
                        aliases,
                        historyType,
                        subResultEntry.getBoolean("Enabled"),
                        subResultEntry.getBoolean("Hidden"),
                        category,
                        DocumentFileType.JSON.getReader().read(subResultEntry.getString("Data"))));
            }
            templateGroups.add(templateGroup);
        }
        return templateGroups;
    }

    @Override
    public void importTemplateGroup(TemplateGroup templateGroup) {
        FindQuery groupExist = this.templateGroups.find()
                .where("Name", templateGroup.getName())
                .where("TemplateType", templateGroup.getTemplateType().getName());

        if(groupExist.execute().isEmpty()) {
            int id = templateGroups.insert().set("Name", templateGroup.getName())
                    .set("Type", templateGroup.getName())
                    .executeAndGetGeneratedKeyAsInt("Id");
            ((DefaultTemplateGroup)templateGroup).setIdInternal(id);
        }
        for (Template template : templateGroup.getTemplates()) {
            QueryResult result = this.template.find()
                    .where("InGroupId", template.getInGroupId())
                    .where("GroupId", templateGroup.getId())
                    .execute();
            if(!result.isEmpty()) {
                this.template.update()
                        .set("Name", template.getName())
                        .set("DisplayName", template.getDisplayName())
                        .set("Permission", template.getPermission())
                        .set("Aliases", buildAliases(template.getAliases()))
                        .set("HistoryTypeId", template.getHistoryType() != null ? template.getHistoryType().getId() : null)
                        .set("Enabled", template.isEnabled())
                        .set("Hidden", template.isHidden())
                        .set("CategoryId", template.getCategory().getId())
                        .set("Data", DocumentFileType.JSON.getWriter().write(TemplateFactory.toData(template), false))
                        .where("InGroupId", template.getInGroupId())
                        .where("GroupId", templateGroup.getId())
                        .execute();
                ((DefaultTemplate)template).setIdInternal(result.first().getInt("Id"));
            } else {
                int id = this.template.insert()
                        .set("InGroupId", template.getInGroupId())
                        .set("Name", template.getName())
                        .set("DisplayName", template.getDisplayName())
                        .set("Permission", template.getPermission())
                        .set("Aliases", buildAliases(template.getAliases()))
                        .set("HistoryTypeId", template.getHistoryType() != null ? template.getHistoryType().getId() : null)
                        .set("Enabled", template.isEnabled())
                        .set("Hidden", template.isHidden())
                        .set("CategoryId", template.getCategory().getId())
                        .set("Data", DocumentFileType.JSON.getWriter().write(TemplateFactory.toData(template), false))
                        .set("GroupId", templateGroup.getId())
                        .executeAndGetGeneratedKeyAsInt("Id");

                ((DefaultTemplate)template).setIdInternal(id);
            }
        }
    }

    private String buildAliases(Collection<String> aliases) {
        StringBuilder builder = new StringBuilder();
        for (String alias : aliases) {
            if(builder.length() > 0) builder.append(",");
            builder.append(alias);
        }
        return builder.toString();
    }

    private Collection<String> loadAliases(String aliases0) {
        return new ArrayList<>(Arrays.asList(aliases0.split(",")));
    }

    @Override
    public Pair<PlayerHistoryEntry, Integer> createHistoryEntry(DKBansPlayer player, PlayerHistoryEntrySnapshot snapshot) {
        long created = System.currentTimeMillis();
        int id = history.insert()
                .set("PlayerId",player.getUniqueId())
                .set("Created",created)
                .executeAndGetGeneratedKeyAsInt("Id");
        int snapId = buildSnapshotQuery(id,snapshot);
        return new Pair<>(new DefaultPlayerHistoryEntry(player.getHistory(),id,snapshot,created),snapId);
    }

    @Override
    public int insertHistoryEntrySnapshot(PlayerHistoryEntrySnapshot snapshot) {//@Todo optimize with group execution
        historyVersion.update()
                .set("ModifiedActive",false)
                .where("HistoryId",snapshot.getEntry().getId()).execute();
        return buildSnapshotQuery(snapshot.getEntry().getId(),snapshot);
    }

    private int buildSnapshotQuery(int historyId,PlayerHistoryEntrySnapshot snapshot){
       InsertQuery query = historyVersion.insert()
                .set("HistoryId",historyId)
                .set("Reason",snapshot.getReason())
                .set("Timeout",snapshot.getTimeout())
                .set("StaffId",snapshot.getStaff().getUniqueId())
                .set("Points",snapshot.getPoints())
                .set("Active",snapshot.isActive())
                .set("Properties","{}")
                .set("PunishmentType",snapshot.getPunishmentType().getName())
                .set("RevokeReason",snapshot.getRevokeReason())
                .set("ModifiedTime",snapshot.getModifiedTime())
                .set("ModifiedBy",snapshot.getModifiedBy().getUniqueId())
                .set("ModifiedActive",snapshot.isModifiedActive());
       if(snapshot.getHistoryType() != null)  query.set("HistoryTypeId",snapshot.getHistoryType().getId());
       if(snapshot.getRevokeTemplate() != null) query.set("RevokeTemplateId",snapshot.getRevokeTemplate().getId());
       if(snapshot.getTemplate() != null) query.set("TemplateId",snapshot.getTemplate().getId());
       if(snapshot.getScope() != null){
           query.set("ScopeType",snapshot.getScope().getType())
                   .set("ScopeName",snapshot.getScope().getName())
                   .set("ScopeId",snapshot.getScope().getId());
       }
       return query.executeAndGetGeneratedKeyAsInt("Id");
    }

    @Override
    public List<PlayerHistoryEntry> loadActiveEntries(PlayerHistory playerHistory) {
        List<PlayerHistoryEntry> result = new ArrayList<>();
        QueryResult result0 = createBaseQuery()
                .where("PlayerId",playerHistory.getPlayer().getUniqueId())
                .where("ModifiedActive",true).where("Active",true)
                .or(query -> query.where("Timeout",-1).whereHigher("Timeout",System.currentTimeMillis()))
                .orderBy("ModifiedTime", SearchOrder.DESC)
                .execute();

        readEntries(playerHistory, result, result0);
        return result;
    }

    @Override
    public List<PlayerHistoryEntry> loadEntries(PlayerHistory playerHistory) {
        List<PlayerHistoryEntry> result = new ArrayList<>();

        FindQuery query = createBaseQuery()
                .where("ModifiedActive",true)
                .where("PlayerId",playerHistory.getPlayer().getUniqueId());

        QueryResult result0 = query.execute();

        readEntries(playerHistory, result, result0);
        return result;
    }

    @Override
    public PlayerHistoryEntry loadEntry(int id) {
        FindQuery query = createBaseQuery().where("HistoryId",id);
        return createSnapshots(query);
    }

    private PlayerHistoryEntry createSnapshots(FindQuery query) {
        QueryResult result = query.execute();
        if(result.isEmpty()) return null;

        QueryResultEntry resultEntry = result.first();
        DefaultPlayerHistoryEntrySnapshot snapshot = createSnapshot(resultEntry,null);

        PlayerHistory playerHistory = DKBans.getInstance().getPlayerManager()
                .getPlayer(resultEntry.getUniqueId("PlayerId")).getHistory();

        DefaultPlayerHistoryEntry entry = new DefaultPlayerHistoryEntry(playerHistory,
                resultEntry.getInt("HistoryId"),
                snapshot,
                resultEntry.getLong("Created"));
        snapshot.setEntry(entry);
        return entry;
    }

    @Override
    public List<PlayerHistoryEntry> getActiveEntries(PunishmentType type) {
        FindQuery query = createBaseQuery()
                .where("ModifiedActive",true).where("Active",true)
                .or(query1 -> query1.where("Timeout",-1).whereHigher("Timeout",System.currentTimeMillis()))
                .where("PunishmentType",type.getName());
        return createActiveEntries(query);
    }

    @Override
    public List<PlayerHistoryEntry> getActiveEntriesOnPage(PunishmentType type, int page, int pageSize) {
        FindQuery query = createBaseQuery()
                .where("PunishmentType",type.getName())
                .where("ModifiedActive",true).where("Active",true)
                .or(query1 -> query1.where("Timeout",-1).whereHigher("Timeout",System.currentTimeMillis()))
                .page(page,pageSize);
        return createActiveEntries(query);
    }

    private List<PlayerHistoryEntry> createActiveEntries(FindQuery query) {
        QueryResult queryResult = query.execute();
        if(queryResult.isEmpty()) return null;

        List<PlayerHistoryEntry> result = new ArrayList<>();
        for (QueryResultEntry resultEntry : queryResult) {
            DefaultPlayerHistoryEntrySnapshot snapshot = createSnapshot(resultEntry,null);

            PlayerHistory playerHistory = DKBans.getInstance().getPlayerManager()
                    .getPlayer(resultEntry.getUniqueId("PlayerId")).getHistory();

            DefaultPlayerHistoryEntry entry = new DefaultPlayerHistoryEntry(playerHistory,
                    resultEntry.getInt("HistoryId"),
                    snapshot,
                    resultEntry.getLong("Created"));
            snapshot.setEntry(entry);
            result.add(entry);
        }
        return result;
    }

    @Override
    public List<PlayerHistoryEntrySnapshot> loadSnapshots(PlayerHistoryEntry historyEntry) {
        List<PlayerHistoryEntrySnapshot> snapshots = new ArrayList<>();
        QueryResult result = this.historyVersion.find()
                .get("*")
                .getAs("Id","SnapshotId")
                .where("HistoryId",historyEntry.getId()).execute();
        for (QueryResultEntry entry : result) snapshots.add(createSnapshot(entry,historyEntry));
        return snapshots;
    }

    private FindQuery createBaseQuery() {
        return history.find()
                .getAs(this.history, "Id", "HistoryId")
                .getAs(this.historyVersion, "Id", "SnapshotId")
                .get("Created","PlayerId", "Reason", "Timeout", "StaffId", "ScopeType", "ScopeName", "ScopeId"
                        , "Points", "Active", "Properties", "HistoryTypeId", "PunishmentType", "TemplateId"
                        , "RevokeTemplateId", "RevokeReason", "ModifiedTime", "ModifiedBy", "ModifiedActive")
                .join(historyVersion).on(historyVersion,"HistoryId",history,"Id");
    }

    private void readEntries(PlayerHistory playerHistory, List<PlayerHistoryEntry> result, QueryResult result0) {
        if(!result0.isEmpty()){
            for (QueryResultEntry resultEntry : result0) {
                DefaultPlayerHistoryEntrySnapshot snapshot = createSnapshot(resultEntry,null);
                DefaultPlayerHistoryEntry entry = new DefaultPlayerHistoryEntry(playerHistory,
                        resultEntry.getInt("HistoryId"),
                        snapshot,
                        resultEntry.getLong("Created"));
                snapshot.setEntry(entry);
                result.add(entry);
            }
        }
    }

    private DefaultPlayerHistoryEntrySnapshot createSnapshot(QueryResultEntry resultEntry,PlayerHistoryEntry entry) {
        DKBansScope scope = null;
        if(resultEntry.getString("ScopeType") != null){
            scope = new DKBansScope(resultEntry.getString("ScopeType")
                    ,resultEntry.getString("ScopeName")
                    ,resultEntry.getUniqueId("ScopeId"));
        }

        for (Map.Entry<String, Object> entry0 : resultEntry) {
            System.out.println(entry0.getKey()+" | "+entry0.getValue());
        }

        return new DefaultPlayerHistoryEntrySnapshot(entry,
                resultEntry.getInt("SnapshotId"),
                dkBans.getHistoryManager().getHistoryType(resultEntry.getInt("HistoryTypeId")),
                PunishmentType.getPunishmentType(resultEntry.getString("PunishmentType")),
                resultEntry.getString("Reason"),
                resultEntry.getLong("Timeout"),
                dkBans.getTemplateManager().getTemplate(resultEntry.getInt("TemplateId")),
                resultEntry.getUniqueId("StaffId"),
                scope,
                resultEntry.getInt("Points"),
                resultEntry.getBoolean("Active"),
                null/*@Todo add properties*/,
                resultEntry.getString("RevokeReason"),
                dkBans.getTemplateManager().getTemplate(resultEntry.getInt("RevokeTemplateId")),
                resultEntry.getBoolean("ModifiedActive"),
                resultEntry.getLong("ModifiedTime"),
                resultEntry.getUniqueId("ModifiedBy"));
    }

    @Override
    public PlayerHistoryType createPlayerHistoryType(String name) {
        int id = this.historyType.insert().set("Name", name).executeAndGetGeneratedKeyAsInt("Id");
        return new DefaultPlayerHistoryType(id, name);
    }

    @Override
    public Collection<PlayerHistoryType> loadPlayerHistoryTypes() {
        Collection<PlayerHistoryType> types = new ArrayList<>();
        this.historyType.find().execute().loadIn(types, entry -> new DefaultPlayerHistoryType(entry.getInt("Id"), entry.getString("Name")));
        return types;
    }

    @Override
    public void clearHistory(UUID uniqueId) {
        this.history.delete().where("PlayerId",uniqueId).execute();
    }

    @Override
    public void clearHistoryEntry(int id) {
        this.history.delete().where("Id",id).execute();
    }

    @Override
    public void clearHistoryEntry(Collection<PlayerHistoryEntry> entries) {
        this.history.delete().whereIn("Id", entries, PlayerHistoryEntry::getId).execute();
    }

    @Override
    public TemplateGroup createTemplateGroup(String name, TemplateType templateType, CalculationType calculationType) {
        int id = this.templateGroups.insert().set("Name", name)
                .set("TemplateType", templateType.getName())
                .set("CalculationType", calculationType)
                .executeAndGetGeneratedKeyAsInt("id");
        return new DefaultTemplateGroup(id, name, templateType, calculationType);
    }

    @Override
    public int startPlayerSession(PlayerSession session) {
        this.playerSessions.delete().whereIsNull("DisconnectTime").execute();

        return this.playerSessions.insert()
                .set("PlayerId", session.getPlayer().getUniqueId())
                .set("PlayerSessionName", session.getPlayerSessionName())
                .set("IpAddress", session.getIpAddress())
                .set("Country", session.getCountry())
                .set("Region", session.getRegion())
                .set("ProxyId", session.getProxyId())
                .set("ProxyName", session.getProxyName())
                .set("ClientEdition", session.getClientEdition())
                .set("ClientProtocolVersion", session.getClientProtocolVersion())
                .set("ConnectTime", session.getConnectTime())
                .executeAndGetGeneratedKeyAsInt("Id");
    }

    @Override
    public void completePlayerSession(PlayerSession session) {
        this.playerSessions.update()
                .set("LastServerId", session.getLastServerId())
                .set("LastServerName", session.getLastServerName())
                .set("DisconnectTime", session.getDisconnectTime())
                .where("Id", session.getId())
                .execute();
    }

    @Override
    public List<PlayerSession> getPlayerSessions(DKBansPlayer player) {
        return getPlayerSessionsByResult(player, this.playerSessions.find().where("PlayerId", player.getUniqueId()).execute());
    }

    @Override
    public List<PlayerSession> getLastPlayerSessions(DKBansPlayer player, int amount) {
        return getPlayerSessionsByResult(player, this.playerSessions.find()
                .where("PlayerId", player.getUniqueId())
                .orderBy("DisconnectTime", SearchOrder.DESC)
                .limit(amount)
                .execute());
    }

    @Override
    public List<PlayerSession> getFirstPlayerSessions(DKBansPlayer player, int amount) {
        return getPlayerSessionsByResult(player, this.playerSessions.find()
                .where("PlayerId", player.getUniqueId())
                .orderBy("DisconnectTime", SearchOrder.ASC)
                .limit(amount)
                .execute());
    }

    @Override
    public PlayerSession getPlayerSessionByIndex(DKBansPlayer player, int index) {
        return getPlayerSessionByResultEntry(player, this.playerSessions.find()
                .where("PlayerId", player.getUniqueId())
                .orderBy("DisconnectTime", SearchOrder.DESC)
                .index(index, index)
                .execute().firstOrNull());
    }

    @Override
    public List<PlayerSession> getPlayerSessionByIndexRange(DKBansPlayer player, int startIndex, int lastIndex) {
        return getPlayerSessionsByResult(player, this.playerSessions.find()
                .where("PlayerId", player.getUniqueId())
                .orderBy("DisconnectTime", SearchOrder.DESC)
                .index(startIndex, lastIndex)
                .execute());
    }

    @Override
    public List<PlayerSession> getSincePlayerSessions(DKBansPlayer player, long time) {
        return getPlayerSessionsByResult(player, this.playerSessions.find()
                .where("PlayerId", player.getUniqueId())
                .whereHigher("DisconnectTime", time)
                .orderBy("DisconnectTime", SearchOrder.ASC)
                .execute());
    }

    @Override
    public List<PlayerSession> getUntilPlayerSessions(DKBansPlayer player, long time) {
        return getPlayerSessionsByResult(player, this.playerSessions.find()
                .where("PlayerId", player.getUniqueId())
                .whereLower("DisconnectTime", time)
                .orderBy("DisconnectTime", SearchOrder.ASC)
                .execute());
    }

    @Override
    public List<PlayerSession> getBetweenPlayerSessions(DKBansPlayer player, long startTime, long endTime) {
        return getPlayerSessionsByResult(player, this.playerSessions.find()
                .where("PlayerId", player.getUniqueId())
                .whereBetween("DisconnectTime", startTime, endTime)
                .orderBy("DisconnectTime", SearchOrder.ASC)
                .execute());
    }

    @Override
    public Collection<Filter> loadFilters() {
        Collection<Filter> result = new ArrayList<>();
        for (QueryResultEntry entry : filter.find().execute()) {
            result.add(new DefaultFilter(entry.getInt("Id")
                    ,entry.getString("AffiliationArea")
                    ,entry.getString("Operation")
                    ,entry.getString("Value")));
        }
        return result;
    }

    @Override
    public int createFilter(String area, String operation, String value) {
        return filter.insert()
                .set("AffiliationArea",area)
                .set("Operation",operation)
                .set("Value",value)
                .executeAndGetGeneratedKeyAsInt("Id");
    }

    @Override
    public void deleteFilter(int id) {
        filter.delete().where("Id",id).execute();
    }

    @Override
    public PlayerReport createPlayerReport(DKBansPlayer player, ReportState state) {
        Validate.notNull(player, state);
        int id = this.reports.insert()
                .set("PlayerId", player.getUniqueId())
                .set("State", state.toString())
                .executeAndGetGeneratedKeyAsInt("Id");
        return new DefaultPlayerReport(id, player, state);
    }

    @Override
    public PlayerReportEntry createPlayerReportEntry(PlayerReport report, DKBansExecutor reporter,String reason, ReportTemplate template, String serverName, UUID serverId) {
        long time = System.currentTimeMillis();
        int id = this.reportEntries.insert()
                .set("ReportId", report.getId())
                .set("ReporterId", reporter.getUniqueId())
                .set("TemplateId", template != null ? template.getId() : null)
                .set("Reason", reason)
                .set("Time", time)
                .set("ServerName", serverName)
                .set("ServerId", serverId)
                .set("Properties", "{}")
                .executeAndGetGeneratedKeyAsInt("Id");
        return new DefaultPlayerReportEntry(id, report, reporter, template, reason , serverName, serverId, time, Document.newDocument());
    }

    @Override
    public ChatLogEntry createChatLogEntry(UUID playerId, String message, long time, String serverName, UUID serverId, String filterAffiliationArea) {
        int id = this.playerChatLog.insert()
                .set("PlayerId", playerId)
                .set("Message", message)
                .set("Time", time)
                .set("ServerName", serverName)
                .set("ServerId", serverId)
                .set("FilterAffiliationArea", filterAffiliationArea)
                .executeAndGetGeneratedKeyAsInt("Id");
        return new DefaultChatLogEntry(id, playerId, message, time, serverName, serverId, filterAffiliationArea);
    }

    @Override
    public CompletableFuture<ChatLogEntry> createChatLogEntryAsync(UUID playerId, String message, long time, String serverName, UUID serverId, String filterAffiliationArea) {
        CompletableFuture<ChatLogEntry> future = new CompletableFuture<>();
        DKBans.getInstance().getExecutorService().execute(()-> future.complete(createChatLogEntry(playerId, message, time, serverName, serverId, filterAffiliationArea)));
        return future;
    }

    @Override
    public IpAddressBlock getIpAddressBlock(String ipAddress) {
        QueryResultEntry result = this.ipAddressBlacklist.find().where("Address", ipAddress).execute().firstOrNull();
        if(result == null) return null;
        return new DefaultIpAddressBlock(result.getInt("Id"),
                result.getString("Address"),
                IpAddressBlockType.valueOf(result.getString("Type")),
                result.getUniqueId("StaffId"),
                result.getString("Reason"),
                result.getLong("Timeout"),
                result.getString("ForReason"),
                result.getLong("ForDuration"),
                result.getInt("ForTemplateId"));
    }

    @Override
    public IpAddressBlock blockIpAddress(String ipAddress, IpAddressBlockType type, DKBansExecutor staff, String reason, long timeout, String forReason, long forDuration) {
        return null;
    }

    @Override
    public IpAddressBlock blockIpAddress(String ipAddress, IpAddressBlockType type, DKBansExecutor staff, String reason, long timeout, PunishmentTemplate forTemplate) {
        return null;
    }

    @Override
    public void unblockIpAddress(IpAddressBlock addressBlock) {
        this.ipAddressBlacklist.delete().where("Id", addressBlock.getId()).execute();
    }

    private List<PlayerSession> getPlayerSessionsByResult(DKBansPlayer player, QueryResult result) {
        List<PlayerSession> sessions = new ArrayList<>();
        result.loadIn(sessions, entry -> getPlayerSessionByResultEntry(player, entry));
        return sessions;
    }

    private PlayerSession getPlayerSessionByResultEntry(DKBansPlayer player, QueryResultEntry entry) {
        if(entry == null) return null;
        return new DefaultPlayerSession(entry.getInt("Id"),
                player,
                entry.getString("PlayerSessionName"),
                entry.getObject("IpAddress", InetAddress.class),
                entry.getString("Country"),
                entry.getString("Region"),
                entry.getString("ProxyName"),
                entry.getUniqueId("ProxyId"),
                entry.getString("LastServerName"),
                entry.getUniqueId("LastServerId"),
                entry.getString("ClientEdition"),
                entry.getInt("ClientProtocolVersion"),
                entry.getLong("ConnectTime"),
                entry.getLong("DisconnectTime"));
    }


    @Override
    public List<PlayerNote> getPlayerNotes(DKBansPlayer player) {
        return getPlayerNotesByResult(player, this.playerNotes.find().where("PlayerId", player.getUniqueId()).execute());
    }

    @Override
    public List<PlayerNote> getLastPlayerNotes(DKBansPlayer player, int amount) {
        return getPlayerNotesByResult(player, this.playerNotes.find()
                .where("PlayerId", player.getUniqueId())
                .orderBy("Time", SearchOrder.DESC)
                .limit(amount)
                .execute());
    }

    @Override
    public List<PlayerNote> getFirstPlayerNotes(DKBansPlayer player, int amount) {
        return getPlayerNotesByResult(player, this.playerNotes.find()
                .where("PlayerId", player.getUniqueId())
                .orderBy("Time", SearchOrder.ASC)
                .limit(amount)
                .execute());
    }

    @Override
    public PlayerNote getPlayerNoteByIndex(DKBansPlayer player, int index) {
        return getPlayerNoteByResultEntry(player, this.playerNotes.find()
                .where("PlayerId", player.getUniqueId())
                .orderBy("Time", SearchOrder.DESC)
                .index(index, index)
                .execute().firstOrNull());
    }

    @Override
    public List<PlayerNote> getPlayerNotesByIndexRange(DKBansPlayer player, int startIndex, int lastIndex) {
        return getPlayerNotesByResult(player, this.playerNotes.find()
                .where("PlayerId", player.getUniqueId())
                .orderBy("Time", SearchOrder.DESC)
                .index(startIndex, lastIndex)
                .execute());
    }

    @Override
    public List<PlayerNote> getSincePlayerNotes(DKBansPlayer player, long time) {
        return getPlayerNotesByResult(player, this.playerNotes.find()
                .where("PlayerId", player.getUniqueId())
                .whereHigher("Time", time)
                .orderBy("Time", SearchOrder.ASC)
                .execute());
    }

    @Override
    public List<PlayerNote> getUntilPlayerNotes(DKBansPlayer player, long time) {
        return getPlayerNotesByResult(player, this.playerNotes.find()
                .where("PlayerId", player.getUniqueId())
                .whereLower("Time", time)
                .orderBy("Time", SearchOrder.ASC)
                .execute());
    }

    @Override
    public List<PlayerNote> getBetweenPlayerNotes(DKBansPlayer player, long startTime, long endTime) {
        return getPlayerNotesByResult(player, this.playerNotes.find()
                .where("PlayerId", player.getUniqueId())
                .whereBetween("Time", startTime, endTime)
                .orderBy("Time", SearchOrder.ASC)
                .execute());
    }

    @Override
    public List<PlayerNote> getPagePlayerNotes(DKBansPlayer player, int page, int sizePerPage) {
        return getPlayerNotesByResult(player, this.playerNotes.find()
                .where("PlayerId", player.getUniqueId())
                .page(page,sizePerPage)
                .orderBy("Time", SearchOrder.ASC)
                .execute());
    }

    private List<PlayerNote> getPlayerNotesByResult(DKBansPlayer player, QueryResult result) {
        List<PlayerNote> sessions = new ArrayList<>();
        result.loadIn(sessions, entry -> getPlayerNoteByResultEntry(player, entry));
        return sessions;
    }

    private PlayerNote getPlayerNoteByResultEntry(DKBansPlayer player, QueryResultEntry entry) {
        if(entry == null) return null;
        return new DefaultPlayerNote(entry.getInt("Id"),
                PlayerNoteType.byId(entry.getInt("TypeId")),
                entry.getLong("Time"),
                entry.getString("Message"),
                player);
    }

    public DatabaseCollection getPlayerSessions() {
        return playerSessions;
    }

    public DatabaseCollection getPlayerChatLog() {
        return playerChatLog;
    }

    public DatabaseCollection getPlayerSettings() {
        return playerSettings;
    }

    public DatabaseCollection getPlayerNotes() {
        return playerNotes;
    }

    public DatabaseCollection getHistoryType() {
        return historyType;
    }

    public DatabaseCollection getHistory() {
        return history;
    }

    public DatabaseCollection getHistoryVersion() {
        return historyVersion;
    }

    public DatabaseCollection getHistoryNotes() {
        return historyNotes;
    }

    public DatabaseCollection getReports() {
        return reports;
    }

    public DatabaseCollection getReportEntries() {
        return reportEntries;
    }

    public DatabaseCollection getIpAddressBlacklist() {
        return ipAddressBlacklist;
    }

    public DatabaseCollection getTemplateCategories() {
        return templateCategories;
    }

    public DatabaseCollection getTemplateGroups() {
        return templateGroups;
    }

    public DatabaseCollection getTemplate() {
        return template;
    }

    public DatabaseCollection getFilter() {
        return filter;
    }

    public DatabaseCollection getBroadcast() {
        return broadcast;
    }

    public DatabaseCollection getBroadcastGroupAssignment() {
        return broadcastGroupAssignment;
    }

    public DatabaseCollection getBroadcastGroup() {
        return broadcastGroup;
    }

    private DatabaseCollection createPlayerSessionsCollection() {
        return database.createCollection("dkbans_player_sessions")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("PlayerSessionName", DataType.STRING, FieldOption.NOT_NULL)
                .field("IpAddress", DataType.STRING, FieldOption.NOT_NULL)
                .field("Country", DataType.STRING, FieldOption.NOT_NULL)
                .field("Region", DataType.STRING, FieldOption.NOT_NULL)
                .field("LastServerId", DataType.UUID)
                .field("LastServerName", DataType.STRING)
                .field("ProxyId", DataType.UUID)
                .field("ProxyName", DataType.STRING)
                .field("ClientEdition", DataType.STRING, FieldOption.NOT_NULL)
                .field("ClientProtocolVersion", DataType.INTEGER, FieldOption.NOT_NULL)
                .field("ConnectTime", DataType.LONG, FieldOption.NOT_NULL)
                .field("DisconnectTime", DataType.LONG)
                .create();
    }

    private DatabaseCollection createPlayerChatLogCollection() {
        return database.createCollection("dkbans_player_chatlog")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Message", DataType.STRING, FieldOption.NOT_NULL)
                .field("Time", DataType.LONG, FieldOption.NOT_NULL)
                .field("ServerName", DataType.STRING, FieldOption.NOT_NULL)
                .field("ServerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("FilterAffiliationArea", DataType.STRING)
                .create();
    }

    private DatabaseCollection createPlayerSettingsCollection() {
        return database.createCollection("dkbans_player_settings")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Key", DataType.STRING, FieldOption.NOT_NULL)
                .field("Value", DataType.STRING, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createPlayerNotesCollection() {
        return database.createCollection("dkbans_player_notes")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("CreatorId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Time", DataType.LONG, FieldOption.NOT_NULL)
                .field("Message", DataType.STRING, FieldOption.NOT_NULL)
                .field("TypeId", DataType.INTEGER, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createHistoryTypeCollection() {
        return database.createCollection("dkbans_history_type")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("Name", DataType.STRING, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createHistoryCollection() {
        return database.createCollection("dkbans_history")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("SessionId", DataType.INTEGER, ForeignKey.of(this.playerSessions, "Id"))
                .field("Created", DataType.LONG, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createHistoryVersionCollection() {
        return database.createCollection("dkbans_history_version")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("HistoryId", DataType.INTEGER, ForeignKey.of(this.history, "Id", ForeignKey.Option.CASCADE), FieldOption.NOT_NULL)
                .field("Reason", DataType.STRING, FieldOption.NOT_NULL)
                .field("Timeout", DataType.LONG, FieldOption.NOT_NULL)
                .field("StaffId", DataType.UUID, FieldOption.NOT_NULL)
                .field("ScopeType", DataType.STRING)
                .field("ScopeName", DataType.STRING)
                .field("ScopeId", DataType.UUID)
                .field("Points", DataType.INTEGER)
                .field("Active", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Properties", DataType.LONG_TEXT, -1, FieldOption.NOT_NULL)
                .field("HistoryTypeId", DataType.INTEGER, ForeignKey.of(this.historyType, "Id"))
                .field("PunishmentType", DataType.STRING, FieldOption.NOT_NULL)
                .field("TemplateId", DataType.INTEGER, ForeignKey.of(this.template, "Id"))
                .field("RevokeTemplateId", DataType.INTEGER, ForeignKey.of(this.template, "Id"))
                .field("RevokeReason", DataType.STRING)
                .field("ModifiedTime", DataType.LONG, FieldOption.NOT_NULL)
                .field("ModifiedBy", DataType.UUID, FieldOption.NOT_NULL)
                .field("ModifiedActive", DataType.BOOLEAN)
                .create();
    }

    private DatabaseCollection createHistoryNotesCollection() {
        return database.createCollection("dkbans_history_notes")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("HistoryId", DataType.INTEGER, ForeignKey.of(this.history, "Id"), FieldOption.NOT_NULL)
                .field("SenderId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Time", DataType.LONG, FieldOption.NOT_NULL)
                .field("Message", DataType.STRING, FieldOption.NOT_NULL)
                .field("TypeId", DataType.INTEGER, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createReportsCollection() {
        return database.createCollection("dkbans_report")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("WatcherId", DataType.UUID)
                .field("State", DataType.STRING, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createReportEntriesCollection() {
        return database.createCollection("dkbans_report_entries")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("ReportId", DataType.INTEGER, ForeignKey.of(this.reports, "Id"))
                .field("ReporterId", DataType.UUID, FieldOption.NOT_NULL)
                .field("TemplateId", DataType.INTEGER, ForeignKey.of(this.template, "Id"))
                .field("Reason", DataType.STRING)
                .field("ServerName", DataType.STRING, FieldOption.NOT_NULL)
                .field("ServerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Time", DataType.LONG, FieldOption.NOT_NULL)
                .field("Properties", DataType.LONG_TEXT, -1, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createIpAddressBlacklistCollection() {
        return database.createCollection("dkbans_ipaddress_blacklist")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("Address", DataType.STRING, FieldOption.NOT_NULL)
                .field("Type", DataType.STRING, FieldOption.NOT_NULL)
                .field("StaffId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Reason", DataType.STRING, FieldOption.NOT_NULL)
                .field("Timeout", DataType.LONG, FieldOption.NOT_NULL)
                .field("ForReason", DataType.STRING)
                .field("ForDuration", DataType.LONG)
                .field("ForTemplateId", DataType.INTEGER, ForeignKey.of(this.template, "Id"))
                .create();
    }

    private DatabaseCollection createTemplateCategoriesCollection() {
        return database.createCollection("dkbans_template_categories")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("Name", DataType.STRING, FieldOption.NOT_NULL)
                .field("DisplayName", DataType.STRING, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createTemplateGroupsCollection() {
        return database.createCollection("dkbans_template_groups")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("Name", DataType.STRING, FieldOption.NOT_NULL)
                .field("TemplateType", DataType.STRING, FieldOption.NOT_NULL)
                .field("CalculationType", DataType.STRING, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createTemplateCollection() {
        return database.createCollection("dkbans_template")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("InGroupId", DataType.INTEGER, FieldOption.NOT_NULL)
                .field("Name", DataType.STRING, FieldOption.NOT_NULL)
                .field("DisplayName", DataType.STRING, FieldOption.NOT_NULL)
                .field("Permission", DataType.STRING, FieldOption.NOT_NULL)
                .field("Aliases", DataType.STRING, FieldOption.NOT_NULL)
                .field("HistoryTypeId", DataType.INTEGER, ForeignKey.of(this.historyType, "Id"))
                .field("Enabled", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Hidden", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("CategoryId", DataType.INTEGER, ForeignKey.of(this.templateCategories, "Id"), FieldOption.NOT_NULL)
                .field("GroupId", DataType.INTEGER, ForeignKey.of(this.templateGroups, "id"), FieldOption.NOT_NULL)
                .field("Data", DataType.LONG_TEXT, -1, FieldOption.NOT_NULL)
                .create();
    }
    private DatabaseCollection createFilterCollection(){
        return database.createCollection("dkbans_filter")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("AffiliationArea", DataType.STRING,32, FieldOption.NOT_NULL)
                .field("Operation", DataType.STRING,32, FieldOption.NOT_NULL)
                .field("Value", DataType.STRING,512, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createBroadcastCollection() {
        return database.createCollection("dkbans_broadcast")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("Name", DataType.STRING, FieldOption.NOT_NULL, FieldOption.UNIQUE)
                .field("Visibility", DataType.STRING, FieldOption.NOT_NULL)
                .field("Properties", DataType.LONG_TEXT, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createBroadcastGroupCollection() {
        return database.createCollection("dkbans_broadcast_group")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("Name", DataType.STRING, FieldOption.NOT_NULL, FieldOption.UNIQUE)
                .field("Enabled", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Permission", DataType.STRING)
                .field("Interval", DataType.LONG, FieldOption.NOT_NULL)
                .field("Order", DataType.STRING, FieldOption.NOT_NULL)
                .field("ScopeType", DataType.STRING)
                .field("ScopeName", DataType.STRING)
                .field("ScopeId", DataType.UUID)
                .field("Properties", DataType.LONG_TEXT, FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createBroadcastGroupAssignment() {
        return database.createCollection("dkbans_broadcast_group_assignment")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("BroadcastId", DataType.INTEGER, ForeignKey.of(this.broadcast, "Id"), FieldOption.NOT_NULL)
                .field("GroupId", DataType.INTEGER, ForeignKey.of(this.broadcastGroup, "Id"), FieldOption.NOT_NULL)
                .field("Position", DataType.INTEGER, FieldOption.NOT_NULL)
                .create();
    }
}
