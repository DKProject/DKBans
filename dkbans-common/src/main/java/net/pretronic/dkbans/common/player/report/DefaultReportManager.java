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

import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.event.report.DKBansPlayerReportCreateEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.report.ReportManager;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.event.DefaultDKBansPlayerReportCreateEvent;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultReportManager implements ReportManager {

    private List<PlayerReport> openReports;

    public DefaultReportManager() {
        this.openReports = new ArrayList<>();
    }

    @Override
    public int getReportCount() {
        return getOpenReports().size();
    }

    @Override
    public List<PlayerReport> getOpenReports() {
        if(this.openReports == null){
            QueryResult result = DefaultDKBans.getInstance().getStorage().getReports()
                    .find().where("State",ReportState.OPEN).execute();
            this.openReports = new ArrayList<>();
            for (QueryResultEntry entry : result) {
                this.openReports.add(new DefaultPlayerReport(entry.getInt("Id")
                        ,ReportState.valueOf(entry.getString("State"))
                        ,entry.getUniqueId("PlayerId"),entry.getUniqueId("WatcherId")));
            }
        }
        return this.openReports;
    }

    @Override
    public PlayerReport getReport(DKBansPlayer player) {
        return Iterators.findOne(this.openReports, report -> report.getPlayer().equals(player));
    }

    @Override
    public PlayerReport getReport(UUID uniqueId) {
        return Iterators.findOne(this.openReports, report -> report.getPlayerId().equals(uniqueId));
    }

    @Override
    public PlayerReport getReportByWatcher(UUID uniqueId) {
        return Iterators.findOne(this.openReports, report -> report.getWatcherId().equals(uniqueId));
    }

    @Override
    public PlayerReportEntry report(DKBansPlayer executor, DKBansPlayer target, ReportTemplate template, String serverName, UUID serverId) {
        Validate.notNull(executor, target, template);
        return report(executor, target, template.getDisplayName(),template, serverName, serverId);
    }

    @Override
    public PlayerReportEntry report(DKBansPlayer executor, DKBansPlayer target, String reason, String serverName, UUID serverId) {
        Validate.notNull(executor, target);
        return report(executor, target, reason,null, serverName, serverId);
    }

    private PlayerReportEntry report(DKBansPlayer executor, DKBansPlayer target,String reason, ReportTemplate template, String serverName, UUID serverId) {
        DefaultPlayerReport report = getReportOrCreate(target);
        if(report.getEntry(executor) != null) return null;
        DefaultPlayerReportEntry entry = (DefaultPlayerReportEntry) DKBans.getInstance().getStorage()
                .createPlayerReportEntry(report, executor,reason, template, serverName, serverId);
        report.addEntry(entry);

        DefaultDKBansPlayerReportCreateEvent event = new DefaultDKBansPlayerReportCreateEvent(entry);
        DKBans.getInstance().getEventBus().callEvent(DKBansPlayerReportCreateEvent.class, event);
        return entry;
    }

    private DefaultPlayerReport getReportOrCreate(DKBansPlayer target) {
        PlayerReport report0 = Iterators.findOne(this.openReports, target0 -> target0.getPlayer().equals(target));
        if(report0 == null) {
            report0 = DKBans.getInstance().getStorage().createPlayerReport(target, ReportState.OPEN);
            this.openReports.add(report0);
        }
        return (DefaultPlayerReport) report0;
    }

    @Internal
    public void removeReport(PlayerReport report){
        this.openReports.remove(report);
    }
}
