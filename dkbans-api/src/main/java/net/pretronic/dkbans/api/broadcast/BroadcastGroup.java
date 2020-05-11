package net.pretronic.dkbans.api.broadcast;

import net.pretronic.dkbans.api.DKBansScope;

import java.util.Collection;
import java.util.List;

public interface BroadcastGroup extends Iterable<BroadcastAssignment>{

    int getId();

    String getName();

    void setName(String name);


    boolean isEnabled();

    void setEnabled(boolean enabled);


    String getPermission();

    void setPermission(String permission);


    BroadcastOrder getOrder();

    void setOrder(BroadcastOrder order);


    long getInterval();

    void setInterval(long interval);


    DKBansScope getScope();

    void setScope(DKBansScope scope);


    Collection<BroadcastAssignment> getAssignments();

    BroadcastAssignment getAssignment(Broadcast broadcast);


    List<Broadcast> getBroadcasts();

    default BroadcastAssignment addBroadcast(Broadcast broadcast){
        return addBroadcast(broadcast,getBroadcasts().size()+1);
    }

    BroadcastAssignment addBroadcast(Broadcast broadcast, int position);

    void removeBroadcast(Broadcast broadcast);



    BroadcastAssignment getNext(int position);

    BroadcastAssignment getNext(BroadcastAssignment current);


    void delete();


}
