/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 11.07.20, 11:26
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

package net.pretronic.dkbans.common.event;

import net.pretronic.dkbans.api.event.report.DKBansPlayerReportCreateEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;

import java.util.UUID;

public class DefaultDKBansPlayerReportCreateEvent implements DKBansPlayerReportCreateEvent {

    private final PlayerReportEntry reportEntry;

    public DefaultDKBansPlayerReportCreateEvent(PlayerReportEntry reportEntry) {
        this.reportEntry = reportEntry;
    }

    @Override
    public PlayerReportEntry getReportEntry() {
        return this.reportEntry;
    }

    @Override
    public PlayerReport getReport() {
        return reportEntry.getReport();
    }

    @Override
    public UUID getPlayerId() {
        return null;
    }

    @Override
    public DKBansPlayer getPlayer() {
        return null;
    }
}
