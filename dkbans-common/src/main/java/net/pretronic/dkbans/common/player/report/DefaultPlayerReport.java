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
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.event.report.DKBansReportWatchEvent;
import net.pretronic.dkbans.api.event.report.DKBansReportStateChangeEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.event.report.DefaultDKBansReportStateChangeEvent;
import net.pretronic.dkbans.common.event.report.DefaultDKBansReportWatchEvent;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultPlayerReport implements PlayerReport {

    private final int id;
    private final UUID playerId;
    private UUID watcherId;
    private ReportState state;

    private transient DKBansPlayer cachedPlayer;
    private transient List<PlayerReportEntry> cachedEntries;
    private transient DKBansPlayer cachedWatcher;

    public DefaultPlayerReport(int id, UUID playerId, UUID watcherId, ReportState state, DKBansPlayer cachedPlayer
            , List<PlayerReportEntry> cachedEntries, DKBansPlayer cachedWatcher) {
        this.id = id;
        this.playerId = playerId;
        this.watcherId = watcherId;
        this.state = state;
        this.cachedPlayer = cachedPlayer;
        this.cachedEntries = cachedEntries;
        this.cachedWatcher = cachedWatcher;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public DKBansPlayer getPlayer() {
        if(cachedPlayer == null) this.cachedPlayer = DKBans.getInstance().getPlayerManager().getPlayer(this.playerId);
        return this.cachedPlayer;
    }

    @Override
    public ReportState getState() {
        return this.state;
    }

    @Override
    public void setState(ReportState state) {
        Validate.notNull(state, "Report state can't be null");
        ReportState oldState = this.state;
        this.state = state;
        DefaultDKBans.getInstance().getStorage().getReports().update()
                .set("State",state)
                .where("Id",id)
                .execute();
        DKBans.getInstance().getEventBus().callEvent(DKBansReportStateChangeEvent.class
                ,new DefaultDKBansReportStateChangeEvent(this.id,oldState,state,this));
    }

    @Override
    public List<PlayerReportEntry> getEntries() {
        if(this.cachedEntries == null){
            this.cachedEntries = new ArrayList<>();
            QueryResult result = DefaultDKBans.getInstance().getStorage().getReportEntries()
                    .find().where("ReportId",id).execute();
            for (QueryResultEntry entry : result) {
                this.cachedEntries.add(new DefaultPlayerReportEntry(entry.getInt("Id")
                        ,id
                        ,entry.getUniqueId("ReporterId")
                        ,entry.getInt("TemplateId")
                        ,entry.getString("Reason")
                        ,entry.getString("SererName")
                        ,entry.getUniqueId("ServerId")
                        ,entry.getLong("Time")
                        ,DocumentFileType.JSON.getReader().read(entry.getString("Properties"))
                        ,this,null));
            }
        }
        return this.cachedEntries;
    }

    @Override
    public PlayerReportEntry getEntry(DKBansExecutor reporter) {
        return Iterators.findOne(getEntries(), entry -> entry.getReporter().equals(reporter));
    }

    @Override
    public boolean isWatched() {
        return this.watcherId != null;
    }

    @Override
    public UUID getWatcherId() {
        return watcherId;
    }

    @Override
    public DKBansPlayer getWatcher() {
        if(cachedWatcher == null) this.cachedWatcher = DKBans.getInstance().getPlayerManager().getPlayer(this.watcherId);
        return this.cachedWatcher;
    }

    @Override
    public void watch(DKBansPlayer player) {
        Validate.notNull(player);

        DefaultDKBans.getInstance().getStorage().getReports().update()
                .set("State",ReportState.PROCESSING)
                .set("WatcherId",player.getUniqueId())
                .where("Id",id)
                .execute();

        this.cachedWatcher = player;
        this.watcherId = player.getUniqueId();
        this.state = ReportState.PROCESSING;

        DKBans.getInstance().getEventBus().callEvent(DKBansReportWatchEvent.class
                , new DefaultDKBansReportWatchEvent(id,watcherId,this,player));
    }

    @Override
    public void accept(DKBansExecutor executor) {
        Validate.notNull(executor);
        this.setState(ReportState.ACCEPTED);
    }

    @Override
    public void decline(DKBansExecutor executor) {
        Validate.notNull(executor);
        this.setState(ReportState.DECLINED);
    }

    @Internal
    public void addEntry(PlayerReportEntry entry) {
        if(this.cachedEntries != null) {
            this.cachedEntries.add(entry);
        }
    }

    @Internal
    public void changeStatus(ReportState state){
        this.state = state;
    }

    @Internal
    public void changeWatcher(UUID watcherId){
        this.watcherId = watcherId;
        this.state = ReportState.PROCESSING;
    }
}
