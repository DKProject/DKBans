package net.pretronic.dkbans.api.broadcast;

import net.pretronic.dkbans.api.DKBansExecutor;

public interface Broadcast {

    int getId();

    String getName();

    void setName(String name);


    BroadcastVisibility getVisibility();

    void setVisibility(BroadcastVisibility visibility);


    String getText();

    void setText(String text);


    void send();

    void send(Iterable<DKBansExecutor> executors);

    void delete();

}
