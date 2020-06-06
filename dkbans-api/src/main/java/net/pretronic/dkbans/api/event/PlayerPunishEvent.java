package net.pretronic.dkbans.api.event;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;

public interface PlayerPunishEvent extends DKBansEvent{

    DKBansPlayer getPlayer();

    PlayerHistoryEntry getEntry();

    PlayerHistoryEntrySnapshot getSnapshot();

}
