package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.Iterator;
import java.util.List;

public class DefaultPlayerHistoryEntry implements PlayerHistoryEntry {

    private final PlayerHistory history;
    private final int id;

    private PlayerHistoryEntrySnapshot current;
    private List<PlayerHistoryEntrySnapshot> snapshots;

    public DefaultPlayerHistoryEntry(PlayerHistory history, int id, PlayerHistoryEntrySnapshot current) {
        this.history = history;
        this.id = id;
        this.current = current;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PlayerHistory getHistory() {
        return history;
    }

    @Override
    public PlayerSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerHistoryEntrySnapshot getCurrent() {
        return current;
    }

    @Override
    public PlayerHistoryEntrySnapshot getFirst() {
        return getAll().get(0);
    }

    @Override
    public List<PlayerHistoryEntrySnapshot> getAll() {
        if(snapshots == null){
            //@Todo load
        }
        return snapshots;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder newSnapshot(DKBansExecutor executor) {
        return new DefaultPlayerHistoryEntrySnapshotBuilder(history.getPlayer(),this);
    }

    @Override
    public PlayerNoteList getNotes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerNote createNote(DKBansExecutor creator, String message, PlayerNoteType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<PlayerHistoryEntrySnapshot> iterator() {
        return getAll().iterator();
    }

    @Internal
    public void setCurrent(PlayerHistoryEntrySnapshot current){
        this.current = current;
    }
}
