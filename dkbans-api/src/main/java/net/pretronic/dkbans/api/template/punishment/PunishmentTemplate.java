package net.pretronic.dkbans.api.template.punishment;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.template.Template;

import java.util.Map;

public interface PunishmentTemplate extends Template {

    Map<Integer, PunishmentTemplateEntry> getDurations();

    Map<Integer, PunishmentTemplateEntry> getPoints();

    int getAddedPoints();

    double getPointsDivider();

    void build(DKBansPlayer player, DKBansExecutor executor, PlayerHistoryEntrySnapshotBuilder builder);

    PunishmentTemplateEntry getNextEntry(DKBansPlayer player);
}
