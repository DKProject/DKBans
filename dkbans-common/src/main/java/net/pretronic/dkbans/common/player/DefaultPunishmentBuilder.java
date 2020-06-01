package net.pretronic.dkbans.common.player;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.document.Document;

import java.util.concurrent.TimeUnit;

public class DefaultPunishmentBuilder implements PunishmentBuilder {
    @Override
    public PunishmentBuilder type(PunishmentType type) {
        return null;
    }

    @Override
    public PunishmentBuilder historyType(PlayerHistoryType type) {
        return null;
    }

    @Override
    public PunishmentBuilder points(int points) {
        return null;
    }

    @Override
    public PunishmentBuilder reason(String reason) {
        return null;
    }

    @Override
    public PunishmentBuilder duration(long time, TimeUnit unit) {
        return null;
    }

    @Override
    public PunishmentBuilder timeout(long timeout) {
        return null;
    }

    @Override
    public PunishmentBuilder stuff(DKBansPlayer player) {
        return null;
    }

    @Override
    public PunishmentBuilder scope(DKBansScope scope) {
        return null;
    }

    @Override
    public PunishmentBuilder properties(Document document) {
        return null;
    }

    @Override
    public PlayerHistoryEntry execute() {
        return null;
    }
}
