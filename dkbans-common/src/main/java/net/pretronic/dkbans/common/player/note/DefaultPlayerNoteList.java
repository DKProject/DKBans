package net.pretronic.dkbans.common.player.note;

import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DefaultPlayerNoteList implements PlayerNoteList {

    private final Collection<PlayerNote> cached;

    public DefaultPlayerNoteList() {
        this.cached = new ArrayList<>();
    }

    @Override
    public Collection<PlayerNote> getAll() {
        return this.cached;
    }

    @Override
    public Collection<PlayerNote> getLast(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getFirst(int amount) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getByIndex(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getByIndexRange(int startIndex, int endIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getSince(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getUntil(long time) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<PlayerNote> getBetween(long startTime, long endTime) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<PlayerNote> iterator() {
        throw new UnsupportedOperationException();
    }
}
