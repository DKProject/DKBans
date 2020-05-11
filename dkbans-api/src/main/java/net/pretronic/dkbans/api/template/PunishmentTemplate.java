package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.document.Document;

public interface PunishmentTemplate {

    int getId();

    String getName();

    String getDisplayName();

    PlayerHistoryType getHistoryType();

    PunishmentType getPunishmentType();

    boolean isEnabled();

    Document getData();


    PunishmentTemplateCategory getCategory();




}
