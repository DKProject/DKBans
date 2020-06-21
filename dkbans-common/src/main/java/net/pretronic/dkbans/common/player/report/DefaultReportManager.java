package net.pretronic.dkbans.common.player.report;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.report.ReportManager;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultReportManager implements ReportManager {

    private final Collection<PlayerReport> openReports;

    public DefaultReportManager() {
        this.openReports = new ArrayList<>();
    }

    @Override
    public Collection<PlayerReport> getOpenReports() {
        return this.openReports;
    }

    @Override
    public PlayerReportEntry report(DKBansPlayer executor, DKBansPlayer target, ReportTemplate template, DKBansScope scope) {
        Validate.notNull(executor, target, template);
        DefaultPlayerReport report = getReport(target);
        DefaultPlayerReportEntry entry = (DefaultPlayerReportEntry) DKBans.getInstance().getStorage().createPlayerReportEntry(report, executor, template, scope);
        report.addEntry(entry);
        return entry;
    }

    @Override
    public PlayerReportEntry report(DKBansPlayer executor, DKBansPlayer target, String reason, DKBansScope scope) {
        Validate.notNull(executor, target);
        DefaultPlayerReport report = getReport(target);
        DefaultPlayerReportEntry entry = (DefaultPlayerReportEntry) DKBans.getInstance().getStorage().createPlayerReportEntry(report, executor, reason, scope);
        report.addEntry(entry);
        return entry;
    }

    private DefaultPlayerReport getReport(DKBansPlayer target) {
        PlayerReport report0 = Iterators.findOne(this.openReports, target0 -> target0.getPlayer().equals(target));
        if(report0 == null) {
            report0 = DKBans.getInstance().getStorage().createPlayerReport(target, ReportState.OPEN);
            this.openReports.add(report0);
        }
        return (DefaultPlayerReport) report0;
    }
}
