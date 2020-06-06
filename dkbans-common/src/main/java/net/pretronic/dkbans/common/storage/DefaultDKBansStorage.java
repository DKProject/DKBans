package net.pretronic.dkbans.common.storage;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.api.query.type.SearchQuery;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.PlayerSetting;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.storage.DKBansStorage;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.DefaultTemplateCategory;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.libraries.document.type.DocumentFileType;

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

        /*for (QueryResultEntry resultEntry : this.template.find().execute()) {
            String name = resultEntry.getString("Name");
            String type = resultEntry.getString("Type");
            TemplateType templateType = TemplateType.byName(type);

            Collection<String> aliases = DocumentFileType.JSON.getReader().read(resultEntry.getString("Aliases")).getAsCollection(String.class);
            TemplateCategory category = DKBans.getInstance().getTemplateManager().getTemplateCategory(resultEntry.getInt("CategoryId"));
            Collection<DefaultDKBansScope> scopes = DocumentFileType.JSON.getReader().read(resultEntry.getString("Scopes")).getAsCollection(DefaultDKBansScope.class);
            templates.add(TemplateFactory.create(templateType,
                    resultEntry.getInt("Id"),
                    name,
                    resultEntry.getString("DisplayName"),
                    resultEntry.getString("Permission"),
                    aliases,
                    DKBans.getInstance().getHistoryManager().getHistoryType(resultEntry.getInt("HistoryTypeId")),
                    PunishmentType.getPunishmentType(resultEntry.getInt("PunishmentTypeId")),
                    resultEntry.getBoolean("Enabled"),
                    resultEntry.getBoolean("Hidden"),
                    scopes,
                    category,
                    DocumentFileType.JSON.getReader().read("Data")));
        }*/
        for (QueryResultEntry resultEntry : this.templateGroups.find().execute()) {
            int groupId = resultEntry.getInt("Id");
            String groupName = resultEntry.getString("Name");

            DefaultTemplateGroup templateGroup = new DefaultTemplateGroup(groupId, groupName);

            for (QueryResultEntry subResultEntry : this.template.find().where("GroupId").execute()) {
                String name = subResultEntry.getString("Name");
                String type = subResultEntry.getString("Type");
                TemplateType templateType = TemplateType.byName(type);

                Collection<String> aliases = DocumentFileType.JSON.getReader().read(subResultEntry.getString("Aliases")).getAsCollection(String.class);
                TemplateCategory category = DKBans.getInstance().getTemplateManager().getTemplateCategory(subResultEntry.getInt("CategoryId"));
                Collection<DefaultDKBansScope> scopes = DocumentFileType.JSON.getReader().read(subResultEntry.getString("Scopes")).getAsCollection(DefaultDKBansScope.class);
                templateGroup.addTemplateInternal(TemplateFactory.create(templateType,
                        subResultEntry.getInt("Id"),
                        name,
                        subResultEntry.getString("DisplayName"),
                        subResultEntry.getString("Permission"),
                        aliases,
                        DKBans.getInstance().getHistoryManager().getHistoryType(subResultEntry.getInt("HistoryTypeId")),
                        PunishmentType.getPunishmentType(subResultEntry.getInt("PunishmentTypeId")),
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
    public int createHistoryEntry(int playerId, int sessionId) {
        return 0;
    }

    @Override
    public int insertHistoryEntrySnapshot(PlayerHistoryEntrySnapshot snapshot) {
        return 0;
    }

    @Override
    public List<PlayerHistoryEntry> loadActiveEntries(UUID uniqueId) {
        List<PlayerHistoryEntry> result = new ArrayList<>();
        QueryResult result0 = history.find()
                .join(historyVersion).on(historyVersion,"HistoryId",history,"Id")
                .where("ModifiedActive",true).where("Active",true)
                .or(query -> query.where("Timeout",-1).whereLower("Timeout",System.currentTimeMillis()))
                .orderBy("ModifiedTime", SearchOrder.ASC)
                .execute();
        if(!result0.isEmpty()){
            for (QueryResultEntry entry : result0) {

            }
        }
        return result;
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
                .field("SessionId", DataType.INTEGER, ForeignKey.of(this.playerSessions, "Id"), FieldOption.NOT_NULL)
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
                .field("HistoryTypeId", DataType.INTEGER, ForeignKey.of(this.historyType, "Id"), FieldOption.NOT_NULL)
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
                .create();
    }

    private DatabaseCollection createTemplateCollection() {
        return database.createCollection("dkbans_template")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("Type", DataType.STRING, FieldOption.NOT_NULL)
                .field("Name", DataType.STRING, FieldOption.NOT_NULL)
                .field("DisplayName", DataType.STRING, FieldOption.NOT_NULL)
                .field("Permission", DataType.STRING, FieldOption.NOT_NULL)
                .field("Aliases", DataType.STRING, FieldOption.NOT_NULL)
                .field("HistoryTypeId", DataType.INTEGER, ForeignKey.of(this.historyType, "Id"), FieldOption.NOT_NULL)
                .field("PunishmentTypeId", DataType.INTEGER, FieldOption.NOT_NULL)//@Todo foreign key maybe / save as string
                .field("Enabled", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Hidden", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Scopes", DataType.STRING, FieldOption.NOT_NULL)
                .field("CategoryId", DataType.INTEGER, ForeignKey.of(this.templateCategories, "Id"), FieldOption.NOT_NULL)
                .field("GroupId", DataType.INTEGER, ForeignKey.of(this.templateGroups, "id"), FieldOption.NOT_NULL)
                .field("Data", DataType.LONG_TEXT, -1, "{}", FieldOption.NOT_NULL)
                .create();
    }
}
