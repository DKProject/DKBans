package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
        return entries;
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
                entries.addAll(DKBans.getInstance().getStorage().loadActiveEntries(player.getUniqueId()));
                loadedActive = true;
            }
            return entries;
        }
    }

    @Override
    public int calculateAmount(PunishmentType punishmentType) {
        return 0;
    }

    @Override
    public int calculateAmount(PlayerHistoryType historyType, PunishmentType punishmentType) {
        return 0;
    }

    @Override
    public int calculatePoints(PlayerHistoryType type) {
        int result = 0;
        for (PlayerHistoryEntry entry : getEntries()) {
            PlayerHistoryEntrySnapshot snapshot = entry.getCurrent();
            if(snapshot.getHistoryType().equals(type)){
                result += snapshot.getPoints();
            }
        }
        return result;
    }
}
