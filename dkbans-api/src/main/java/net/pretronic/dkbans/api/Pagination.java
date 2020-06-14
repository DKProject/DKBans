package net.pretronic.dkbans.api;

import java.util.List;

public interface Pagination<T> {

    List<T> getAll();

    List<T> getLast(int amount);

    T getLast();

    List<T> getFirst(int amount);

    T getByIndex(int index);

    List<T> getByIndexRange(int startIndex, int endIndex);

    List<T> getSince(long time);

    List<T> getUntil(long time);

    List<T> getBetween(long startTime, long endTime);
}
