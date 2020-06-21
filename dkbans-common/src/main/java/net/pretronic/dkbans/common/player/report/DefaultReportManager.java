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

package net.pretronic.dkbans.common.player.report;

import net.pretronic.dkbans.api.DKBans;
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
import java.util.UUID;

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
    public PlayerReportEntry report(DKBansPlayer executor, DKBansPlayer target, ReportTemplate template, String serverName, UUID serverId) {
        Validate.notNull(executor, target, template);
        DefaultPlayerReport report = getReport(target);
        DefaultPlayerReportEntry entry = (DefaultPlayerReportEntry) DKBans.getInstance().getStorage()
                .createPlayerReportEntry(report, executor, template, serverName, serverId);
        report.addEntry(entry);
        return entry;
    }

    @Override
    public PlayerReportEntry report(DKBansPlayer executor, DKBansPlayer target, String reason, String serverName, UUID serverId) {
        Validate.notNull(executor, target);
        DefaultPlayerReport report = getReport(target);
        DefaultPlayerReportEntry entry = (DefaultPlayerReportEntry) DKBans.getInstance().getStorage()
                .createPlayerReportEntry(report, executor, reason, serverName, serverId);
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
