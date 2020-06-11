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
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.storage.DKBansStorage;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntry;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryType;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.dkbans.common.template.DefaultTemplateCategory;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.map.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DefaultDKBansStorage implements DKBansStorage {

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
    /*private final DatabaseCollection filter;
    private final DatabaseCollection broadcast;
    private final DatabaseCollection broadcastGroupAssignment;
    private final DatabaseCollection broadcastGroup;
    private final DatabaseCollection supportTicket;
    private final DatabaseCollection supportTicketParticipants;
    private final DatabaseCollection supportTicketMessages;*/

    public DefaultDKBansStorage(Database database) {
        this.database = database;
        this.playerSessions = createPlayerSessionsCollection();
        this.playerChatLog = createPlayerChatLogCollection();
        this.playerSettings = createPlayerSettingsCollection();
        this.playerNotes = createPlayerNotesCollection();
        this.historyType = createHistoryTypeCollection();
        this.history = createHistoryCollection();
        this.historyVersion = createHistoryVersionCollection();
        this.historyNotes = createHistoryNotesCollection();
        this.reports = createReportsCollection();
        this.reportEntries = createReportEntriesCollection();
        this.ipAddressBlacklist = createIpAddressBlacklistCollection();
        this.templateCategories = createTemplateCategoriesCollection();
        this.templateGroups = createTemplateGroupsCollection();

        this.template = createTemplateCollection();

    }

    @Override
    public Collection<PlayerNote> loadPlayerNotes(UUID uniqueId) {
        return null;
    }

    @Override
    public int createPlayerNote(UUID playerId, UUID creatorId, PlayerNoteType type, String message) {
        return 0;
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

            for (QueryResultEntry subResultEntry : this.template.find().where("GroupId").execute()) {
                String name = subResultEntry.getString("Name");

                Collection<String> aliases = DocumentFileType.JSON.getReader().read(subResultEntry.getString("Aliases")).getAsCollection(String.class);
                TemplateCategory category = DKBans.getInstance().getTemplateManager().getTemplateCategory(subResultEntry.getInt("CategoryId"));
                Collection<DefaultDKBansScope> scopes = DocumentFileType.JSON.getReader().read(subResultEntry.getString("Scopes")).getAsCollection(DefaultDKBansScope.class);
                templateGroup.addTemplateInternal(TemplateFactory.create(templateType,
                        subResultEntry.getInt("Id"),
                        name,
                        templateGroup,
                        subResultEntry.getString("DisplayName"),
                        subResultEntry.getString("Permission"),
                        aliases,
                        DKBans.getInstance().getHistoryManager().getHistoryType(subResultEntry.getInt("HistoryTypeId")),
                        subResultEntry.getBoolean("Enabled"),
                        subResultEntry.getBoolean("Hidden"),
                        scopes,
                        category,
                        DocumentFileType.JSON.getReader().read("Data")));
            }
            templateGroups.add(templateGroup);
        }
        return templateGroups;
    }

    @Override
    public void importTemplateGroup(TemplateGroup templateGroup) {
        FindQuery groupExist = this.templateGroups.find().where("Name", templateGroup.getName())
                .where("TemplateType", templateGroup.getTemplateType().getName());
        if(templateGroup.getId() != -1) {
            groupExist.where("Id", templateGroup.getId());
        }
        if(groupExist.execute().isEmpty()) {
            int id = templateGroups.insert().set("Name", templateGroup.getName())
                    .set("Type", templateGroup.getName())
                    .executeAndGetGeneratedKeyAsInt("Id");
            ((DefaultTemplateGroup)templateGroup).setIdInternal(id);
        }
        for (Template template : templateGroup.getTemplates()) {
            boolean exist = !this.template.find().where("Id", template.getId())
                    .where("Name", template.getName())
                    .where("GroupId", templateGroup.getId())
                    .execute().isEmpty();
            if(exist) {
                this.template.update().set("DisplayName", template.getDisplayName())
                        .set("Permission", template.getPermission())
                        .set("Aliases", template.getAliases())
                        .set("HistoryTypeId", template.getHistoryType().getId())
                        .set("Enabled", template.isEnabled())
                        .set("Hidden", template.isHidden())
                        .set("Scopes", DocumentFileType.JSON.getWriter().write(Document.newDocument(template.getScopes()), false))
                        .set("CategoryId", template.getCategory().getId())
                        .set("Data", DocumentFileType.JSON.getWriter().write(TemplateFactory.toData(template), false))
                        .where("Id", template.getId())
                        .execute();
            } else {
                int id = this.template.insert()
                        .set("Name", template.getName())
                        .set("DisplayName", template.getDisplayName())
                        .set("Permission", template.getPermission())
                        .set("Aliases", template.getAliases())
                        .set("HistoryTypeId", template.getHistoryType().getId())
                        .set("Enabled", template.isEnabled())
                        .set("Hidden", template.isHidden())
                        .set("Scopes", DocumentFileType.JSON.getWriter().write(Document.newDocument(template.getScopes()), false))
                        .set("CategoryId", template.getCategory().getId())
                        .set("Data", DocumentFileType.JSON.getWriter().write(TemplateFactory.toData(template), false))
                        .set("GroupId", templateGroup.getId())
                        .executeAndGetGeneratedKeyAsInt("Id");

                ((DefaultTemplate)template).setIdInternal(id);
            }
        }
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
        historyVersion.update().set("ModifiedActive").where("HistoryId",snapshot.getEntry().getId()).execute();
        return buildSnapshotQuery(snapshot.getEntry().getId(),snapshot);
    }

    private int buildSnapshotQuery(int historyId,PlayerHistoryEntrySnapshot snapshot){
       InsertQuery query = historyVersion.insert()
                .set("HistoryId",historyId)
                .set("Reason",snapshot.getReason())
                .set("Timeout",snapshot.getTimeout())
                .set("StaffId",snapshot.getStuff().getUniqueId())
                .set("Points",snapshot.getPoints())
                .set("Active",snapshot.isActive())
                .set("Properties","{}")
                .set("PunishmentType",snapshot.getPunishmentType().getName())
                .set("RevokeReason",snapshot.getReason())
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
        QueryResult result0 = history.find()
                .getAs(this.history, "Id", "HistoryId")
                .getAs(this.historyVersion, "Id", "SnapshotId")
                .get("Created", "Reason", "Timeout", "StaffId", "ScopeType", "ScopeName", "ScopeId", "Points", "Active", "Properties",
                        "HistoryTypeId", "PunishmentType", "TemplateId", "RevokeTemplateId", "RevokeReason", "ModifiedTime", "ModifiedBy", "ModifiedActive")
                .join(historyVersion).on(historyVersion,"HistoryId",history,"Id")
                .where("ModifiedActive",true).where("Active",true)
                .or(query -> query.where("Timeout",-1).whereLower("Timeout",System.currentTimeMillis()))
                .orderBy("ModifiedTime", SearchOrder.DESC)
                .execute();

        if(!result0.isEmpty()){
            for (QueryResultEntry resultEntry : result0) {
                DefaultPlayerHistoryEntrySnapshot snapshot = new DefaultPlayerHistoryEntrySnapshot(null,
                        resultEntry.getInt("SnapshotId"),
                        DKBans.getInstance().getHistoryManager().getHistoryType(resultEntry.getInt("HistoryTypeId")),
                        PunishmentType.getPunishmentType(resultEntry.getString("PunishmentType")),
                        resultEntry.getString("Reason"),
                        resultEntry.getLong("Timeout"),
                        DKBans.getInstance().getTemplateManager().getTemplate(resultEntry.getInt("TemplateId")),
                        null/*@Todo add stuff*/,
                        new DefaultDKBansScope(resultEntry.getString("ScopeType"), resultEntry.getString("ScopeName"), resultEntry.getUniqueId("ScopeId")),
                        resultEntry.getInt("Points"),
                        resultEntry.getBoolean("Active"),
                        null/*@Todo add properties*/,
                        resultEntry.getString("RevokeReason"),
                        DKBans.getInstance().getTemplateManager().getTemplate(resultEntry.getInt("RevokeTemplateId")),
                        resultEntry.getBoolean("ModifiedActive"),
                        resultEntry.getLong("ModifiedTime"),
                        null/*@Todo add modified by*/);
                DefaultPlayerHistoryEntry entry = new DefaultPlayerHistoryEntry(playerHistory,
                        resultEntry.getInt("HistoryId"),
                        snapshot,
                        resultEntry.getLong("Created"));
                snapshot.setEntry(entry);
                result.add(entry);
            }
        }
        return result;
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
    public TemplateGroup createTemplateGroup(String name, TemplateType templateType, CalculationType calculationType) {
        int id = this.templateGroups.insert().set("Name", name)
                .set("TemplateType", templateType.getName())
                .set("CalculationType", calculationType)
                .executeAndGetGeneratedKeyAsInt("id");
        return new DefaultTemplateGroup(id, name, templateType, calculationType);
    }

    private DatabaseCollection createPlayerSessionsCollection() {
        return database.createCollection("dkbans_player_sessions")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("SessionName", DataType.STRING, FieldOption.NOT_NULL)
                .field("IpAddress", DataType.STRING, FieldOption.NOT_NULL)
                .field("Country", DataType.STRING, FieldOption.NOT_NULL)
                .field("Region", DataType.STRING, FieldOption.NOT_NULL)
                .field("LastServerName", DataType.STRING, FieldOption.NOT_NULL)
                .field("LastServerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("ProxyName", DataType.STRING, FieldOption.NOT_NULL)
                .field("ProxyId", DataType.UUID, FieldOption.NOT_NULL)
                .field("ClientProtocolVersion", DataType.INTEGER, FieldOption.NOT_NULL)
                .field("ClientLanguage", DataType.STRING, FieldOption.NOT_NULL)
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
                .field("FilterId", DataType.INTEGER)//@Todo foreign key filter collection
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
                .field("SenderId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Time", DataType.LONG, FieldOption.NOT_NULL)
                .field("Message", DataType.STRING, FieldOption.NOT_NULL)
                .field("Type", DataType.STRING, FieldOption.NOT_NULL)//@Todo maybe id
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
                .field("HistoryId", DataType.INTEGER, ForeignKey.of(this.history, "Id"), FieldOption.NOT_NULL)
                .field("Reason", DataType.STRING, FieldOption.NOT_NULL)
                .field("Timeout", DataType.LONG, FieldOption.NOT_NULL)
                .field("StaffId", DataType.UUID, FieldOption.NOT_NULL)
                .field("ScopeType", DataType.STRING, FieldOption.NOT_NULL)
                .field("ScopeName", DataType.STRING, FieldOption.NOT_NULL)
                .field("ScopeId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Points", DataType.INTEGER)
                .field("Active", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Properties", DataType.LONG_TEXT, -1, "{}", FieldOption.NOT_NULL)
                .field("HistoryTypeId", DataType.INTEGER, ForeignKey.of(this.historyType, "Id"))
                .field("PunishmentType", DataType.INTEGER, FieldOption.NOT_NULL)//@Todo foreign key maybe / save as string
                .field("TemplateId", DataType.INTEGER)//@Todo add foreign key
                .field("RevokeTemplateId", DataType.INTEGER)//@Todo add foreign key
                .field("RevokeReason", DataType.STRING)
                .field("ModifiedTime", DataType.LONG)//@Todo nullAble ?
                .field("ModifiedBy", DataType.UUID)//@Todo nullAble ?
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
                .field("Type", DataType.STRING, FieldOption.NOT_NULL)//@Todo maybe id
                .create();
    }

    private DatabaseCollection createReportsCollection() {
        return database.createCollection("dkbans_reports")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("PlayerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("WatcherId", DataType.UUID, FieldOption.NOT_NULL)
                .field("State", DataType.STRING, FieldOption.NOT_NULL)//@Todo maybe id
                .create();
    }

    private DatabaseCollection createReportEntriesCollection() {
        return database.createCollection("dkbans_report_entries")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("ReportId", DataType.INTEGER, ForeignKey.of(this.reports, "Id"))
                .field("ReporterId", DataType.UUID, FieldOption.NOT_NULL)
                .field("TemplateId", DataType.INTEGER)//@Todo add foreign key
                .field("Reason", DataType.STRING, FieldOption.NOT_NULL)
                .field("ServerName", DataType.STRING, FieldOption.NOT_NULL)
                .field("ServerId", DataType.UUID, FieldOption.NOT_NULL)
                .field("Time", DataType.LONG, FieldOption.NOT_NULL)
                .field("Properties", DataType.LONG_TEXT, -1, "{}", FieldOption.NOT_NULL)
                .create();
    }

    private DatabaseCollection createIpAddressBlacklistCollection() {
        return database.createCollection("dkbans_ipaddress_blacklist")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("IpAddress", DataType.STRING, FieldOption.NOT_NULL)
                .field("Reason", DataType.STRING, FieldOption.NOT_NULL)
                .field("Timeout", DataType.LONG, FieldOption.NOT_NULL)
                .field("ExecutorId", DataType.UUID, FieldOption.NOT_NULL)
                .field("TemplateId", DataType.INTEGER)//@Todo add foreign key
                .field("Type", DataType.STRING, FieldOption.NOT_NULL)//@Todo maybe id
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
                .field("Name", DataType.STRING, FieldOption.NOT_NULL)
                .field("DisplayName", DataType.STRING, FieldOption.NOT_NULL)
                .field("Permission", DataType.STRING, FieldOption.NOT_NULL)
                .field("Aliases", DataType.STRING, FieldOption.NOT_NULL)
                .field("HistoryTypeId", DataType.INTEGER, ForeignKey.of(this.historyType, "Id"), FieldOption.NOT_NULL)
                .field("Enabled", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Hidden", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Scopes", DataType.STRING, FieldOption.NOT_NULL)
                .field("CategoryId", DataType.INTEGER, ForeignKey.of(this.templateCategories, "Id"), FieldOption.NOT_NULL)
                .field("GroupId", DataType.INTEGER, ForeignKey.of(this.templateGroups, "id"), FieldOption.NOT_NULL)
                .field("Data", DataType.LONG_TEXT, -1, "{}", FieldOption.NOT_NULL)
                .create();
    }
}
