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

package net.pretronic.dkbans.api;

import net.pretronic.dkbans.api.broadcast.BroadcastManager;
import net.pretronic.dkbans.api.filter.FilterManager;
import net.pretronic.dkbans.api.joinme.JoinMeManager;
import net.pretronic.dkbans.api.migration.MigrationManager;
import net.pretronic.dkbans.api.player.DKBansPlayerManager;
import net.pretronic.dkbans.api.player.chatlog.ChatLogManager;
import net.pretronic.dkbans.api.player.history.PlayerHistoryManager;
import net.pretronic.dkbans.api.player.report.ReportManager;
import net.pretronic.dkbans.api.storage.DKBansStorage;
import net.pretronic.dkbans.api.support.SupportTicketManager;
import net.pretronic.dkbans.api.template.TemplateManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.logging.PretronicLogger;

import java.util.concurrent.ExecutorService;

/*
- PUNISH
- UNPUNISH
- REPORT
 */
public abstract class DKBans {

    private static DKBans INSTANCE;

    public abstract String getVersion();

    public abstract PretronicLogger getLogger();

    public abstract ExecutorService getExecutorService();

    public abstract EventBus getEventBus();

    public abstract DKBansStorage getStorage();


    public abstract PlayerHistoryManager getHistoryManager();

    public abstract ReportManager getReportManager();

    public abstract SupportTicketManager getTicketManager();


    public abstract BroadcastManager getBroadcastManager();

    public abstract FilterManager getFilterManager();

    public abstract TemplateManager getTemplateManager();

    public abstract ChatLogManager getChatLogManager();

    public abstract DKBansPlayerManager getPlayerManager();

    public abstract MigrationManager getMigrationManager();

    public abstract JoinMeManager getJoinMeManager();

    public abstract void broadcastMessage(String channel,DKBansExecutor executor,String message);



    public static DKBans getInstance() {
        if(INSTANCE == null) throw new IllegalArgumentException("Instance not set");
        return INSTANCE;
    }

    public static void setInstance(DKBans instance) {
        if(INSTANCE != null) throw new IllegalArgumentException("Instance is already set");
        INSTANCE = instance;
    }
}
