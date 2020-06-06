package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryManager;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.libraries.utility.Iterators;

import java.util.Collection;

public class DefaultPlayerHistoryManager implements PlayerHistoryManager {

    private final Collection<PlayerHistoryType> historyTypes;

    public DefaultPlayerHistoryManager(DKBans dkBans) {
        this.historyTypes = dkBans.getStorage().loadPlayerHistoryTypes();
    }

    @Override
    public Collection<PlayerHistoryEntry> getActivePunishments(String type) {
        return null;
    }

    @Override
    public PlayerHistoryEntry getHistoryEntry(int id) {
        return null;
    }

    @Override
    public PlayerHistoryType getHistoryType(int id) {
        return Iterators.findOne(this.historyTypes, historyType -> historyType.getId() == id);
    }

    @Override
    public PlayerHistoryType getHistoryType(String name) {
        return Iterators.findOne(this.historyTypes, historyType -> historyType.getName().equalsIgnoreCase(name));
    }
}
