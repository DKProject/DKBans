package net.pretronic.dkbans.api.player;

import java.net.InetAddress;
import java.util.UUID;

public interface PlayerSession {

    int getId();

    DKBansPlayer getPlayer();

    String getPlayerSessionName();

    InetAddress getIpAddress();

    String getCountry();

    String getRegion();


    String getLastServerName();

    UUID getLastServerId();


    String getProxyName();

    UUID getProxyId();


    String getClientEdition();

    int getClientProtocolVersion();//@Todo maybe use from McNative

   // String getClientLanguage();

    long getConnectTime();

    long getDisconnectTime();

    default long getDuration(){
        return getDisconnectTime()-getConnectTime();
    }

    boolean isActive();

}
