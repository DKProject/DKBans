/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 18:35
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

package ch.dkrieger.bansystem.lib.storage.mongodb;

import ch.dkrieger.bansystem.lib.broadcast.Broadcast;
import ch.dkrieger.bansystem.lib.config.Config;
import ch.dkrieger.bansystem.lib.filter.Filter;
import ch.dkrieger.bansystem.lib.player.IPBan;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import ch.dkrieger.bansystem.lib.player.chatlog.ChatLog;
import ch.dkrieger.bansystem.lib.player.chatlog.ChatLogEntry;
import ch.dkrieger.bansystem.lib.player.history.History;
import ch.dkrieger.bansystem.lib.player.history.entry.Ban;
import ch.dkrieger.bansystem.lib.player.history.entry.HistoryEntry;
import ch.dkrieger.bansystem.lib.report.Report;
import ch.dkrieger.bansystem.lib.stats.NetworkStats;
import ch.dkrieger.bansystem.lib.storage.DKBansStorage;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Filters.*;

public class MongoDBDKBansStorage implements DKBansStorage {

    private Config config;
    private MongoClient mongoClient;
    private MongoDatabase database;

    private MongoCollection<Document> playerCollection, chatlogCollection, historyCollection, reportCollection
            , filterCollection, broadcastCollection, networkStatsCollection , ipBanCollection, settingsCollection;

    public MongoDBDKBansStorage(Config config) {
        this.config = config;
    }
    @Override
    public boolean connect() {//Indexes.descending("name")
        String uri = "mongodb"+(config.mongoDbSrv?"+srv":"")+"://";
        if(config.mongoDbAuthentication) uri += config.storageUser+":"+config.storagePassword+"@";
        uri += config.storageHost+"/";
        if(config.mongoDbAuthentication) uri += config.mongoDbAuthDB;
        uri += "?retryWrites=true&connectTimeoutMS=500&socketTimeoutMS=500";
        if(config.storageSSL) uri+= "&ssl=true";

        this.mongoClient = new MongoClient(new MongoClientURI(uri));
        this.database = this.mongoClient.getDatabase(config.storageDatabase);
        this.playerCollection = database.getCollection("DKBans_players",Document.class);
        this.chatlogCollection = database.getCollection("DKBans_chatlogs",Document.class);
        this.broadcastCollection = database.getCollection("DKBans_broadcasts",Document.class);
        this.historyCollection = database.getCollection("DKBans_historys",Document.class);
        this.reportCollection = database.getCollection("DKBans_reports",Document.class);
        this.filterCollection = database.getCollection("DKBans_filters",Document.class);
        this.networkStatsCollection = database.getCollection("DKBans_networkstats",Document.class);
        this.ipBanCollection = database.getCollection("DKBans_ipbans",Document.class);
        this.settingsCollection = database.getCollection("DKBans_settings",Document.class);

        try{this.playerCollection.createIndex(Indexes.descending("id"),new IndexOptions().unique(true)); }catch (Exception exception){}
        try{this.historyCollection.createIndex(Indexes.descending("id"),new IndexOptions().unique(true)); }catch (Exception exception){}
        try{this.filterCollection.createIndex(Indexes.descending("id"),new IndexOptions().unique(true)); }catch (Exception exception){}
        try{this.broadcastCollection.createIndex(Indexes.descending("id"),new IndexOptions().unique(true)); }catch (Exception exception){}

        if(this.playerCollection.countDocuments() == 0)
            this.playerCollection.insertOne(new Document("name","DKBansUniquePlayerIDCounter").append("counterNext",1));

        if(this.historyCollection.countDocuments() == 0)
            this.historyCollection.insertOne(new Document("name","DKBansUniqueHistoryIDCounter").append("counterNext",1));

        if(this.filterCollection.countDocuments() == 0)
            this.filterCollection.insertOne(new Document("name","DKBansUniqueFilterIDCounter").append("counterNext",1));

        if(this.broadcastCollection.countDocuments() == 0)
            this.broadcastCollection.insertOne(new Document("name","DKBansUniqueBroadcastIDCounter").append("counterNext",1));

        if(this.networkStatsCollection.countDocuments() == 0){
            MongoDBUtil.insertOne(networkStatsCollection,new NetworkStats());
        }
        return true;
    }
    @Override
    public void disconnect() {

    }

    @Override
    public Collection<NetworkPlayer> getPlayers() {
        Collection<NetworkPlayer> players = new ArrayList<>();
        for (Document document : playerCollection.find()) {
            players.add(getPlayer(document));
        }
        return players;
    }

    @Override
    public boolean isConnected() {
        try{
            playerCollection.countDocuments();
            return true;
        }catch (Exception exception){}
        return false;
    }

