package net.pretronic.dkbans.api.template;

import java.util.Collection;

public interface TemplateCategory {

    int getId();

    String getName();

    String getDisplayName();


    Collection<Template> getTemplates();
}
