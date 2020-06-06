package net.pretronic.dkbans.common;

import net.pretronic.databasequery.api.Database;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.broadcast.BroadcastManager;
import net.pretronic.dkbans.api.filter.FilterManager;
import net.pretronic.dkbans.api.player.history.PlayerHistoryManager;
import net.pretronic.dkbans.api.player.report.ReportManager;
import net.pretronic.dkbans.api.storage.DKBansStorage;
import net.pretronic.dkbans.api.support.SupportTicketManager;
import net.pretronic.dkbans.api.template.TemplateManager;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryManager;
import net.pretronic.dkbans.common.storage.DefaultDKBansStorage;
import net.pretronic.dkbans.common.template.DefaultTemplateManager;
import net.pretronic.libraries.logging.PretronicLogger;

import java.util.Map;

public class DefaultDKBans extends DKBans {

    private final PretronicLogger logger;
    private final DKBansStorage storage;
    private final PlayerHistoryManager historyManager;
    private final ReportManager reportManager;
    private final SupportTicketManager ticketManager;
    private final BroadcastManager broadcastManager;
    private final FilterManager filterManager;
    private final TemplateManager templateManager;

    public DefaultDKBans(PretronicLogger logger, Database database) {
        this.logger = logger;
        this.storage = new DefaultDKBansStorage(database);
        this.historyManager = new DefaultPlayerHistoryManager(this);
        this.reportManager = null;
        this.ticketManager = null;
        this.broadcastManager = null;
        this.filterManager = null;
        this.templateManager = new DefaultTemplateManager();
    }

    @Override
    public PretronicLogger getLogger() {
        return this.logger;
    }

    @Override
    public DKBansStorage getStorage() {
        return this.storage;
    }

    @Override
    public PlayerHistoryManager getHistoryManager() {
        return this.historyManager;
    }

    @Override
    public ReportManager getReportManager() {
        return this.reportManager;
    }

    @Override
    public SupportTicketManager getTicketManager() {
        return this.ticketManager;
    }

    @Override
    public BroadcastManager getBroadcastManager() {
        return this.broadcastManager;
    }

    @Override
    public FilterManager getFilterManager() {
        return this.filterManager;
    }

    @Override
    public TemplateManager getTemplateManager() {
        return this.templateManager;
    }
}
