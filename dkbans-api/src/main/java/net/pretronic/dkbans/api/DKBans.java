package net.pretronic.dkbans.api;

import net.pretronic.dkbans.api.broadcast.BroadcastManager;
import net.pretronic.dkbans.api.filter.FilterManager;
import net.pretronic.dkbans.api.player.history.PlayerHistoryManager;
import net.pretronic.dkbans.api.player.report.ReportManager;
import net.pretronic.dkbans.api.storage.DKBansStorage;
import net.pretronic.dkbans.api.support.SupportTicketManager;
import net.pretronic.dkbans.api.template.PunishmentTemplateManager;
import net.pretronic.libraries.logging.PretronicLogger;

public abstract class DKBans {

    private static DKBans INSTANCE;

    public abstract PretronicLogger getLogger();

    public abstract DKBansStorage getStorage();


    public abstract PlayerHistoryManager getHistoryManager();

    public abstract ReportManager getReportManager();

    public abstract SupportTicketManager getTicketManager();


    public abstract BroadcastManager getBroadcastManager();

    public abstract FilterManager getFilterManager();

    public abstract PunishmentTemplateManager getTemplateManager();





    public static DKBans getInstance() {
        return INSTANCE;
    }

    public static void setInstance(DKBans instance) {
        if(INSTANCE != null) throw new IllegalArgumentException("Instance is already set");
        INSTANCE = instance;
    }
}
