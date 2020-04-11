package net.pretronic.dkbans.api.player.note;

import net.pretronic.dkbans.api.player.DKBansPlayer;

public interface PlayerNote {

    int getId();

    PlayerNoteType getType();

    long getTime();

    String getMessage();

    DKBansPlayer getCreator();




}
