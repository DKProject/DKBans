package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;

import java.util.Collection;

public interface Template {

    int getId();

    String getName();

    String getDisplayName();

    String getPermission();

    Collection<String> getAliases();

    PlayerHistoryType getHistoryType();

    PunishmentType getPunishmentType();

    boolean isEnabled();

    boolean isHidden();

    Collection<? extends DKBansScope> getScopes();

    TemplateCategory getCategory();

    Document getData();

    void build(DKBansPlayer player, DKBansExecutor executor,PlayerHistoryEntrySnapshotBuilder builder);

    PunishmentTemplateEntry getNextEntry(DKBansPlayer player);


}
