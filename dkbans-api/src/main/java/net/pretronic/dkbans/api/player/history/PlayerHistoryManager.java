package net.pretronic.dkbans.api.player.history;

import java.util.Collection;

public interface PlayerHistoryManager {

    //@Todo add callback with size maybe create in library
    Collection<PlayerHistoryEntry> getActivePunishments(String type);

    PlayerHistoryEntry getHistoryEntry(int id);

    PlayerHistoryType getHistoryType(int id);

    PlayerHistoryType getHistoryType(String name);

}
