package net.pretronic.dkbans.api.event;

import net.pretronic.dkbans.api.DKBansExecutor;

public interface ChannelBroadcastMessageReceiveEvent extends DKBansEvent {

    String getChannel();

    DKBansExecutor getExecutor();


}
