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
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.event.DKBansPlayerReportTakeEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.event.DefaultDKBansPlayerReportTakeEvent;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultPlayerReport implements PlayerReport {

    private final int id;
    private final UUID playerId;
    private final UUID watcherId;
    private DKBansPlayer player;
    private ReportState state;
    private List<PlayerReportEntry> entries;
    private DKBansPlayer watcher;

    public DefaultPlayerReport(int id, ReportState state, UUID playerId, UUID watcherId) {
        this.id = id;
        this.state = state;
        this.playerId = playerId;
        this.watcherId = watcherId;
    }

    public DefaultPlayerReport(int id, ReportState state, DKBansPlayer player, DKBansPlayer watcher) {
        this(id,state,player.getUniqueId(),watcher != null ? watcher.getUniqueId() : null);
        this.player = player;
        this.watcher = watcher;
    }

    public DefaultPlayerReport(int id, ReportState state, DKBansPlayer player) {
        this(id,state,player,null);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public DKBansPlayer getPlayer() {
        if(player == null){
            this.player = DKBans.getInstance().getPlayerManager().getPlayer(this.playerId);
        }
        return this.player;
    }

    @Override
    public ReportState getState() {
        return this.state;
    }

    @Override
    public void setState(ReportState state) {
        Validate.notNull(state, "Report state can't be null");
        this.state = state;
    }

    @Override
    public List<PlayerReportEntry> getEntries() {
        if(this.entries == null){
            this.entries = new ArrayList<>();
        }
        return this.entries;
    }

    @Override
    public PlayerReportEntry getEntry(DKBansExecutor reporter) {
        return Iterators.findOne(this.entries, entry -> entry.getReporter().equals(reporter));
    }

    @Override
    public boolean isWatched() {
        return this.watcherId != null;
    }

    @Override
    public DKBansPlayer getWatcher() {
        if(watcher == null){
            this.watcher = DKBans.getInstance().getPlayerManager().getPlayer(this.watcherId);
        }
        return this.watcher;
    }

    @Override
    public void setWatcher(DKBansPlayer player) {
        Validate.notNull(player);

        DefaultDKBans.getInstance().getStorage().getReports().update()
                .set("WatcherId",player.getUniqueId()).where("Id",id)
                .execute();

        this.watcher = player;

        DKBansPlayerReportTakeEvent event = new DefaultDKBansPlayerReportTakeEvent(player, this);
        DKBans.getInstance().getEventBus().callEvent(DKBansPlayerReportTakeEvent.class, event);
    }

    @Override
    public PlayerHistoryEntry accept(DKBansExecutor executor, String reason) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerHistoryEntry accept(DKBansExecutor executor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decline(DKBansExecutor executor) {
        throw new UnsupportedOperationException();
    }

    @Internal
    public void addEntry(DefaultPlayerReportEntry entry) {
        this.entries.add(entry);
    }
}
