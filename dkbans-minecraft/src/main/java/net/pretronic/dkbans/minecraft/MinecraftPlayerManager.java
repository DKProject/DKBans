package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.DKBansPlayerManager;
import net.pretronic.dkbans.common.player.DefaultDKBansPlayer;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MinecraftPlayerManager implements DKBansPlayerManager {

    private final Cache<DKBansPlayer> players;

    public MinecraftPlayerManager() {
        this.players = new ArrayCache<>();
        this.players.setMaxSize(1024);
        this.players.setExpireAfterAccess(10, TimeUnit.MINUTES);
    }

    @Override
    public DKBansPlayer getPlayer(UUID uniqueId) {
        return getPlayer(McNative.getInstance().getPlayerManager().getPlayer(uniqueId));
    }

    @Override
    public DKBansPlayer getPlayer(String name) {
        return getPlayer(McNative.getInstance().getPlayerManager().getPlayer(name));
    }

    public DKBansPlayer getPlayer(MinecraftPlayer player){//@Todo temporary
        if(player == null) return null;
        DKBansPlayer player1 = players.get(player0 -> player0.getUniqueId().equals(player.getUniqueId()));
        if(player1 == null) {
            player1 = new DefaultDKBansPlayer(player.getUniqueId(),player.getName());
            this.players.insert(player1);
        }
        return player1;
    }
}
