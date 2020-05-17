package net.pretronic.dkbans.common.player;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.libraries.utility.annonations.Internal;

import java.net.InetAddress;
import java.util.UUID;

public class DefaultPlayerSession implements PlayerSession {

    private final int id;
    private final DKBansPlayer player;
    private final String sessionName;

    private final InetAddress address;
    private final String country;
    private final String region;

    private final String proxyName;
    private final UUID proxyId;
    private final String lastServerName;
    private final UUID lastServerId;

    private final String edition;
    private final int protocolVersion;

    private final long connectTime;
    private final long disconnectTime;

    public DefaultPlayerSession(int id, DKBansPlayer player, String sessionName, InetAddress address, String country
            , String region, String proxyName, UUID proxyId, String lastServerName, UUID lastServerId
            , String edition, int protocolVersion, long connectTime, long disconnectTime) {
        this.id = id;
        this.player = player;
        this.sessionName = sessionName;
        this.address = address;
        this.country = country;
        this.region = region;
        this.proxyName = proxyName;
        this.proxyId = proxyId;
        this.lastServerName = lastServerName;
        this.lastServerId = lastServerId;
        this.edition = edition;
        this.protocolVersion = protocolVersion;
        this.connectTime = connectTime;
        this.disconnectTime = disconnectTime;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public DKBansPlayer getPlayer() {
        return player;
    }

    @Override
    public String getPlayerSessionName() {
        return sessionName;
    }

    @Override
    public InetAddress getIpAddress() {
        return address;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public String getLastServerName() {
        return lastServerName;
    }

    @Override
    public UUID getLastServerId() {
        return lastServerId;
    }

    @Override
    public String getProxyName() {
        return proxyName;
    }

    @Override
    public UUID getProxyId() {
        return proxyId;
    }

    @Override
    public String getClientEdition() {
        return edition;
    }

    @Override
    public int getClientProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public long getConnectTime() {
        return connectTime;
    }

    @Override
    public long getDisconnectTime() {
        return disconnectTime;
    }

    @Override
    public boolean isActive() {
        return disconnectTime > -1;
    }

    @Internal
    public void completeSession(){

    }
}
