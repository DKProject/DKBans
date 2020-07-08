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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DefaultPlayerHistory implements PlayerHistory {

    private final DKBansPlayer player;

    private final List<PlayerHistoryEntry> entries;

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
    public PlayerHistoryEntry getActiveEntry(PunishmentType type, DKBansScope scope) {
        return Iterators.findOne(getActiveEntries(), entry -> entry.getCurrent().getPunishmentType() == type
                && entry.getCurrent().getScope().matches(scope));
    }

    @Override
    public List<PlayerHistoryEntry> getEntries() {
        if(!loadedAll){
            this.entries.addAll(DKBans.getInstance().getStorage().loadEntries(this,loadedActive));
            this.loadedAll = true;
        }
        return entries;
    }

    @Override
    public List<PlayerHistoryEntry> getEntries(PunishmentType punishmentType) {
        return Iterators.filter(getEntries(), entry -> entry.getCurrent().getPunishmentType() == punishmentType);
    }

    @Override
    public List<PlayerHistoryEntry> getEntries(PlayerHistoryType historyType) {
        return Iterators.filter(getEntries(), entry -> entry.getCurrent().getHistoryType().equals(historyType));
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
    public List<PlayerHistoryEntry> getActiveEntries(PunishmentType type) {
        return Iterators.filter(getActiveEntries(), entry -> entry.getCurrent().getPunishmentType() == type);
    }

    @Override
    public List<PlayerHistoryEntry> getActiveEntries(PunishmentType type, DKBansScope scope) {
        return Iterators.filter(getActiveEntries(), entry -> entry.getCurrent().getPunishmentType() == type
                && entry.getCurrent().getScope().matches(scope));
    }

    @Override
    public List<PlayerHistoryEntry> getActiveEntries(PlayerHistoryType historyType) {
        return Iterators.filter(getActiveEntries(), entry -> entry.getCurrent().getHistoryType() == historyType);
    }

    @Override
    public List<PlayerHistoryEntry> getActiveEntries(PlayerHistoryType historyType, DKBansScope scope) {
        return Iterators.filter(getActiveEntries(), entry -> entry.getCurrent().getHistoryType() == historyType
                && entry.getCurrent().getScope().matches(scope));
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

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }

    public void push(PlayerHistoryEntry entry){
        if(loadedActive || loadedAll){
            this.entries.add(entry);
        }
    }
}
