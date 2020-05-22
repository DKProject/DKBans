package net.pretronic.dkbans.api.template.punishment.types;

import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;

public interface WarnPunishmentTemplateEntry extends PunishmentTemplateEntry {

    boolean shouldKick();

    PunishmentTemplate getPunishmentTemplate();
}
