package net.pretronic.dkbans.common.player.report;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

import java.util.UUID;

public class DefaultPlayerReportEntry implements PlayerReportEntry {

    private final int id;
    private final PlayerReport report;
    private final DKBansExecutor reporter;
    private final ReportTemplate template;
    private final String reason;
    private final String serverName;
    private final UUID serverId;
    private final long time;
    private final Document properties;

    public DefaultPlayerReportEntry(int id, PlayerReport report, DKBansExecutor reporter, ReportTemplate template, String reason,
                                    String serverName, UUID serverId, long time, Document properties) {
        Validate.notNull(report, reporter);
        this.id = id;
        this.report = report;
        this.reporter = reporter;
        this.template = template;
        this.reason = reason;
        this.serverName = serverName;
        this.serverId = serverId;
        this.time = time;
        this.properties = properties;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public PlayerReport getReport() {
        return this.report;
    }

    @Override
    public DKBansExecutor getReporter() {
        return this.reporter;
    }

    @Override
    public ReportTemplate getTemplate() {
        return this.template;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }

    @Override
    public UUID getServerId() {
        return this.serverId;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public Document getProperties() {
        return this.properties;
    }
}
