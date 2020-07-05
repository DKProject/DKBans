/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 18:42
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

package ch.dkrieger.bansystem.lib.player;

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.Messages;
import ch.dkrieger.bansystem.lib.filter.FilterType;
import ch.dkrieger.bansystem.lib.player.chatlog.ChatLog;
import ch.dkrieger.bansystem.lib.player.chatlog.ChatLogEntry;
import ch.dkrieger.bansystem.lib.utils.Document;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

    private Map<UUID,NetworkPlayer> loadedPlayers;
    private long cachedRegisteredCountTime;
    private int cachedRegisteredCount;

    public PlayerManager() {
        this.loadedPlayers = new HashMap<>();
        this.cachedRegisteredCount = -1;
        this.cachedRegisteredCountTime = -1;
    }
    public Map<UUID, NetworkPlayer> getLoadedPlayers() {
        return loadedPlayers;
    }
    public NetworkPlayer searchPlayer(String parser){
        try{
            if(parser.length() > 25){
                return getPlayer(UUID.fromString(parser));
            }
            else {
                NetworkPlayer player = null;
                if(parser.length() < 18) player = getPlayer(parser);
                if(player == null && GeneralUtil.isNumber(parser)) return getPlayer(Integer.valueOf(parser));
                return player;
            }
        }catch (Exception exception){}
        return null;
    }

    public int getRegisteredCount(){
        if(this.cachedRegisteredCount > 0 && cachedRegisteredCountTime > System.currentTimeMillis()) return this.cachedRegisteredCount;
        this.cachedRegisteredCount = BanSystem.getInstance().getStorage().getRegisteredPlayerCount();
        cachedRegisteredCountTime = System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(15);
        return cachedRegisteredCount;
    }
    public List<NetworkPlayer> getPlayers(String ip){
        return BanSystem.getInstance().getStorage().getPlayersByIp(ip);
    }

    public NetworkPlayer getPlayer(int id){
        NetworkPlayer player = GeneralUtil.iterateOne(this.loadedPlayers.values(), object -> object.getID() == id);
        if(player == null){
            try{
                player = BanSystem.getInstance().getStorage().getPlayer(id);
            }catch (Exception exception){}
            if(player != null) this.loadedPlayers.put(player.getUUID(),player);
        }
        return player;
    }
    public NetworkPlayer getPlayer(UUID uuid){
        NetworkPlayer player = loadedPlayers.get(uuid);
        if(player == null){
            try{
                player = getPlayerSave(uuid);
            }catch (Exception exception){}
        }
        return player;
    }
    public NetworkPlayer getPlayerSave(UUID uuid) throws Exception{
        NetworkPlayer player = BanSystem.getInstance().getStorage().getPlayer(uuid);
        if(player != null) this.loadedPlayers.put(uuid,player);
        return player;
    }
    public NetworkPlayer getPlayer(String name){
        NetworkPlayer player = GeneralUtil.iterateOne(this.loadedPlayers.values(), object -> object.getName().equalsIgnoreCase(name));
        if(player == null){
            try{
                player = BanSystem.getInstance().getStorage().getPlayer(name);
                if(player != null) this.loadedPlayers.put(player.getUUID(),player);
            }catch (Exception exception){}
        }
        return player;
    }
    public ChatLog getChatLog(NetworkPlayer player){
        return getChatLog(player.getUUID());
    }
    public ChatLog getChatLog(UUID uuid){
        return BanSystem.getInstance().getStorage().getChatLog(uuid);
    }
    public ChatLog getChatLog(String server){
        return BanSystem.getInstance().getStorage().getChatLog(server);
    }
    public ChatLog getChatLog(UUID uuid, String server){
        return BanSystem.getInstance().getStorage().getChatLog(uuid,server);
    }

    public IPBan getIpBan(String ip){
        IPBan ban = BanSystem.getInstance().getStorage().getIpBan(ip);
        if(ban != null && (ban.getTimeOut() > 0 && ban.getTimeOut() <= System.currentTimeMillis())){
            return null;
        }
        else return ban;
    }
    public boolean isIPBanned(String ip){
        return getIpBan(ip) != null;
    }

    public void removePlayerFromCache(NetworkPlayer player){
        removePlayerFromCache(player.getUUID());
    }


    public void removePlayerFromCache(UUID uuid){
        this.loadedPlayers.remove(uuid);
    }

    public void resetCachedCounts(){
        this.cachedRegisteredCount = -1;
    }
    public String getCountry(String ip){
        try{
            InputStream stream = new URL("http://ip-api.com/json/"+ip+"?fields=country").openStream();
            Scanner scanner = new Scanner(stream, "UTF-8").useDelimiter("\\A");
            Document document = Document.loadData(scanner.next());
            return document.contains("country")?document.getString("country"): Messages.UNKNOWN;
        }catch (Exception exception){}
        return Messages.UNKNOWN;
    }
}
