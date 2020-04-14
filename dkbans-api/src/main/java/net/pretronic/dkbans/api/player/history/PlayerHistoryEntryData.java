package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.libraries.document.Document;

public interface PlayerHistoryEntryData {

    PlayerHistoryEntry getEntry();

    int getId();

    String getReason();

    long getTimeout();

    DKBansExecutor getStuff();

    DKBansScope getScope();

    int getPoints();


    long getModifyTime();

    DKBansExecutor getModifiedBy();

    boolean isActive();

    Document getProperties();


}
