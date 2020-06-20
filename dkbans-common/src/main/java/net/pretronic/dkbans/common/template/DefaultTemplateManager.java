/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.06.20, 17:19
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.pretronic.dkbans.common.template;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.history.CalculationType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntryFactory;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplate;
import net.pretronic.dkbans.common.template.punishment.types.DefaultBanPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.types.DefaultKickPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.types.DefaultMutePunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.types.DefaultWarnPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.report.DefaultReportTemplate;
import net.pretronic.dkbans.common.template.unpunishment.DefaultUnPunishmentTemplate;
import net.pretronic.dkbans.common.template.unpunishment.DefaultUnPunishmentTemplateEntry;
import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultTemplateManager implements TemplateManager {

    private final Collection<TemplateGroup> templateGroups;
    private final Collection<TemplateCategory> templateCategories;

    public DefaultTemplateManager(DKBans dkBans) {
        this.templateGroups = new ArrayList<>();
        this.templateCategories = new ArrayList<>();
        registerDefaultFactories();
    }

    @Override
    public Collection<TemplateGroup> getTemplateGroups() {
        return this.templateGroups;
    }

    @Override
    public TemplateGroup getTemplateGroup(String name) {
        return Iterators.findOne(templateGroups, templateGroup -> templateGroup.getName().equalsIgnoreCase(name));
    }

    @Override
    public TemplateGroup createTemplateGroup(String name, TemplateType templateType, CalculationType calculationType, List<Template> templates) {
        TemplateGroup templateGroup = DKBans.getInstance().getStorage().createTemplateGroup(name, templateType, calculationType);
        this.templateGroups.add(templateGroup);
        return templateGroup;
    }

    @Override
    public Collection<Template> getTemplates() {
        Collection<Template> templates = new ArrayList<>();
        this.templateGroups.forEach(group -> templates.addAll(group.getTemplates()));
        return templates;
    }

    @Override
    public Collection<Template> getTemplates(TemplateCategory category) {
        Collection<Template> templates = new ArrayList<>();
        this.templateGroups.forEach(group -> templates.addAll(Iterators.filter(group.getTemplates(), template -> template.getCategory().equals(category))));
        return templates;
    }

    @Override
    public Template getTemplate(int id) {
        for (TemplateGroup group : this.templateGroups) {
            for (Template template : group.getTemplates()) {
                if(template.getId() == id) return template;
            }
        }
        return null;
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

    @Override
    public TemplateCategory createTemplateCategory(String name, String displayName) {
        TemplateCategory category = DKBans.getInstance().getStorage().createTemplateCategory(name, displayName);
        this.templateCategories.add(category);
        return category;
    }

    private void registerDefaultFactories() {
        TemplateFactory.register(TemplateType.PUNISHMENT, new DefaultPunishmentTemplate.Factory());

        PunishmentTemplateEntryFactory.register(PunishmentType.BAN, new DefaultBanPunishmentTemplateEntry.Factory());
        PunishmentTemplateEntryFactory.register(PunishmentType.KICK, new DefaultKickPunishmentTemplateEntry.Factory());
        PunishmentTemplateEntryFactory.register(PunishmentType.MUTE, new DefaultMutePunishmentTemplateEntry.Factory());
        PunishmentTemplateEntryFactory.register(PunishmentType.WARN, new DefaultWarnPunishmentTemplateEntry.Factory());


        TemplateFactory.register(TemplateType.UNPUNISHMENT, new DefaultUnPunishmentTemplate.Factory());
        UnPunishmentTemplateEntryFactory.setFactory(new DefaultUnPunishmentTemplateEntry.Factory());


        TemplateFactory.register(TemplateType.REPORT, new DefaultReportTemplate.Factory());
    }

    public void initialize() {
        this.templateGroups.addAll(DKBans.getInstance().getStorage().loadTemplateGroups());
        this.templateCategories.addAll(DKBans.getInstance().getStorage().loadTemplateCategories());
    }
}
