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
import net.pretronic.databasequery.api.query.type.SearchQuery;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.event.report.DKBansReportCreateEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.report.ReportManager;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.event.report.DefaultDKBansPlayerReportCreateEvent;
import net.pretronic.dkbans.common.player.DefaultDKBansPlayer;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.caching.ShadowArrayCache;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class DefaultReportManager implements ReportManager {

    private final Cache<PlayerReport> reports;
    public long loadedAllTimeout;

    public DefaultReportManager() {
        this.reports = new ShadowArrayCache<>();
        this.reports.setMaxSize(1024);
        this.reports.setExpireAfterAccess(10, TimeUnit.MINUTES);
        this.reports.registerQuery("get", new ReportGetter());
    }

    @Override
    public int getReportCount() {
        return getNewReports().size();
    }

    @Override
    public List<PlayerReport> getNewReports() {
        if(loadedAllTimeout < System.currentTimeMillis()){
            QueryResult result = DefaultDKBans.getInstance().getStorage().getReports().find()
                    .where("State",ReportState.NEW).execute();
            List<PlayerReport> reports = new ArrayList<>();
            for (QueryResultEntry entry : result) {
                int id = entry.getInt("Id");
                PlayerReport report = this.reports.get(report1 -> report1.getId() == id);
                if(report == null){
                    report = new DefaultPlayerReport(entry.getInt("Id")
                            ,entry.getUniqueId("PlayerId")
                            ,entry.getUniqueId("WatcherId")
                            ,ReportState.valueOf(entry.getString("State"))
                            ,null,null,null);
                    this.reports.insert(report);
                }
                reports.add(report);
                loadedAllTimeout = System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5);
            }
            return reports;
        }
        return Iterators.filter(this.reports.getCachedObjects(), report -> report.getState() == ReportState.NEW);
    }

    @Override
    public PlayerReport getReport(int reportId) {
        return this.reports.get("get", reportId);
    }

    @Override
    public PlayerReport getReport(UUID uniqueId) {
        return this.reports.get("get", uniqueId);
    }

    @Override
    public PlayerReport getOpenReport(UUID uniqueId) {
        PlayerReport report = this.reports.get(report1 -> report1.getWatcherId().equals(uniqueId) && report1.getState().isOpen());
        if(report == null){
            report = loadReport(DefaultDKBans.getInstance().getStorage().getReports().find()
                    .where("PlayerId",uniqueId)
                    .or(query -> {
                        query.where("State",ReportState.NEW);
                        query.where("State",ReportState.PROCESSING);
                        query.where("State",ReportState.WAITING);
                    }).execute());
            if(report != null){
                reports.insert(report);
            }
        }
        return report;
    }

    @Override
    public PlayerReport getReportByWatcher(UUID uniqueId) {
        PlayerReport report = this.reports.get(report1 -> report1.getWatcherId().equals(uniqueId));
        if(report == null){
            report = loadReport(DefaultDKBans.getInstance().getStorage().getReports().find()
                    .where("WatcherId",uniqueId).execute());
            reports.insert(report);
        }
        return report;
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
        DKBans.getInstance().getEventBus().callEvent(DKBansReportCreateEvent.class, event);
        return entry;
    }

    private DefaultPlayerReport getReportOrCreate(DKBansPlayer target) {
        PlayerReport report = getOpenReport(target.getUniqueId());
        if(report == null) {
            report = DKBans.getInstance().getStorage().createPlayerReport(target, ReportState.NEW);
            this.reports.insert(report);
        }
        return (DefaultPlayerReport) report;
    }

    @Internal
    public PlayerReport getLoadedReport(int reportId){
        return this.reports.get(report -> report.getId() == reportId);
    }

    private PlayerReport loadReport(QueryResult result){
        if(!result.isEmpty()) return loadReport(result.first());
        return null;
    }

    private PlayerReport loadReport(QueryResultEntry entry){
        return new DefaultPlayerReport(entry.getInt("Id")
                ,entry.getObject("PlayerId",UUID.class)
                ,entry.getObject("WatcherId",UUID.class)
                ,ReportState.valueOf(entry.getString("State"))
                ,null,null,null);
    }

    private class ReportGetter implements CacheQuery<PlayerReport> {

        @Override
        public boolean check(PlayerReport report, Object[] objects) {
            Object identifier = objects[0];
            if(identifier instanceof UUID) return report.getPlayerId().equals(identifier);
            return report.getId() == (int)identifier;
        }

        @Override
        public void validate(Object[] identifiers) {
            Validate.isTrue(identifiers.length == 1 && (identifiers[0] instanceof UUID || identifiers[0] instanceof String));
        }

        @Override
        public PlayerReport load(Object[] identifiers) {
            Object identifier = identifiers[0];
            if(identifier instanceof UUID){
                return loadReport(DefaultDKBans.getInstance().getStorage().getReports().find()
                        .where("PlayerId",identifier).execute());
            } else {
                return loadReport(DefaultDKBans.getInstance().getStorage().getReports().find()
                        .where("Id",identifier).execute());
            }
        }
    }
}
