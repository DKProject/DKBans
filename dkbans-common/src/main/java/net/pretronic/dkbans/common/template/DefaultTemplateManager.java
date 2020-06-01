package net.pretronic.dkbans.common.template;

import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplate;
import net.pretronic.dkbans.common.template.punishment.types.DefaultBanPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.types.DefaultKickPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.types.DefaultMutePunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.types.DefaultWarnPunishmentTemplateEntry;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultTemplateManager implements TemplateManager {

    private final Collection<Template> templates;
    private final Collection<TemplateCategory> templateCategories;

    public DefaultTemplateManager() {
        this.templates = new ArrayList<>();
        this.templateCategories = new ArrayList<>();
        registerDefaultFactories();
    }

    @Override
    public Collection<Template> getTemplates() {
        return this.templates;
    }

    @Override
    public Collection<Template> getTemplates(PunishmentType type) {
        return Iterators.filter(this.templates, template -> template.getPunishmentType().equals(type));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Template> Collection<T> getTemplates(PunishmentType type, Class<T> templateClass) {
        Collection<T> templates = new ArrayList<>();
        for (Template template : this.templates) {
            if(template.getPunishmentType().equals(type)) {
                templates.add((T) template);
            }
        }
        return templates;
    }

    @Override
    public Collection<Template> getTemplates(TemplateCategory category) {
        return Iterators.filter(this.templates, template -> template.getCategory().equals(category));
    }

    @Override
    public Template getTemplate(int id) {
        return Iterators.findOne(this.templates, template -> template.getId() == id);
    }

    @Override
    public Template getTemplate(String name) {
        return Iterators.findOne(this.templates, template -> template.getName().equalsIgnoreCase(name));
    }

    @Override
    public void addTemplate(Template template) {
        this.templates.add(template);
    }

    @Override
    public boolean removeTemplate(Template template) {
        return this.templates.remove(template);
    }

    @Override
    public Collection<TemplateCategory> getTemplateCategories() {
        return this.templateCategories;
    }

    @Override
    public TemplateCategory getTemplateCategory(int id) {
        return Iterators.findOne(this.templateCategories, category -> category.getId() == id);
    }

    @Override
    public TemplateCategory getTemplateCategory(String name) {
        return Iterators.findOne(this.templateCategories, category -> category.getName().equalsIgnoreCase(name));
    }

    private void registerDefaultFactories() {
        TemplateFactory.register(TemplateType.PUNISHMENT, new DefaultPunishmentTemplate.Factory());

        PunishmentTemplateEntryFactory.register(PunishmentType.BAN, new DefaultBanPunishmentTemplateEntry.Factory());
        PunishmentTemplateEntryFactory.register(PunishmentType.KICK, new DefaultKickPunishmentTemplateEntry.Factory());
        PunishmentTemplateEntryFactory.register(PunishmentType.MUTE, new DefaultMutePunishmentTemplateEntry.Factory());
        PunishmentTemplateEntryFactory.register(PunishmentType.WARN, new DefaultWarnPunishmentTemplateEntry.Factory());
    }
}
