package net.pretronic.dkbans.api.support;

import net.pretronic.dkbans.api.player.DKBansPlayer;

public interface SupportMessage {


    DKBansPlayer getSender();

    String getMessage();

    long getTime();

}
