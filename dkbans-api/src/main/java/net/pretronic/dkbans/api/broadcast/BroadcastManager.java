package net.pretronic.dkbans.api.broadcast;

import java.util.Collection;

public interface BroadcastManager {

    Collection<Broadcast> getBroadcasts();

    Broadcast getBroadcast(int id);

    Broadcast getBroadcast(String name);

    Broadcast createBroadcast(String name, String text,BroadcastVisibility visibility);

    void deleteBroadcast(int id);


    Collection<BroadcastGroup> getGroups();

    BroadcastGroup getGroup(int id);

    BroadcastGroup getGroup(String name);

    BroadcastGroup createGroup(String name, int interval);

    void deleteGroup(int id);


}
