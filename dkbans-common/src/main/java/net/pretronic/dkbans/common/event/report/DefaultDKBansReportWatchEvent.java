/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.02.21, 13:20
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

package net.pretronic.dkbans.common.event.report;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.event.report.DKBansReportWatchEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;

import java.util.UUID;

public class DefaultDKBansReportWatchEvent implements DKBansReportWatchEvent {

    private final int reportId;
    private final UUID watcherId;

    private PlayerReport cachedReport;
    private DKBansExecutor cachedWatcher;

    public DefaultDKBansReportWatchEvent(int reportId, UUID watcherId, PlayerReport cachedReport, DKBansExecutor cachedWatcher) {
        this.reportId = reportId;
        this.watcherId = watcherId;
        this.cachedReport = cachedReport;
        this.cachedWatcher = cachedWatcher;
    }

    @Override
    public int getReportId() {
        return reportId;
    }

    @Override
    public PlayerReport getReport() {
        if(this.cachedReport == null) cachedReport = DKBans.getInstance().getReportManager().getReport(reportId);
        return this.cachedReport;
    }

    @Override
    public UUID getPlayerId() {
        return getReport().getPlayerId();
    }

    @Override
    public DKBansPlayer getPlayer() {
        return getReport().getPlayer();
    }

    @Override
    public UUID getWatcherId() {
        return watcherId;
    }

    @Override
    public DKBansExecutor getWatcher() {
        if(cachedWatcher == null) cachedWatcher = DKBans.getInstance().getPlayerManager().getExecutor(watcherId);
        return cachedWatcher;
    }
}
