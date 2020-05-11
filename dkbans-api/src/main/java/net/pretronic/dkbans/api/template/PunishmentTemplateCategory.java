package net.pretronic.dkbans.api.template;

import java.util.Collection;

public interface PunishmentTemplateCategory {

    int getId();

    String getName();

    void setName(String name);


    String getDisplayName();


    void setDisplayName(String name);


    Collection<PunishmentTemplate> getTemplates();


}
