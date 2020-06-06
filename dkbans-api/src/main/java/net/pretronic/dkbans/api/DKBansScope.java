package net.pretronic.dkbans.api;

import java.util.UUID;

public interface DKBansScope {

    String getType();

    String getName();

    UUID getId();


    boolean isCurrent();
}
