package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.player.history.CalculationType;

import java.util.Collection;
import java.util.List;

public interface TemplateManager {

    Collection<TemplateGroup> getTemplateGroups();

    TemplateGroup getTemplateGroup(String name);

    void createTemplateGroup(String name, TemplateType templateType, CalculationType calculationType, List<Template> templates);


    Collection<Template> getTemplates();

    Collection<Template> getTemplates(TemplateCategory category);

    Template getTemplate(int id);


    Collection<TemplateCategory> getTemplateCategories();

    TemplateCategory getTemplateCategory(int id);

    TemplateCategory getTemplateCategory(String name);

}
