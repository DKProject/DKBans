package net.pretronic.dkbans.api.support;

import net.pretronic.dkbans.api.player.DKBansPlayer;

import java.util.Collection;
import java.util.List;

public interface SupportTicket extends Iterable<SupportMessage> {


    String getTopic();

    void setTopic(String topic);


    TicketState getState();

    void setState(TicketState state);


    Collection<SupportParticipant> getParticipants();

    SupportParticipant getParticipant(DKBansPlayer player);

    default SupportParticipant addParticipant(DKBansPlayer player){
        return addParticipant(player,false);
    }

    SupportParticipant addParticipant(DKBansPlayer player, boolean hidden);

    void removeParticipant(DKBansPlayer player);

    boolean isParticipant(DKBansPlayer player);

    boolean isHidden(DKBansPlayer player);


    SupportMessage getLastMessage();

    List<SupportMessage> getMessages();


    SupportMessage send(DKBansPlayer player,String message);


}
