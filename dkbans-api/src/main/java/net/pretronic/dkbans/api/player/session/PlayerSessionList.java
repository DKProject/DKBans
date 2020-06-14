package net.pretronic.dkbans.api.player.session;

import net.pretronic.dkbans.api.Pagination;

public interface PlayerSessionList extends Pagination<PlayerSession> {

    PlayerSession getActive();
}
