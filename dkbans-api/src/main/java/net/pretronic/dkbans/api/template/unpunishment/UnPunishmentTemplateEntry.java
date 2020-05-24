package net.pretronic.dkbans.api.template.unpunishment;

import java.util.Map;

public interface UnPunishmentTemplateEntry {

    Map<Integer, UnPunishmentTemplateEntryOption> getOptions();
}
