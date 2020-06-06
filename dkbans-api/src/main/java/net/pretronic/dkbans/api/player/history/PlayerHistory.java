package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.player.DKBansPlayer;

import java.util.List;

public interface PlayerHistory {

    DKBansPlayer getPlayer();

    PlayerHistoryEntry getActiveEntry(PunishmentType type);

    PlayerHistoryEntry getLastEntry(PunishmentType type);

    List<PlayerHistoryEntry> getEntries();

    List<PlayerHistoryEntry> getEntries(int page, int size);

    List<PlayerHistoryEntry> getEntries(PunishmentType punishmentType);

    List<PlayerHistoryEntry> getEntries(PlayerHistoryType historyType);

    List<PlayerHistoryEntry> getActiveEntries();


    default boolean hasActivePunish(PunishmentType type){
        return getActiveEntry(type) != null;
    }


    int calculate(CalculationType calculationType, PlayerHistoryType type);

    int calculate(CalculationType calculationType, PunishmentType type);


}
