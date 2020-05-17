package net.pretronic.dkbans.api.player.note;

import net.pretronic.dkbans.api.DKBansExecutor;

public interface PlayerNote {

    int getId();

    PlayerNoteType getType();

    long getTime();

    String getMessage();

    DKBansExecutor getCreator();




}
