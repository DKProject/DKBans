/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 18:51
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

package ch.dkrieger.bansystem.lib.broadcast;

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.Messages;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import ch.dkrieger.bansystem.lib.utils.GeneralUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BroadcastManager {

    private Map<Integer,Broadcast> broadcasts;
    private Map<String,BuildAdapter> buildAdapters;
    private int nextBroadcast;

    public BroadcastManager() {
        nextBroadcast = 1;
        this.broadcasts = new HashMap<>();
        this.buildAdapters = new HashMap<>();

        registerBuildAdapter("player",NetworkPlayer::getColoredName);
        registerBuildAdapter("country",NetworkPlayer::getCountry);
        registerBuildAdapter("time", player -> BanSystem.getInstance().getConfig().dateFormat.format(System.currentTimeMillis()));
        registerBuildAdapter("prefix",player -> Messages.PREFIX_NETWORK);

        reloadLocal();
    }

    public Broadcast getNext(){
        List<Broadcast> broadcasts = getAutoBroadcasts();
        broadcasts.sort((o1, o2) -> (o1.getID()>o2.getID()?1:-1));
        if(broadcasts.size() < 1) return null;
        if(BanSystem.getInstance().getConfig().autobroadcastSorted){
            Broadcast broadcast = null;
            int lastId = 0;
            for(Broadcast bc : broadcasts){
                if(bc.getID()==nextBroadcast){
                    nextBroadcast++;
                    return bc;
                }
                if(broadcast == null || (bc.getID() > nextBroadcast && bc.getID() < lastId)){
                    lastId = bc.getID();
                    broadcast = bc;
                }
            }
            nextBroadcast = broadcast.getID()+1;
            return broadcast;
        }else return broadcasts.get(GeneralUtil.RANDOM.nextInt(broadcasts.size()));
    }

    public void registerBuildAdapter(String replace, BuildAdapter adapter){
        this.buildAdapters.put(replace,adapter);
    }

    public Broadcast getBroadcast(int id){
        return this.broadcasts.get(id);
    }

    public List<Broadcast> getAutoBroadcasts(){
        List<Broadcast> broadcasts = new ArrayList<>();
        GeneralUtil.iterateAcceptedForEach(this.broadcasts.values(),Broadcast::isAuto,broadcasts::add);
        return broadcasts;
    }

    public List<Broadcast> getBroadcasts(){
        return new ArrayList<>(broadcasts.values());
    }


    public void reloadLocal(){
        this.broadcasts.clear();
        GeneralUtil.iterateForEach(BanSystem.getInstance().getStorage().loadBroadcasts(),object ->broadcasts.put(object.getID(),object));
    }

    public interface BuildAdapter {
        String build(NetworkPlayer player);
    }
}
