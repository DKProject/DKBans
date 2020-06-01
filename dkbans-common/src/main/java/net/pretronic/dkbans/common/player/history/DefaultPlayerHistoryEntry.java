package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;

import java.util.Iterator;
import java.util.List;

public class DefaultPlayerHistoryEntry implements PlayerHistoryEntry {

    private final PlayerHistory history;
    private final int id;

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public PlayerHistory getHistory() {
        return history;
    }

    @Override
    public PlayerSession getSession() {
        return null;
    }

    @Override
    public PlayerHistoryEntrySnapshot getCurrent() {
        return null;
    }

    @Override
    public PlayerHistoryEntrySnapshot getFirst() {
        return null;
    }

    @Override
    public List<PlayerHistoryEntrySnapshot> getAll() {
        return null;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder newSnapshot(DKBansExecutor executor) {
        return null;
    }

    @Override
    public List<PlayerNote> getNotes() {
        return null;
    }

    @Override
    public PlayerNote createNote(DKBansPlayer creator, String message, PlayerNoteType type) {
        return null;
    }

    @Override
    public Iterator<PlayerHistoryEntrySnapshot> iterator() {
        return getAll().iterator();
    }
}
