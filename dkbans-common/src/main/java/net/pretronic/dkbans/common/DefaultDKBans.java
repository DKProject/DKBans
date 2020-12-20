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
import net.pretronic.dkbans.api.joinme.JoinMeManager;
import net.pretronic.dkbans.api.migration.MigrationManager;
import net.pretronic.dkbans.api.player.DKBansPlayerManager;
import net.pretronic.dkbans.api.player.chatlog.ChatLogManager;
import net.pretronic.dkbans.api.player.history.PlayerHistoryManager;
import net.pretronic.dkbans.api.player.ipblacklist.IpAddressBlacklistManager;
import net.pretronic.dkbans.api.player.report.ReportManager;
import net.pretronic.dkbans.api.support.SupportTicketManager;
import net.pretronic.dkbans.common.broadcast.DefaultBroadcastManager;
import net.pretronic.dkbans.common.event.DefaultDKBansChannelBroadcastMessageReceiveEvent;
import net.pretronic.dkbans.common.filter.DefaultFilterManager;
import net.pretronic.dkbans.common.migration.DefaultMigrationManager;
import net.pretronic.dkbans.common.player.chatlog.DefaultChatLogManager;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryManager;
import net.pretronic.dkbans.common.player.ipblacklist.DefaultIpAddressBlacklistManager;
import net.pretronic.dkbans.common.player.report.DefaultReportManager;
import net.pretronic.dkbans.common.storage.DefaultDKBansStorage;
import net.pretronic.dkbans.common.template.DefaultTemplateManager;
import net.pretronic.libraries.event.EventBus;
import net.pretronic.libraries.logging.PretronicLogger;

import java.util.concurrent.ExecutorService;

public class DefaultDKBans extends DKBans {

    private final String version;
    private final PretronicLogger logger;
    private final ExecutorService executorService;
    private final EventBus eventBus;
    private final DefaultDKBansStorage storage;
    private final PlayerHistoryManager historyManager;
    private final ReportManager reportManager;
    private final SupportTicketManager ticketManager;
    private final BroadcastManager broadcastManager;
    private final DefaultFilterManager filterManager;
    private final DefaultTemplateManager templateManager;
    private final ChatLogManager chatLogManager;
    private final DKBansPlayerManager playerManager;
    private final MigrationManager migrationManager;
    private final JoinMeManager joinMeManager;
    private final IpAddressBlacklistManager ipAddressBlacklistManager;

    public DefaultDKBans(String version, PretronicLogger logger, ExecutorService executorService, EventBus eventBus, Database database, DKBansPlayerManager playerManager, JoinMeManager joinMeManager) {
        this.version =version;
        this.logger = logger;
        this.executorService = executorService;
        this.eventBus = eventBus;
        this.storage = new DefaultDKBansStorage(this, database);
        this.joinMeManager = joinMeManager;
        this.historyManager = new DefaultPlayerHistoryManager(this);
        this.reportManager = new DefaultReportManager();
        this.ticketManager = null;
        this.broadcastManager = new DefaultBroadcastManager();
        this.filterManager = new DefaultFilterManager();
        this.templateManager = new DefaultTemplateManager();
        this.chatLogManager = new DefaultChatLogManager();
        this.playerManager = playerManager;
        this.migrationManager = new DefaultMigrationManager();
        this.ipAddressBlacklistManager = new DefaultIpAddressBlacklistManager();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public PretronicLogger getLogger() {
        return this.logger;
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public DefaultDKBansStorage getStorage() {
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
    public ChatLogManager getChatLogManager() {
        return this.chatLogManager;
    }

    @Override
    public DKBansPlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public MigrationManager getMigrationManager() {
        return this.migrationManager;
    }

    @Override
    public JoinMeManager getJoinMeManager() {
        return this.joinMeManager;
    }

    @Override
    public IpAddressBlacklistManager getIpAddressBlacklistManager() {
        return this.ipAddressBlacklistManager;
    }

    @Override
    public void broadcastMessage(String channel, DKBansExecutor executor, String message) {
        getEventBus().callEvent(DKBansChannelBroadcastMessageReceiveEvent.class
                ,new DefaultDKBansChannelBroadcastMessageReceiveEvent(channel, message, executor));
    }

    public static DefaultDKBans getInstance() {
        return (DefaultDKBans) DKBans.getInstance();
    }
}
