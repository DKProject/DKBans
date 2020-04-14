package net.pretronic.dkbans.api;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.util.UUID;

public interface DKBansExecutor {

    String getName();

    UUID getUniqueId();

    default boolean isPlayer(){
        return getPlayer() != null;
    }

    @Nullable
    DKBansPlayer getPlayer();

}
