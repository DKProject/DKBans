/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 18:34
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

package ch.dkrieger.bansystem.lib.storage.sql;

import ch.dkrieger.bansystem.lib.broadcast.Broadcast;
import ch.dkrieger.bansystem.lib.config.Config;
import ch.dkrieger.bansystem.lib.filter.Filter;
import ch.dkrieger.bansystem.lib.filter.FilterOperation;
import ch.dkrieger.bansystem.lib.filter.FilterType;
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
import ch.dkrieger.bansystem.lib.storage.StorageType;
import ch.dkrieger.bansystem.lib.storage.sql.query.ColumnType;
import ch.dkrieger.bansystem.lib.storage.sql.query.QueryOption;
import ch.dkrieger.bansystem.lib.storage.sql.query.SelectQuery;
import ch.dkrieger.bansystem.lib.storage.sql.table.Table;
import ch.dkrieger.bansystem.lib.utils.Document;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SQLDKBansStorage implements DKBansStorage {

    private Config config;
    private SQL sql;
    private Table players, chatlogs, histories, reports, filters, broadcasts,onlineSessions, networkStats, ipbans, settings;

    public SQLDKBansStorage(Config config) {
        this.config = config;
    }
    @Override
    public boolean connect() {
        if(config.storageType==StorageType.MYSQL) sql = new MySQL(config.storageHost,config.storagePort,config.storageDatabase,config.storageUser,config.storagePassword,config.storageSSL);
        else sql = new SQLite(config.storageFolder,"dkabns.db");
        boolean connect = sql.connect();

        if(connect){

            players = new Table(sql,"DKBans_players");
            chatlogs = new Table(sql,"DKBans_chatlogs");
            histories = new Table(sql,"DKBans_histories");
            reports = new Table(sql,"DKBans_reports");
            filters = new Table(sql,"DKBans_filters");
            broadcasts = new Table(sql,"DKBans_broadcasts");
            networkStats = new Table(sql,"DKBans_networkStats");
            onlineSessions = new Table(sql,"DKBans_onlinesessions");
            ipbans = new Table(sql,"DKBans_ipbans");
            settings = new Table(sql,"DKBans_settings");

            try{
                players.create().create("id",ColumnType.INT,QueryOption.NOT_NULL,QueryOption.PRIMARY_KEY,QueryOption.AUTO_INCREMENT)
                        .create("uuid",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("name",ColumnType.VARCHAR,20,QueryOption.NOT_NULL)
                        .create("color",ColumnType.VARCHAR,10,QueryOption.NOT_NULL)
                        .create("lastIp",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("lastCountry",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("lastLogin",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("firstLogin",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("onlineTime",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("bypass",ColumnType.VARCHAR,10,QueryOption.NOT_NULL)
                        .create("teamChatLogin",ColumnType.VARCHAR,10,QueryOption.NOT_NULL)
                        .create("reportLogin",ColumnType.VARCHAR,10,QueryOption.NOT_NULL)
                        .create("watchingReportedPlayer",ColumnType.VARCHAR,80)
                        .create("statsLogins",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsMessages",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsReports",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsReportsAccepted",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsReportsReceived",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsStaffBans",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsStaffMutes",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsStaffKicks",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsStaffWarns",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("statsStaffUnbans",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("properties",ColumnType.MEDIUMTEXT,QueryOption.NOT_NULL).execute();
                this.chatlogs.create().create("uuid",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("message",ColumnType.VARCHAR,500,QueryOption.NOT_NULL)
                        .create("server",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("time",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("filter",ColumnType.VARCHAR,20,QueryOption.NOT_NULL).execute();
                this.filters.create().create("id",ColumnType.INT,QueryOption.NOT_NULL,QueryOption.PRIMARY_KEY,QueryOption.AUTO_INCREMENT)
                        .create("message",ColumnType.VARCHAR,100,QueryOption.NOT_NULL)
                        .create("operation",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("type",ColumnType.VARCHAR,50,QueryOption.NOT_NULL).execute();
                this.broadcasts.create().create("id",ColumnType.INT,QueryOption.NOT_NULL,QueryOption.PRIMARY_KEY,QueryOption.AUTO_INCREMENT)
                        .create("message",ColumnType.VARCHAR,1000,QueryOption.NOT_NULL)
                        .create("hover",ColumnType.VARCHAR,500,QueryOption.NOT_NULL)
                        .create("clickType",ColumnType.VARCHAR,30,QueryOption.NOT_NULL)
                        .create("clickMessage",ColumnType.VARCHAR,500,QueryOption.NOT_NULL)
                        .create("permission",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("auto",ColumnType.VARCHAR,10,QueryOption.NOT_NULL)
                        .create("created",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("lastChange",ColumnType.BIG_INT,QueryOption.NOT_NULL).execute();
                this.reports.create() .create("uuid",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("reporter",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("staff",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("reason",ColumnType.VARCHAR,200,QueryOption.NOT_NULL)
                        .create("message",ColumnType.VARCHAR,200,QueryOption.NOT_NULL)
                        .create("reportedServer",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("reasonID",ColumnType.VARCHAR,10,QueryOption.NOT_NULL)
                        .create("time",ColumnType.BIG_INT,QueryOption.NOT_NULL).execute();
                this.histories.create().create("id",ColumnType.INT,QueryOption.NOT_NULL,QueryOption.PRIMARY_KEY,QueryOption.AUTO_INCREMENT)
                        .create("uuid",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("ip",ColumnType.VARCHAR,60,QueryOption.NOT_NULL)
                        .create("reason",ColumnType.VARCHAR,200,QueryOption.NOT_NULL)
                        .create("message",ColumnType.VARCHAR,200,QueryOption.NOT_NULL)
                        .create("time",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("points",ColumnType.INT,QueryOption.NOT_NULL)
                        .create("reasonID",ColumnType.INT,QueryOption.NOT_NULL)
                        .create("staff",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("type",ColumnType.VARCHAR,15,QueryOption.NOT_NULL)
                        .create("jsonEntryObject",ColumnType.TEXT,QueryOption.NOT_NULL).execute();
                this.onlineSessions.create()
                        .create("uuid",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("ip",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("country",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("lastServer",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("proxy",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("clientLanguage",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("clientVersion",ColumnType.VARCHAR,50,QueryOption.NOT_NULL)
                        .create("connected",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("disconnected",ColumnType.BIG_INT,QueryOption.NOT_NULL).execute();
                this.networkStats.create().create("logins",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("reports",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("reportsAccepted",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("messages",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("bans",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("mutes",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("unbans",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("kicks",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("warns",ColumnType.BIG_INT,QueryOption.NOT_NULL).execute();

                this.ipbans.create().create("ip",ColumnType.VARCHAR,60,QueryOption.NOT_NULL)
                        .create("lastPlayer",ColumnType.VARCHAR,80,QueryOption.NOT_NULL)
                        .create("timeStamp",ColumnType.BIG_INT,QueryOption.NOT_NULL)
                        .create("timeOut",ColumnType.BIG_INT,QueryOption.NOT_NULL).execute();
                this.settings.create().create("name",ColumnType.VARCHAR,150,QueryOption.NOT_NULL,QueryOption.PRIMARY_KEY)
                        .create("value",ColumnType.MEDIUMTEXT,QueryOption.NOT_NULL).execute();
                if(networkStats.select().execute("SELECT COUNT(*) FROM "+networkStats.getName(), result -> {
                    try{
                        if(result.next())return result.getInt(1);
                    }catch (Exception exception){}
                    return 0;
                }) == 0){
                    this.networkStats.insert().insert("logins").insert("reports").insert("reportsAccepted").insert("messages")
                            .insert("bans").insert("mutes").insert("unbans").insert("kicks").insert("warns").value(0).value(0).value(0)
                            .value(0).value(0).value(0).value(0).value(0).value(0).execute();
                }else{
                    try{
                        this.players.execute("ALTER TABLE "+players.getName()+" ADD COLUMN watchingReportedPlayer varchar(80) AFTER reportLogin");
                        this.players.execute("ALTER TABLE "+players.getName()+" ADD COLUMN statsStaffWarns bigint AFTER statsStaffKicks");
                        this.networkStats.execute("ALTER TABLE "+networkStats.getName()+" ADD COLUMN warns bigint");
                    }catch (Exception exception){}
                }
            }catch (Exception exception){
                exception.printStackTrace();
                connect = false;
            }
        }
        return connect;
    }

    @Override
    public boolean isConnected() {
        return sql.isConnected();
    }

    @Override
    public void disconnect() {
        sql.disconnect();
    }

    @Override
    public Collection<NetworkPlayer> getPlayers() {
        return this.players.selectAll().execute(result -> {
            Collection<NetworkPlayer> players1 = new ArrayList<>();
            try {
                while (result.next()) {
                    players1.add(loadPlayer(result));
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return players1;
        });
    }

    @Override
    public NetworkPlayer getPlayer(int id) throws Exception {
        return getPlayer("id",id);
    }

    @Override
    public NetworkPlayer getPlayer(String name) throws Exception {
        return getPlayer("name",name);
    }

    @Override
    public NetworkPlayer getPlayer(UUID uuid) throws Exception {
        return getPlayer("uuid",uuid.toString());
    }

    public NetworkPlayer getPlayer(String key, Object value){
        NetworkPlayer player = players.select().where(key,value).execute(result -> {
            try{
               if(result.next()){
                   return loadPlayer(result);
               }
            }catch (Exception exception){
                exception.printStackTrace();
            }
            return null;
        });
        return player;
    }

    private NetworkPlayer loadPlayer(ResultSet result) throws SQLException {
        UUID uuid = UUID.fromString(result.getString("uuid"));
        NetworkPlayer resultPlayer = new NetworkPlayer(result.getInt("id"),uuid
                ,result.getString("name"),result.getString("color"),result.getString("lastIp")
                ,result.getString("lastCountry"),result.getLong("lastLogin"),result.getLong("firstLogin")
                ,result.getLong("onlineTime"),result.getBoolean("bypass"),result.getBoolean("teamChatLogin")
                ,result.getBoolean("reportLogin"),null,Document.loadData(result.getString("properties"))
                ,new PlayerStats(result.getLong("statsLogins")
                ,result.getLong("statsReports"),result.getLong("statsReportsAccepted")
                ,result.getLong("statsMessages"),result.getLong("statsReportsReceived")
                ,result.getLong("statsStaffBans"),result.getLong("statsStaffMutes")
                ,result.getLong("statsStaffUnbans"),result.getLong("statsStaffKicks")
                ,result.getLong("statsStaffWarns")),new ArrayList<>(),null);
        resultPlayer.setHistory(getHistory(resultPlayer.getUUID()));
        resultPlayer.setReports(getReports(resultPlayer.getUUID()));
        resultPlayer.setOnlineSessions(getSessions(resultPlayer.getUUID()));
        return resultPlayer;
    }
    public List<OnlineSession> getSessions(UUID uuid){
        return onlineSessions.select().where("uuid",uuid.toString()).execute(result -> {
            List<OnlineSession> sessions = new ArrayList<>();
            try{
                while(result.next()){
                    sessions.add(new OnlineSession(result.getString("ip"),result.getString("country")
                            ,result.getString("lastServer"),result.getString("proxy"),result.getString("clientLanguage")
                            ,result.getInt("clientVersion"),result.getLong("connected"),result.getLong("disconnected")));
                }
            }catch (Exception e){}
            return sessions;
        });
    }

    @Override
    public List<NetworkPlayer> getPlayersByIp(String ip) {
        List<NetworkPlayer> player = players.select().where("lastIp",ip).execute(result -> {
            List<NetworkPlayer> players = new ArrayList<>();
            try{
                while(result.next()){
                    UUID uuid = UUID.fromString(result.getString("uuid"));
                    players.add(new NetworkPlayer(result.getInt("id"),uuid
                            ,result.getString("name"),result.getString("color"),result.getString("lastIp")
                            ,result.getString("lastCountry"),result.getLong("lastLogin"),result.getLong("firstLogin")
                            ,result.getLong("onlineTime"),result.getBoolean("bypass"),result.getBoolean("teamChatLogin")
                            ,result.getBoolean("reportLogin"),null,Document.loadData(result.getString("properties"))
                            ,new PlayerStats(result.getLong("statsLogins")
                            ,result.getLong("statsReports"),result.getLong("statsReportsAccepted")
                            ,result.getLong("statsMessages")
                            ,result.getLong("statsStaffBans"),result.getLong("statsStaffMutes")
                            ,result.getLong("statsStaffUnbans"),result.getLong("statsStaffKicks")
                            ,result.getLong("statsStaffWarns"),result.getLong("statsReportsReceived")),new ArrayList<>(),null));
                }
            }catch (Exception exception){}
            return players;
        });
        GeneralUtil.iterateForEach(player, object -> {
            object.setHistory(getHistory(object.getUUID()));
            object.setReports(getReports(object.getUUID()));
            object.setOnlineSessions(getSessions(object.getUUID()));
        });
        return player;
    }
    public List<Report> getReports(UUID player){
        return reports.select().where("uuid",player.toString()).execute(result -> {
            List<Report> reports = new ArrayList<>();
            try{
                while(result.next()) reports.add(new Report(UUID.fromString(result.getString("uuid")),
                        UUID.fromString(result.getString("staff")),UUID.fromString(result.getString("reporter"))
                        ,result.getString("reason"),result.getString("message"),result.getString("reportedServer")
                        ,result.getLong("time"),result.getInt("reasonID")));
            }catch (Exception e){}
            return reports;
        });
    }
    @Override
    public int getRegisteredPlayerCount() {
        return players.select().execute("SELECT COUNT(*) FROM "+players.getName(), result -> {
            try{
                if(result.next()){
                    return result.getInt(1);
                }
            }catch (Exception exception){}
            return 0;
        });
    }

    @Override
    public ChatLog getChatLog(UUID player) {
        return getChatLog(chatlogs.select().where("uuid",player.toString()));
    }

    @Override
    public ChatLog getChatLog(String server) {
        return getChatLog(chatlogs.select().where("server",server));
    }
    @Override
    public ChatLog getChatLog(UUID player, String server) {
        return getChatLog(chatlogs.select().where("server",server).where("uuid",player.toString()));
    }
    public ChatLog getChatLog(SelectQuery query){
        return query.execute(result -> {
            List<ChatLogEntry> entries = new LinkedList<>();
            try{
                while(result.next()){
                    entries.add(new ChatLogEntry(UUID.fromString(result.getString("uuid")),result.getString("message")
                            ,result.getString("server"),result.getLong("time"), FilterType.parseNull(result.getString("filter"))));
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            return new ChatLog(entries);
        });
    }

    @Override
    public HistoryEntry getHistoryEntry(int id) {
        return this.histories.select().where("id",id).execute(result -> {
            try{
                if(result.next()){
                    HistoryEntry entry = GeneralUtil.GSON.fromJson(result.getString("jsonEntryObject"),HistoryEntry.class);
                    entry.setID(result.getInt("id"));
                    return entry;
                }
            }catch (Exception e){}
            return null;
        });
    }

    @Override
    public History getHistory(UUID player) {
        return histories.select().where("uuid",player.toString()).execute(result -> {
            Map<Integer,HistoryEntry> entries = new HashMap<>();
            try{
                while(result.next()){
                    HistoryEntry entry = GeneralUtil.GSON.fromJson(result.getString("jsonEntryObject"),HistoryEntry.class);
                    entry.setID(result.getInt("id"));
                    entries.put(result.getInt("id"),entry);
                }
                return new History(entries);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public List<Report> getReports() {
        return reports.select().execute(result -> {
            List<Report> reports = new ArrayList<>();
            try{
                while(result.next()) reports.add(new Report(UUID.fromString(result.getString("uuid")),
                        UUID.fromString(result.getString("staff")),UUID.fromString(result.getString("reporter"))
                        ,result.getString("reason"),result.getString("message"),result.getString("reportedServer")
                        ,result.getLong("time"),result.getInt("reasonID")));
            }catch (Exception e){}
            return reports;
        });
    }

    @Override
    public List<Ban> getBans() {
        return this.histories.select().where("type","ban").execute(result -> {
            List<Ban> bans = new ArrayList<>();
            try {
                while(result.next()){
                    bans.add((Ban) GeneralUtil.GSON.fromJson(result.getString("jsonEntryObject"),HistoryEntry.class));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return bans;
        });
    }

    @Override
    public List<Ban> getNotTimeOutedBans() {
        return getBans();
    }

    @Override
    public List<Filter> loadFilters() {
        return this.filters.select().execute(result -> {
            List<Filter> filters = new ArrayList<>();
            try{
                while(result.next()){
                    filters.add(new Filter(result.getInt("id"),result.getString("message")
                            ,FilterOperation.valueOf(result.getString("operation"))
                            ,FilterType.valueOf(result.getString("type"))));
                }
            }catch (Exception exception){}
            return filters;
        });
    }

    @Override
    public List<Broadcast> loadBroadcasts() {
        return broadcasts.select().execute(result -> {
            List<Broadcast> broadcasts = new ArrayList<>();
            try{
                while(result.next()){
                    //Info: Get boolean is a temporary solution (Data Type confusion) -> Boolean.parseBoolean(result.getString("auto"))
                    broadcasts.add(new Broadcast(result.getInt("id"),result.getString("message")
                            ,result.getString("permission")
                            ,result.getString("hover"),result.getLong("created"),result.getLong("lastChange")
                            ,Boolean.parseBoolean(result.getString("auto")),new Broadcast.Click(result.getString("clickMessage")
                            ,Broadcast.ClickType.valueOf(result.getString("clickType")))));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return broadcasts;
        });
    }

    @Override
    public NetworkStats getNetworkStats() {
        return networkStats.select().execute(result -> {
            try{
                if(result.next()){
                    return new NetworkStats(result.getLong("logins"),result.getLong("reports")
                            ,result.getLong("reportsAccepted")
                            ,result.getLong("messages"),result.getLong("bans"),result.getLong("mutes")
                            ,result.getLong("unbans"),result.getLong("kicks"),result.getLong("warns"));
                }
            }catch (Exception exception){}
            return new NetworkStats();
        });
    }

    @Override
    public IPBan getIpBan(String ip) {
        return ipbans.select().where("ip",ip).execute(result -> {
            try{
                while(result.next()){
                    return new IPBan(result.getString("lastPlayer").equalsIgnoreCase("NULL")?null
                            :UUID.fromString(result.getString("lastPlayer"))
                            ,result.getString("ip"),result.getLong("timeStamp")
                            ,result.getLong("timeOut"));
                }
            }catch (Exception ignored){}
            return null;
        });
    }

    @Override
    public Collection<IPBan> getIpBans() {
        return ipbans.selectAll().execute(result -> {
            Collection<IPBan> ipBans = new ArrayList<>();
            try{

                while(result.next()){
                    ipBans.add(new IPBan(result.getString("lastPlayer").equalsIgnoreCase("NULL")?null
                            :UUID.fromString(result.getString("lastPlayer"))
                            ,result.getString("ip"),result.getLong("timeStamp")
                            ,result.getLong("timeOut")));
                }
            }catch (Exception ignored){}
            return ipBans;
        });
    }

    @Override
    public Document getSetting(String name) {
        return this.settings.select().where("name",name).execute(result -> {
            try{while(result.next()) return Document.loadData(result.getString("value"));
            }catch (Exception ignored){}
            return null;
        });
    }
}
