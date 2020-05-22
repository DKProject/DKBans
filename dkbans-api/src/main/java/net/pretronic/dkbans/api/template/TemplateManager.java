package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.document.Document;

import java.util.Collection;
import java.util.function.Function;

public interface TemplateManager {

    Collection<Template> getTemplates();

    Collection<Template> getTemplates(PunishmentType type);

    <T extends Template> Collection<T> getTemplates(PunishmentType type, Class<T> templateClass);

    Collection<Template> getTemplates(TemplateCategory category);

    Template getTemplate(int id);

    Template getTemplate(String name);


    void addTemplate(Template template);

    boolean removeTemplate(Template template);


    Function<Template, Template> getTemplateTransformer(PunishmentType type);

    void registerTemplateTransformer(PunishmentType type, Function<Template, Template> transformer);

    void loadTemplates(Document file);
}
