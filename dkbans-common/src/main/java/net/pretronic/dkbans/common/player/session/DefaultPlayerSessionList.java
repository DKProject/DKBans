package net.pretronic.dkbans.common.player.session;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.api.player.session.PlayerSessionList;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.List;

public class DefaultPlayerSessionList implements PlayerSessionList {

    private final DKBansPlayer player;
    private PlayerSession active;

    public DefaultPlayerSessionList(DKBansPlayer player) {
        this.player = player;
    }

    @Override
    public List<PlayerSession> getAll() {
        return DKBans.getInstance().getStorage().getPlayerSessions(player);
    }

    @Override
    public List<PlayerSession> getLast(int amount) {
        return DKBans.getInstance().getStorage().getLastPlayerSessions(player, amount);
    }

    @Override
    public PlayerSession getLast() {
        List<PlayerSession> last = getLast(1);
        if(!last.isEmpty()) return last.get(0);
        return null;
    }

    @Override
    public List<PlayerSession> getFirst(int amount) {
        return DKBans.getInstance().getStorage().getFirstPlayerSessions(player, amount);
    }

    @Override
    public PlayerSession getByIndex(int index) {
        return DKBans.getInstance().getStorage().getPlayerSessionByIndex(player, index);
    }

    @Override
    public List<PlayerSession> getByIndexRange(int startIndex, int endIndex) {
        return DKBans.getInstance().getStorage().getPlayerSessionByIndexRange(player, startIndex, endIndex);
    }

    @Override
    public List<PlayerSession> getSince(long time) {
        return DKBans.getInstance().getStorage().getSincePlayerSessions(player, time);
    }

    @Override
    public List<PlayerSession> getUntil(long time) {
        return DKBans.getInstance().getStorage().getUntilPlayerSessions(player, time);
    }

    @Override
    public List<PlayerSession> getBetween(long startTime, long endTime) {
        return DKBans.getInstance().getStorage().getBetweenPlayerSessions(player, startTime, endTime);
    }

    @Override
    public PlayerSession getActive() {
        return this.active;
    }

    @Internal
    public void setActive(PlayerSession session) {
        this.active = session;
    }
}