    @Override
    public NetworkPlayer getPlayer(int id) throws Exception {
        return getPlayer(eq("id",id));
    }

    @Override
    public NetworkPlayer getPlayer(String name) throws Exception {
        return getPlayer(new Document().append("name",new Document("$regex",name).append("$options","i")));
    }

    @Override
    public NetworkPlayer getPlayer(UUID uuid) throws Exception {
        return getPlayer(eq("uuid",uuid.toString()));
    }
    public NetworkPlayer getPlayer(Bson bson){
        NetworkPlayer player = MongoDBUtil.findFirst(this.playerCollection,bson,NetworkPlayer.class);
        if(player != null){
            player.setHistory(getHistory(player.getUUID()));
            player.setReports(MongoDBUtil.find(this.reportCollection,eq("uuid",player.getUUID().toString()),Report.class));
        }
        return player;
    }

    @Override
    public List<NetworkPlayer> getPlayersByIp(String ip) {
        List<NetworkPlayer> players = MongoDBUtil.find(this.playerCollection,eq("lastIP",ip),NetworkPlayer.class);
        GeneralUtil.iterateForEach(players, player -> {
            player.setHistory(getHistory(player.getUUID()));
            player.setReports(MongoDBUtil.find(reportCollection,eq("uuid",player.getUUID().toString()),Report.class));
        });
        return players;
    }

    @Override
    public int getRegisteredPlayerCount() {
        return Integer.parseInt(String.valueOf(playerCollection.countDocuments()-1));
    }

    @Override
    public HistoryEntry getHistoryEntry(int id) {
        return MongoDBUtil.findFirst(this.historyCollection,eq("id",id),HistoryEntry.class);
    }

    @Override
    public ChatLog getChatLog(UUID uuid) {
        List<ChatLogEntry> entries = MongoDBUtil.find(this.chatlogCollection,eq("uuid",uuid.toString()),ChatLogEntry.class);
        return new ChatLog(entries);
    }
    @Override
    public ChatLog getChatLog(String server) {
        List<ChatLogEntry> entries = MongoDBUtil.find(this.chatlogCollection,eq("server",server),ChatLogEntry.class);
        return new ChatLog(entries);
    }

    @Override
    public ChatLog getChatLog(UUID player, String server) {
        List<ChatLogEntry> entries = MongoDBUtil.find(this.chatlogCollection,and(eq("server",server),eq("uuid",player.toString())),ChatLogEntry.class);
        return new ChatLog(entries);
    }

    @Override
    public History getHistory(UUID uuid) {
        List<HistoryEntry> entries = MongoDBUtil.find(this.historyCollection,eq("uuid",uuid.toString()),HistoryEntry.class);
        Map<Integer,HistoryEntry> mapEntries = new HashMap<>();
        GeneralUtil.iterateForEach(entries, object -> mapEntries.put(object.getID(),object));
        return new History(mapEntries);
    }


    @Override
    public List<Report> getReports() {
        return MongoDBUtil.findALL(this.reportCollection,Report.class);
    }

    @Override
    public List<Ban> getBans() {
        return MongoDBUtil.find(this.historyCollection,eq("historyAdapterType","BAN"),Ban.class);
    }

    @Override
    public List<Ban> getNotTimeOutedBans() {
        return MongoDBUtil.find(this.historyCollection,and(eq("historyAdapterType","BAN")
                ,gt("timeOut",System.currentTimeMillis())),Ban.class);
    }

    @Override
    public List<Filter> loadFilters() {
        return MongoDBUtil.find(this.filterCollection,not(eq("name","DKBansUniqueFilterIDCounter")),Filter.class);
    }

    @Override
    public List<Broadcast> loadBroadcasts() {
        return MongoDBUtil.find(this.broadcastCollection,not(eq("name","DKBansUniqueBroadcastIDCounter")),Broadcast.class);
    }

    @Override
    public NetworkStats getNetworkStats() {
        List<NetworkStats> stats = MongoDBUtil.findALL(this.networkStatsCollection,NetworkStats.class);
        if(stats.size() > 0) return stats.get(0);
        return new NetworkStats();
    }

    @Override
    public IPBan getIpBan(String ip) {
        return MongoDBUtil.findFirst(this.ipBanCollection,eq("ip",ip),IPBan.class);
    }

    @Override
    public Collection<IPBan> getIpBans() {
        return MongoDBUtil.findALL(this.ipBanCollection, IPBan.class);
    }

    @Override
    public ch.dkrieger.bansystem.lib.utils.Document getSetting(String name) {
        Document result =  this.settingsCollection.find(eq("settingTypeName",name)).first();
        if(result != null){
            return ch.dkrieger.bansystem.lib.utils.Document.loadData(result.toJson(MongoDBUtil.MONGOJSONSETTINGS));
        }
        return null;
    }
}
