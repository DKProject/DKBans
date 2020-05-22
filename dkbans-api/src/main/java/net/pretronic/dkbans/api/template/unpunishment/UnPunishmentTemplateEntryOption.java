package net.pretronic.dkbans.api.template.unpunishment;

import net.pretronic.dkbans.api.player.history.PunishmentType;

import java.util.Map;

public interface UnPunishmentTemplateEntryOption {

    PunishmentType getType();

    long getRemoveTime();

    double getDivider();

    Map<String, Object> getProperties();
}
