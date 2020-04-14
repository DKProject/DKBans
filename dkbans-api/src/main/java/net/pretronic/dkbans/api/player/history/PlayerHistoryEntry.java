package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;

import java.util.List;

public interface PlayerHistoryEntry {

    int getId();

    DKBansPlayer getPlayer();

    PlayerSession getSession();

    PlayerHistoryType getHistoryType();

    PunishmentType getPunishmentType();

    //getTempalte


    PlayerHistoryEntryData getCurrent();

    PlayerHistoryEntryData getFirst();

    List<PlayerHistoryEntryData> getAll();


    //update


    List<PlayerNote> getNotes();

    default PlayerNote createNote(DKBansPlayer creator, String message){
        return createNote(creator,message, PlayerNoteType.NORMAL);
    }

    PlayerNote createNote(DKBansPlayer creator, String message, PlayerNoteType type);

}
