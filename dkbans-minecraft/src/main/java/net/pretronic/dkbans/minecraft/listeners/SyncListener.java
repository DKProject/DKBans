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
import net.pretronic.dkbans.api.event.report.DKBansPlayerReportCreateEvent;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.network.NetworkListener;

public class SyncListener {

    @NetworkListener(priority = EventPriority.LOW,onlyRemote = true)
    public void onJoinMeCreate(DKBansJoinMeCreateEvent event) {
        DKBans.getInstance().getJoinMeManager().registerJoinMe(event.getJoinMe());
    }

    @NetworkListener(priority = EventPriority.LOW,onlyRemote = true)
    public void onPlayerReportCreate(DKBansPlayerReportCreateEvent event) {

    }

}
