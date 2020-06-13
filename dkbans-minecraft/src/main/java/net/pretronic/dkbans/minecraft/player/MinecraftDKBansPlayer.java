package net.pretronic.dkbans.minecraft.player;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.common.player.DefaultDKBansPlayer;
import net.pretronic.dkbans.common.player.DefaultPlayerSession;
import org.mcnative.common.McNative;
import org.mcnative.common.player.ConnectedMinecraftPlayer;

import java.net.InetAddress;
import java.util.UUID;

public class MinecraftDKBansPlayer extends DefaultDKBansPlayer {

    public MinecraftDKBansPlayer(UUID uniqueId, String name) {
        super(uniqueId, name);
    }

    @Override
    public void startSession(String currentName, InetAddress address) {
        ConnectedMinecraftPlayer player = McNative.getInstance().getLocal().getConnectedPlayer(getUniqueId());
        //@Todo set missing data
        DefaultPlayerSession session = new DefaultPlayerSession(-1,
                this,
                getName(),
                player.getAddress().getAddress(),
                "none",
                "none",
                "none",//player.getProxy().getName()
                UUID.randomUUID(),//player.getProxy().getIdentifier().getUniqueId()
                player.getServer().getName(),
                player.getServer().getIdentifier().getUniqueId(),
                player.getProtocolVersion().getEdition().getName(),
                player.getProtocolVersion().getNumber(),
                System.currentTimeMillis(),
                -1);
        int sessionId = DKBans.getInstance().getStorage().startPlayerSession(session);
        session.setId(sessionId);
        this.lastSession = session;
    }
}
