/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.02.21, 10:53
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

package net.pretronic.dkbans.minecraft.listeners;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.event.DKBansJoinMeCreateEvent;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishEvent;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishUpdateEvent;
import net.pretronic.dkbans.api.event.report.DKBansReportCreateEvent;
import net.pretronic.dkbans.api.event.report.DKBansReportStateChangedEvent;
import net.pretronic.dkbans.api.event.report.DKBansReportWatchEvent;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.player.report.DefaultPlayerReport;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.network.NetworkListener;

public class SyncListener {

    private final DefaultDKBans dkbans;

    public SyncListener(DefaultDKBans dkbans) {
        this.dkbans = dkbans;
    }

    @NetworkListener(priority = EventPriority.LOWEST,onlyRemote = true)
    public void onJoinMeCreate(DKBansJoinMeCreateEvent event) {
        System.out.println("---> JOINME SYNC");
        DKBans.getInstance().getJoinMeManager().registerJoinMe(event.getJoinMe());
    }

    @NetworkListener(priority = EventPriority.LOWEST,onlyRemote = true)
    public void onPlayerPunish(DKBansPlayerPunishEvent event){
        System.out.println("---> PUNISH SYNC");

    }

    @NetworkListener(priority = EventPriority.LOWEST,onlyRemote = true)
    public void onPlayerPunishUpdate(DKBansPlayerPunishUpdateEvent event){

    }

    @NetworkListener(priority = EventPriority.LOWEST,onlyRemote = true)
    public void onReportCreate(DKBansReportCreateEvent event) {
        System.out.println("---> REPORT SYNC CREATE");
        PlayerReport report = dkbans.getReportManager().getLoadedReport(event.getReportId());
        if(report != null) ((DefaultPlayerReport) report).addEntry(event.getReportEntry());
    }

    @NetworkListener(priority = EventPriority.LOWEST,onlyRemote = true)
    public void onReportTake(DKBansReportWatchEvent event) {
        System.out.println("---> REPORT SYNC TAKE");
        PlayerReport report = dkbans.getReportManager().getLoadedReport(event.getReportId());
        if(report != null) ((DefaultPlayerReport) report).changeWatcher(event.getWatcherId());
    }

    @NetworkListener(priority = EventPriority.LOWEST,onlyRemote = true)
    public void onReportStateChane(DKBansReportStateChangedEvent event) {
        System.out.println("---> REPORT SYNC STATE CHANGED");
        PlayerReport report = dkbans.getReportManager().getLoadedReport(event.getReportId());
        if(report != null) ((DefaultPlayerReport) report).changeStatus(event.getNewState());
    }

}
