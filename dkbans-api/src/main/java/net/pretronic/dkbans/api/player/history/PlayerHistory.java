package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.player.DKBansPlayer;

import java.util.List;

public interface PlayerHistory {

    DKBansPlayer getPlayer();

    PlayerHistoryEntry getActiveEntry(PunishmentType type);

    List<PlayerHistoryEntry> getEntries();

    List<PlayerHistoryEntry> getActiveEntries();


    int calculatePoints(PlayerHistoryType type);

}
