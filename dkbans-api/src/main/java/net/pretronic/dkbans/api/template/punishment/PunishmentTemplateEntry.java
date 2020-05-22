package net.pretronic.dkbans.api.template.punishment;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;

public interface PunishmentTemplateEntry {

    PunishmentType getType();

    DKBansScope getScope();
}
