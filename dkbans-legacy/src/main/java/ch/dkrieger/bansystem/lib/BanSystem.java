/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 18:28
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

package ch.dkrieger.bansystem.lib;

import ch.dkrieger.bansystem.lib.broadcast.BroadcastManager;
import ch.dkrieger.bansystem.lib.config.Config;
import ch.dkrieger.bansystem.lib.config.MessageConfig;
import ch.dkrieger.bansystem.lib.filter.FilterManager;
import ch.dkrieger.bansystem.lib.player.PlayerManager;
import ch.dkrieger.bansystem.lib.player.history.HistoryManager;
import ch.dkrieger.bansystem.lib.player.history.entry.Ban;
import ch.dkrieger.bansystem.lib.player.history.entry.HistoryEntry;
import ch.dkrieger.bansystem.lib.reason.ReasonProvider;
import ch.dkrieger.bansystem.lib.report.ReportManager;
import ch.dkrieger.bansystem.lib.stats.NetworkStats;
import ch.dkrieger.bansystem.lib.storage.DKBansStorage;
import ch.dkrieger.bansystem.lib.storage.StorageType;
import ch.dkrieger.bansystem.lib.storage.json.JsonDKBansStorage;
import ch.dkrieger.bansystem.lib.storage.mongodb.MongoDBDKBansStorage;
import ch.dkrieger.bansystem.lib.storage.sql.SQLDKBansStorage;

public class BanSystem {

    private static BanSystem instance;
    private final DKBansPlatform platform;
    private PlayerManager playerManager;
    private ReportManager reportManager;
    private BroadcastManager broadcastManager;
    private FilterManager filterManager;
    private HistoryManager historyManager;

    private ReasonProvider reasonProvider;
    private SettingProvider settingProvider;

    private DKBansStorage storage;

    private Config config;
    private MessageConfig messageConfig;

    private NetworkStats cachedNetworkStats, tempSyncStats;

    public BanSystem() {
        if(instance != null) throw new IllegalArgumentException("DKBans (Legacy-Migration) is already initialised");
        instance = this;
        this.platform = new DKBansPlatform();
        this.playerManager = new PlayerManager();
        new Messages();

        System.out.println(Messages.SYSTEM_PREFIX+"starting..");

        systemBootstrap();

        System.out.println(Messages.SYSTEM_PREFIX+"successfully started");
    }
    private void systemBootstrap(){
        this.config = new Config(this.platform);
        this.config.loadConfig();

        this.messageConfig = new MessageConfig(this.platform);
        this.messageConfig.loadConfig();

        this.reasonProvider = new ReasonProvider(this.platform);
        this.settingProvider = new SettingProvider();
        this.historyManager = new HistoryManager();

        HistoryEntry.buildAdapter();
        Ban.buildAdapter();

        if(this.config.storageType == StorageType.MONGODB) this.storage = new MongoDBDKBansStorage(this.config);
        else if(this.config.storageType == StorageType.SQLITE || this.config.storageType == StorageType.MYSQL)this.storage = new SQLDKBansStorage(config);
        else if(this.config.storageType == StorageType.JSON) this.storage = new JsonDKBansStorage(this.config);

        if(storage != null && storage.connect()){
            System.out.println(Messages.SYSTEM_PREFIX + "Used Storage: " + this.config.storageType.toString());
        }else{
            this.config.storageType = StorageType.SQLITE;
            this.storage = new SQLDKBansStorage(config);
            if(!this.storage.connect()){
                this.storage = new JsonDKBansStorage(config);
                this.config.storageType = StorageType.JSON;
                if(!this.storage.connect()){
                    System.out.println(Messages.SYSTEM_PREFIX +"Could not enable DKBans (Legacy-Migration), no storage is working.");
                    return;
                }
                System.out.println(Messages.SYSTEM_PREFIX + "Used Backup Storage: " + config.storageType.toString());
            }
        }

        this.broadcastManager = new BroadcastManager();
        this.filterManager = new FilterManager();
        this.reportManager = new ReportManager();

        this.tempSyncStats = new NetworkStats();
    }
    public void shutdown(){
        if(this.storage != null) this.storage.disconnect();
    }

    public DKBansPlatform getPlatform() {
        return platform;
    }

    public Config getConfig() {
        return config;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public BroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    public FilterManager getFilterManager() {
        return filterManager;
    }

    public ReasonProvider getReasonProvider() {
        return reasonProvider;
    }

    public SettingProvider getSettingProvider() {
        return settingProvider;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public DKBansStorage getStorage() {
        return storage;
    }

    public NetworkStats getNetworkStats(){
        if(this.cachedNetworkStats == null) this.cachedNetworkStats = getStorage().getNetworkStats();
        return this.cachedNetworkStats;
    }
    public NetworkStats getTempSyncStats() {
        if(this.tempSyncStats == null) this.tempSyncStats = new NetworkStats();
        return tempSyncStats;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public void setStorage(DKBansStorage storage) {
        this.storage = storage;
    }

    public static BanSystem getInstance() {
        return instance;
    }
}
