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
import net.pretronic.dkbans.api.event.report.DKBansReportStateChangeEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.ReportState;

import java.util.UUID;

public class DefaultDKBansReportStateChangeEvent implements DKBansReportStateChangeEvent {

    private final int reportId;
    private final ReportState oldState;
    private final ReportState newState;

    private transient PlayerReport cachedReport;

    public DefaultDKBansReportStateChangeEvent(int reportId, ReportState oldState, ReportState newState, PlayerReport cachedReport) {
        this.reportId = reportId;
        this.oldState = oldState;
        this.newState = newState;
        this.cachedReport = cachedReport;
    }

    @Override
    public int getReportId() {
        return reportId;
    }

    @Override
    public PlayerReport getReport() {
        if(cachedReport == null) cachedReport = DKBans.getInstance().getReportManager().getReport(reportId);
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
    public ReportState getOldState() {
        return oldState;
    }

    @Override
    public ReportState getNewState() {
        return newState;
    }
}
