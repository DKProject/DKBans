package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
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


}
