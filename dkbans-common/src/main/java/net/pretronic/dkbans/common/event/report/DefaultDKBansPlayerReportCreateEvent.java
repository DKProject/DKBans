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

import net.pretronic.dkbans.api.event.report.DKBansReportCreateEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;

import java.util.UUID;

public class DefaultDKBansPlayerReportCreateEvent implements DKBansReportCreateEvent {

    private final PlayerReportEntry entry;

    public DefaultDKBansPlayerReportCreateEvent(PlayerReportEntry entry) {
        this.entry = entry;
    }

    @Override
    public PlayerReportEntry getReportEntry() {
        return this.entry;
    }

    @Override
    public int getReportId() {
        return entry.getReportId();
    }

    @Override
    public PlayerReport getReport() {
        return entry.getReport();
    }

    @Override
    public UUID getPlayerId() {
        return entry.getReport().getPlayerId();
    }

    @Override
    public DKBansPlayer getPlayer() {
        return entry.getReport().getPlayer();
    }
}
