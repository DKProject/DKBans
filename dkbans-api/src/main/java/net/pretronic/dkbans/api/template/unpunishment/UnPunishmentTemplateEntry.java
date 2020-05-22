package net.pretronic.dkbans.api.template.unpunishment;

import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;

import java.util.Map;

public interface UnPunishmentTemplateEntry extends PunishmentTemplateEntry {

    Map<Integer, UnPunishmentTemplateEntryOption> getOptions();
}
