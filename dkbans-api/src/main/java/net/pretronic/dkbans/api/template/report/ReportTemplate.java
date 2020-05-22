package net.pretronic.dkbans.api.template.report;

import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;

public interface ReportTemplate extends Template {

    PunishmentTemplate getTargetTemplate();
}
