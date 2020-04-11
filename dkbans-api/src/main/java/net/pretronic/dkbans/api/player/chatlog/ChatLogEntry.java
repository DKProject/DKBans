package net.pretronic.dkbans.api.player.chatlog;

import net.pretronic.dkbans.api.player.DKBansPlayer;

import java.util.UUID;

public interface ChatLogEntry {

    int getId();

    DKBansPlayer getPlayer();

    String getMessage();

    long getTime();


    String getServerName();

    UUID getServerId();

    //@Todo getFilter if rejected

}
