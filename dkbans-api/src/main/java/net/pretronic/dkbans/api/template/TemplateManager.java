package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.player.history.PunishmentType;

import java.util.Collection;

public interface TemplateManager {

    Collection<Template> getTemplates();

    Collection<Template> getTemplates(PunishmentType type);

    <T extends Template> Collection<T> getTemplates(PunishmentType type, Class<T> templateClass);

    Collection<Template> getTemplates(TemplateCategory category);

    Template getTemplate(int id);

    Template getTemplate(String name);


    void addTemplate(Template template);

    boolean removeTemplate(Template template);


    Collection<TemplateCategory> getTemplateCategories();

    TemplateCategory getTemplateCategory(int id);
}
