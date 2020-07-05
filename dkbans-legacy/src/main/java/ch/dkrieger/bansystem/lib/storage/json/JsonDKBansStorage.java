/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 18:36
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

package ch.dkrieger.bansystem.lib.storage.json;

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.broadcast.Broadcast;
import ch.dkrieger.bansystem.lib.config.Config;
import ch.dkrieger.bansystem.lib.filter.Filter;
import ch.dkrieger.bansystem.lib.player.IPBan;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import ch.dkrieger.bansystem.lib.player.OnlineSession;
import ch.dkrieger.bansystem.lib.player.chatlog.ChatLog;
import ch.dkrieger.bansystem.lib.player.chatlog.ChatLogEntry;
import ch.dkrieger.bansystem.lib.player.history.History;
import ch.dkrieger.bansystem.lib.player.history.entry.Ban;
import ch.dkrieger.bansystem.lib.player.history.entry.HistoryEntry;
import ch.dkrieger.bansystem.lib.report.Report;
import ch.dkrieger.bansystem.lib.stats.NetworkStats;
import ch.dkrieger.bansystem.lib.stats.PlayerStats;
import ch.dkrieger.bansystem.lib.storage.DKBansStorage;
import ch.dkrieger.bansystem.lib.utils.Document;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonDKBansStorage implements DKBansStorage {

    private List<NetworkPlayer> players;
    private List<ChatLogEntry> chatLogEntries;
    private List<IPBan> ipBans;
    private Map<String,Document> settings;
    private File storageFolder;

    private AtomicInteger nextPlayerID, nextHistoryID;

    public JsonDKBansStorage(Config config){
        this.storageFolder = config.storageFolder;
        this.storageFolder.mkdirs();
    }
    @Override
    public boolean connect() {
        Document document = Document.loadData(new File(storageFolder,"players.json"));
        if(document.contains("players")){
            players = document.getObject("players",new TypeToken<List<NetworkPlayer>>(){}.getType());
            nextPlayerID = new AtomicInteger(document.getInt("nextPlayerID"));
            nextHistoryID  = new AtomicInteger(document.getInt("nextHistoryID"));
        }else{
            this.players = new ArrayList<>();
            this.nextPlayerID = new AtomicInteger(1);
            this.nextHistoryID = new AtomicInteger(1);
        }
        Document chatLogs = Document.loadData(new File(storageFolder,"chatlogs.json"));
        if(chatLogs.contains("entries")){
            this.chatLogEntries = chatLogs.getObject("entries",new TypeToken<List<ChatLogEntry>>(){}.getType());
        }else this.chatLogEntries = new ArrayList<>();

        Document ipBans = Document.loadData(new File(storageFolder,"ipbans.json"));
        if(ipBans.contains("bans")) this.ipBans = ipBans.getObject("bans",new TypeToken<List<IPBan>>(){}.getType());
        else this.ipBans = new ArrayList<>();

        Document settings = Document.loadData(new File(storageFolder,"settings.json"));
        if(settings.contains("settings")) this.settings = settings.getObject("settings",new TypeToken<LinkedHashMap<String,Document>>(){}.getType());
        else this.settings = new LinkedHashMap<>();
        return true;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void disconnect() {
        save();
    }
    private void save(){
        new Document().append("nextPlayerID",nextPlayerID.get()).append("nextHistoryID",nextHistoryID.get()).append("players",players)
                .saveData(new File(storageFolder,"players.json"));
        new Document().append("entries",chatLogEntries).saveData(new File(storageFolder,"chatlogs.json"));

        new Document().append("bans",ipBans).saveData(new File(storageFolder,"ipbans.json"));

        new Document().append("settings",settings).saveData(new File(storageFolder,"settings.json"));
    }

    @Override
    public NetworkPlayer getPlayer(int id) throws Exception {
        return GeneralUtil.iterateOne(this.players, object -> object.getID() == id);
    }

    @Override
    public NetworkPlayer getPlayer(String name) throws Exception {
        return GeneralUtil.iterateOne(this.players, object -> object.getName().equalsIgnoreCase(name));
    }

    @Override
    public NetworkPlayer getPlayer(UUID uuid) throws Exception {
        return GeneralUtil.iterateOne(this.players, object -> object.getUUID().equals(uuid));
    }

    @Override
    public List<NetworkPlayer> getPlayersByIp(String ip) {
        return GeneralUtil.iterateAcceptedReturn(this.players, object -> object.getIP().equalsIgnoreCase(ip));
    }

    @Override
    public int getRegisteredPlayerCount() {
        return this.players.size();
    }

    @Override
    public ChatLog getChatLog(UUID player) {
        return new ChatLog(GeneralUtil.iterateAcceptedReturn(this.chatLogEntries, object -> object.getUUID().equals(player)));
    }

    @Override
    public ChatLog getChatLog(String server) {
        return new ChatLog(GeneralUtil.iterateAcceptedReturn(this.chatLogEntries, object -> object.getServer().equalsIgnoreCase(server)));
    }

    @Override
    public ChatLog getChatLog(UUID player, String server) {
        return new ChatLog(GeneralUtil.iterateAcceptedReturn(this.chatLogEntries, object -> object.getServer().equalsIgnoreCase(server)
                && object.getUUID().equals(player)));
    }

    @Override
    public History getHistory(UUID uuid) {
        try{
            NetworkPlayer player = getPlayer(uuid);
            return player.getHistory();
        }catch (Exception e){}
        return null;
    }

    @Override
    public List<Report> getReports() {
        List<Report> reports = new ArrayList<>();
        GeneralUtil.iterateForEach(this.players, object -> reports.addAll(object.getReports()));
        return reports;
    }

    @Override
    public HistoryEntry getHistoryEntry(int id) {
        final HistoryEntry[] entryReturn = new HistoryEntry[1];
        GeneralUtil.iterateOne(this.players, player -> {
            HistoryEntry entry = GeneralUtil.iterateOne(player.getHistory().getEntries(), object -> object.getID() == id);
            entryReturn[0] = entry;
            return entry != null;
        });
        return entryReturn[0];
    }

    @Override
    public List<Ban> getBans() {
        List<Ban> bans = new ArrayList<>();
        GeneralUtil.iterateForEach(this.players, player -> GeneralUtil.iterateAcceptedForEach(player.getHistory().getEntries()
                , object -> object instanceof Ban, object -> bans.add((Ban)object)));
        return bans;
    }
    @Override
    public List<Ban> getNotTimeOutedBans() {
        long yet = System.currentTimeMillis();
        List<Ban> bans = new ArrayList<>();
        GeneralUtil.iterateForEach(this.players, player -> GeneralUtil.iterateAcceptedForEach(player.getHistory().getEntries()
                , object -> object instanceof Ban && object.getTimeStamp() > yet, object -> bans.add((Ban)object)));
        return bans;
    }

    @Override
    public IPBan getIpBan(String ip) {
        return GeneralUtil.iterateOne(this.ipBans, object -> object.getIp().equals(ip));
    }

    @Override
    public List<Filter> loadFilters() {
        Document document = Document.loadData(new File(storageFolder,"filters.json"));
        if(document.contains("filters")) return document.getObject("filters",new TypeToken<List<Filter>>(){}.getType());
        return new ArrayList<>();
    }

    @Override
    public List<Broadcast> loadBroadcasts() {
        Document document = Document.loadData(new File(storageFolder,"broadcasts.json"));
        if(document.contains("broadcasts")) return document.getObject("broadcasts",new TypeToken<List<Broadcast>>(){}.getType());
        return new ArrayList<>();
    }

    @Override
    public NetworkStats getNetworkStats() {
        NetworkStats stats = Document.loadData(new File(storageFolder,"networkstats.json")).getObject("networkStats",NetworkStats.class);
        if(stats != null) return stats;
        return new NetworkStats();
    }

    @Override
    public Document getSetting(String name) {
        return settings.get(name);
    }
}