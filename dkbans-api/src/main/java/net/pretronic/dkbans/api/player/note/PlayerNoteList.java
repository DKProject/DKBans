package net.pretronic.dkbans.api.player.note;

import java.util.Collection;

public interface PlayerNoteList extends Iterable<PlayerNote> {

    Collection<PlayerNote> getAll();

    Collection<PlayerNote> getLast(int amount);

    Collection<PlayerNote> getFirst(int amount);

    Collection<PlayerNote> getByIndex(int index);

    Collection<PlayerNote> getByIndexRange(int startIndex, int endIndex);

    Collection<PlayerNote> getSince(long time);

    Collection<PlayerNote> getUntil(long time);

    Collection<PlayerNote> getBetween(long startTime, long endTime);
}
