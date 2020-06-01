package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;

import java.util.List;

public interface PlayerHistoryEntry extends Iterable<PlayerHistoryEntrySnapshot>{

    int getId();

    PlayerHistory getHistory();

    PlayerSession getSession();


    PlayerHistoryEntrySnapshot getCurrent();

    PlayerHistoryEntrySnapshot getFirst();

    List<PlayerHistoryEntrySnapshot> getAll();


    PlayerHistoryEntrySnapshotBuilder newSnapshot(DKBansExecutor executor);

    //update


    List<PlayerNote> getNotes();

    default PlayerNote createNote(DKBansPlayer creator, String message){
        return createNote(creator,message, PlayerNoteType.NORMAL);
    }

    PlayerNote createNote(DKBansPlayer creator, String message, PlayerNoteType type);

}
