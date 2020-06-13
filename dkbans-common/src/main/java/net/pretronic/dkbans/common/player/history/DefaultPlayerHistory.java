/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.06.20, 17:19
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
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.List;

public class DefaultPlayerHistory implements PlayerHistory {

    private final DKBansPlayer player;

    private List<PlayerHistoryEntry> entries;

    private boolean loadedActive;
    private boolean loadedAll;


    public DefaultPlayerHistory(DKBansPlayer player) {
        this.player = player;
        this.entries = new ArrayList<>();
    }

    @Override
    public DKBansPlayer getPlayer() {
        return player;
    }

    @Override
    public PlayerHistoryEntry getActiveEntry(PunishmentType type) {
        for (PlayerHistoryEntry activeEntry : getActiveEntries()) {
            if(activeEntry.getCurrent().getPunishmentType().equals(type)){
                return activeEntry;
            }
        }
        return null;
    }

    @Override
    public PlayerHistoryEntry getLastEntry(PunishmentType type) {
        return null;
    }

    @Override
    public List<PlayerHistoryEntry> getEntries() {
        return getActiveEntries();//@Todo load all
    }

    @Override
    public List<PlayerHistoryEntry> getEntries(PunishmentType punishmentType) {
        return null;
    }

    @Override
    public List<PlayerHistoryEntry> getEntries(PlayerHistoryType historyType) {
        return null;
    }

    @Override
    public List<PlayerHistoryEntry> getActiveEntries() {
        if(loadedAll){
            return Iterators.filter(entries, entry -> entry.getCurrent().isActive());
        }else {
            if(!loadedActive){
                entries.addAll(DKBans.getInstance().getStorage().loadActiveEntries(this));
                loadedActive = true;
            }
            return entries;
        }
    }

    @Override
    public List<PlayerHistoryEntry> getEntries(int page, int size) {
        return null;
    }

    @Override
    public int calculate(CalculationType calculationType, PlayerHistoryType type) {
        if(calculationType == CalculationType.AMOUNT){
            int amount = 0;
            for (PlayerHistoryEntry entry : getEntries()) {
                if(entry.getCurrent().getHistoryType().equals(type)) amount++;
            }
            return amount;
        }else if(calculationType == CalculationType.POINTS){
            int result = 0;
            for (PlayerHistoryEntry entry : getEntries()) {
                PlayerHistoryEntrySnapshot snapshot = entry.getCurrent();
                if(snapshot.getHistoryType().equals(type)){
                    result += snapshot.getPoints();
                }
            }
            return result;
        }
        throw new IllegalArgumentException("Invalid calculation type");
    }

    @Override
    public int calculate(CalculationType calculationType, PunishmentType type) {
        if(calculationType == CalculationType.AMOUNT){
            int amount = 0;
            for (PlayerHistoryEntry entry : getEntries()) {
                if(entry.getCurrent().getPunishmentType().equals(type)) amount++;
            }
            return amount;
        }else if(calculationType == CalculationType.POINTS){
            int result = 0;
            for (PlayerHistoryEntry entry : getEntries()) {
                PlayerHistoryEntrySnapshot snapshot = entry.getCurrent();
                if(snapshot.getPunishmentType().equals(type)){
                    result += snapshot.getPoints();
                }
            }
            return result;
        }
        throw new IllegalArgumentException("Invalid calculation type");
    }
}
