package net.pretronic.dkbans.api.support;

import net.pretronic.dkbans.api.player.DKBansPlayer;

import java.util.Collection;

public interface SupportTicketManager {

    Collection<SupportTicket> getTickets();

    Collection<SupportTicket> getTickets(TicketState state);

    SupportTicket createTicket(DKBansPlayer player,String question);

}
