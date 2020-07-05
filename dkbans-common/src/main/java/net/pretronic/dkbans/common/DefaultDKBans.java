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

package net.pretronic.dkbans.common;

import net.pretronic.databasequery.api.Database;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.broadcast.BroadcastManager;
import net.pretronic.dkbans.api.event.DKBansChannelBroadcastMessageReceiveEvent;
import net.pretronic.dkbans.api.player.DKBansPlayerManager;
import net.pretronic.dkbans.api.player.history.PlayerHistoryManager;
import net.pretronic.dkbans.api.player.report.ReportManager;
import net.pretronic.dkbans.api.storage.DKBansStorage;
import net.pretronic.dkbans.api.support.SupportTicketManager;
import net.pretronic.dkbans.common.event.DefaultDKBansChannelBroadcastMessageReceiveEvent;
import net.pretronic.dkbans.common.filter.DefaultFilterManager;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryManager;
import net.pretronic.dkbans.common.player.report.DefaultReportManager;
import net.pretronic.dkbans.common.storage.DefaultDKBansStorage;
import net.pretronic.dkbans.common.template.DefaultTemplateManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.logging.PretronicLogger;

public class DefaultDKBans extends DKBans {

    private final PretronicLogger logger;
    private final EventBus eventBus;
    private final DKBansStorage storage;
    private final PlayerHistoryManager historyManager;
    private final ReportManager reportManager;
    private final SupportTicketManager ticketManager;
    private final BroadcastManager broadcastManager;
    private final DefaultFilterManager filterManager;
    private final DefaultTemplateManager templateManager;
    private final DKBansPlayerManager playerManager;

    public DefaultDKBans(PretronicLogger logger, EventBus eventBus, Database database,DKBansPlayerManager playerManager) {
        this.logger = logger;
        this.eventBus = eventBus;
        this.storage = new DefaultDKBansStorage(this, database);
        this.historyManager = new DefaultPlayerHistoryManager(this);
        this.reportManager = new DefaultReportManager();
        this.ticketManager = null;
        this.broadcastManager = null;
        this.filterManager = new DefaultFilterManager();
        this.templateManager = new DefaultTemplateManager(this);
        this.playerManager = playerManager;
    }

    @Override
    public PretronicLogger getLogger() {
        return this.logger;
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public DKBansStorage getStorage() {
        return this.storage;
    }

    @Override
    public PlayerHistoryManager getHistoryManager() {
        return this.historyManager;
    }

    @Override
    public ReportManager getReportManager() {
        return this.reportManager;
    }

    @Override
    public SupportTicketManager getTicketManager() {
        return this.ticketManager;
    }

    @Override
    public BroadcastManager getBroadcastManager() {
        return this.broadcastManager;
    }

    @Override
    public DefaultFilterManager getFilterManager() {
        return this.filterManager;
    }

    @Override
    public DefaultTemplateManager getTemplateManager() {
        return this.templateManager;
    }

    @Override
    public DKBansPlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public void broadcastMessage(String channel, DKBansExecutor executor, String message) {
        getEventBus().callEvent(DKBansChannelBroadcastMessageReceiveEvent.class
                ,new DefaultDKBansChannelBroadcastMessageReceiveEvent(channel, message, executor));
    }
}
