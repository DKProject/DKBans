package net.pretronic.dkbans.api.template.punishment.types;

import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;

public interface WarnPunishmentTemplateEntry extends PunishmentTemplateEntry {

    boolean shouldKick();

    PunishmentTemplate getPunishmentTemplate();
}
