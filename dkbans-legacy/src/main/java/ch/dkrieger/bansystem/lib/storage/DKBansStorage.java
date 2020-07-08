/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 18:30
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

package ch.dkrieger.bansystem.lib.storage;

import ch.dkrieger.bansystem.lib.broadcast.Broadcast;
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
import ch.dkrieger.bansystem.lib.utils.Document;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface DKBansStorage {

    public boolean connect();

    public boolean isConnected();

    public void disconnect();

    Collection<NetworkPlayer> getPlayers();

    public NetworkPlayer getPlayer(int id) throws Exception;

    public NetworkPlayer getPlayer(String name) throws Exception;

    public NetworkPlayer getPlayer(UUID uuid) throws Exception;

    public List<NetworkPlayer> getPlayersByIp(String ip);

    public int getRegisteredPlayerCount();

    public ChatLog getChatLog(UUID player);

    public ChatLog getChatLog(String server);

    public ChatLog getChatLog(UUID player, String server);

    public History getHistory(UUID player);

    public List<Report> getReports();

    public HistoryEntry getHistoryEntry(int id);

    @SuppressWarnings("This methode is dangerous, it (can) return many datas and have a long delay.")
    public List<Ban> getNotTimeOutedBans( );

    @SuppressWarnings("This methode is dangerous, it (can) return many datas and have a long delay.")
    public List<Ban> getBans();

    public IPBan getIpBan(String ip);

    public List<Filter> loadFilters();

    public List<Broadcast> loadBroadcasts();

    public NetworkStats getNetworkStats();

    public Document getSetting(String name);
}
