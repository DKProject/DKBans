package net.pretronic.dkbans.common.storage;

import net.pretronic.databasequery.api.Database;
import net.pretronic.databasequery.api.collection.DatabaseCollection;
import net.pretronic.databasequery.api.collection.field.FieldOption;
import net.pretronic.databasequery.api.datatype.DataType;
import net.pretronic.databasequery.api.query.ForeignKey;
import net.pretronic.dkbans.api.storage.DKBansStorage;

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
        this.template = createTemplateCollection();

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
                .field("ModifiedTime", DataType.LONG)//@Todo nullAble ?
                .field("ModifiedBy", DataType.UUID)//@Todo nullAble ?
                .field("Active", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Properties", DataType.STRING, -1, "{}", FieldOption.NOT_NULL)
                .field("HistoryTypeId", DataType.INTEGER, ForeignKey.of(this.historyType, "Id"), FieldOption.NOT_NULL)
                .field("PunishmentType", DataType.INTEGER, FieldOption.NOT_NULL)//@Todo foreign key maybe / save as string
                .field("TemplateId", DataType.INTEGER)//@Todo add foreign key
                .field("RevokeTemplateId", DataType.INTEGER)//@Todo add foreign key
                .field("RevokeReason", DataType.STRING)
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
                .field("Properties", DataType.STRING, -1, "{}", FieldOption.NOT_NULL)
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

    private DatabaseCollection createTemplateCollection() {
        return database.createCollection("dkbans_template")
                .field("Id", DataType.INTEGER, FieldOption.PRIMARY_KEY, FieldOption.AUTO_INCREMENT)
                .field("CategoryId", DataType.INTEGER, ForeignKey.of(this.templateCategories, "Id"), FieldOption.NOT_NULL)
                .field("Name", DataType.STRING, FieldOption.NOT_NULL)
                .field("DisplayName", DataType.STRING, FieldOption.NOT_NULL)
                .field("HistoryTypeId", DataType.INTEGER, ForeignKey.of(this.historyType, "Id"), FieldOption.NOT_NULL)
                .field("PunishmentType", DataType.INTEGER, FieldOption.NOT_NULL)//@Todo foreign key maybe / save as string
                .field("Enabled", DataType.BOOLEAN, FieldOption.NOT_NULL)
                .field("Data", DataType.STRING, -1, "{}", FieldOption.NOT_NULL)
                .create();
    }
}