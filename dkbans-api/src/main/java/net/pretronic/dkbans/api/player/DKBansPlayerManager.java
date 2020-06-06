package net.pretronic.dkbans.api.player;

import java.util.UUID;

public interface DKBansPlayerManager {

    DKBansPlayer getPlayer(UUID uniqueId);

    DKBansPlayer getPlayer(String name);

}
