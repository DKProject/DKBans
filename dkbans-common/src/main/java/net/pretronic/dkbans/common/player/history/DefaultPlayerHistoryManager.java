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

package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.libraries.utility.Iterators;

import java.util.Collection;

public class DefaultPlayerHistoryManager implements PlayerHistoryManager {

    private final Collection<PlayerHistoryType> historyTypes;

    public DefaultPlayerHistoryManager(DKBans dkBans) {
        this.historyTypes = dkBans.getStorage().loadPlayerHistoryTypes();
    }

    @Override
    public PunishmentList getActivePunishments(PunishmentType type) {
        return new DefaultPunishmentList(type);
    }

    @Override
    public PunishmentList getActivePunishments(PunishmentType type, DKBansScope scope) {
        return new DefaultPunishmentList(type);//@Todo add scope check
    }

    @Override
    public PlayerHistoryEntry getHistoryEntry(int id) {
        //Search cached objects
        for (DKBansPlayer player : (DKBans.getInstance().getPlayerManager()).getLoadedPlayers()) {
            for (PlayerHistoryEntry entry : player.getHistory().getLoadedEntries()) {
                if(entry.getId() == id){
                    return entry;
                }
            }
        }
        return DKBans.getInstance().getStorage().loadEntry(id);
    }

    @Override
    public Collection<PlayerHistoryType> getHistoryTypes() {
        return historyTypes;
    }

    @Override
    public PlayerHistoryType getHistoryType(int id) {
        return Iterators.findOne(this.historyTypes, historyType -> historyType.getId() == id);
    }

    @Override
    public PlayerHistoryType getHistoryType(String name) {
        return Iterators.findOne(this.historyTypes, historyType -> historyType.getName().equalsIgnoreCase(name));
    }

    @Override
    public PlayerHistoryType createHistoryType(String name) {
        PlayerHistoryType historyType = DKBans.getInstance().getStorage().createPlayerHistoryType(name);
        this.historyTypes.add(historyType);
        return historyType;
    }
}
