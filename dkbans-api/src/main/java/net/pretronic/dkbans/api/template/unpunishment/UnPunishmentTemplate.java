package net.pretronic.dkbans.api.template.unpunishment;

import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;

import java.util.Collection;
import java.util.Map;

public interface UnPunishmentTemplate extends Template {

    Collection<PunishmentTemplate> getBlacklistedTemplates();

    Map<Integer, UnPunishmentTemplateEntry> getDurations();

    Map<Integer, UnPunishmentTemplateEntry> getPoints();

    int getRemovedPoints();

    double getPointsDivider();
}
