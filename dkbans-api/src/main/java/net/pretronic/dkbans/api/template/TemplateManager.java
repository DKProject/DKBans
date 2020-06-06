package net.pretronic.dkbans.api.template;

import java.util.Collection;
import java.util.List;

public interface TemplateManager {

    Collection<TemplateGroup> getTemplateGroups();

    TemplateGroup getTemplateGroup(String name);

    void createTemplateGroup(String name, TemplateType type, List<Template> templates);


    Collection<Template> getTemplates();

    Template getTemplate(int id);


    Collection<TemplateCategory> getTemplateCategories();

    TemplateCategory getTemplateCategory(int id);

    TemplateCategory getTemplateCategory(String name);

}
