package net.pretronic.dkbans.api.broadcast;

public interface BroadcastAssignment {

    int getId();

    Broadcast getBroadcast();

    int getPosition();

    void setPosition(int position);

}
