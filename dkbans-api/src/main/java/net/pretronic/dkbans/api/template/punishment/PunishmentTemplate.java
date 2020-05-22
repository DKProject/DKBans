package net.pretronic.dkbans.api.template.punishment;

import net.pretronic.dkbans.api.template.Template;

import java.util.Map;

public interface PunishmentTemplate extends Template {

    Map<Integer, PunishmentTemplateEntry> getDurations();

    Map<Integer, PunishmentTemplateEntry> getPoints();

    int getAddedPoints();

    double getPointsDivider();
}
