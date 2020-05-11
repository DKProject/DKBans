package net.pretronic.dkbans.api.template;

import java.util.Collection;

public interface PunishmentTemplateManager {

    Collection<PunishmentTemplate> getTemplates();

    PunishmentTemplate getTemplate(int id);

    PunishmentTemplate getTemplate(String name);




}
