package net.pretronic.dkbans.api.player;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.document.Document;

import java.util.concurrent.TimeUnit;

public interface PunishmentBuilder {

    PunishmentBuilder type(PunishmentType type);

    PunishmentBuilder historyType(PlayerHistoryType type);

    PunishmentBuilder points(int points);


    PunishmentBuilder reason(String reason);


    PunishmentBuilder duration(long time, TimeUnit unit);

    PunishmentBuilder timeout(long timeout);

    PunishmentBuilder stuff(DKBansPlayer player);

    PunishmentBuilder scope(DKBansScope scope);

    PunishmentBuilder properties(Document document);


    PlayerHistoryEntry execute();
}
