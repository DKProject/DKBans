package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.libraries.document.Document;

public interface PlayerHistoryEntryData {

    PlayerHistoryEntry getEntry();

    int getId();

    String getReason();

    long getTimeout();

    DKBansPlayer getStuff();

    DKBansScope getScope();

    int getPoints();


    long getModifyTime();

    DKBansPlayer getModifiedBy();

    boolean isActive();

    Document getProperties();


}
