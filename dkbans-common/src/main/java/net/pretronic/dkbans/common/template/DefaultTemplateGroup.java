/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 17:26
 * @web %web%
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

import net.pretronic.dkbans.api.player.history.CalculationType;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultTemplateGroup implements TemplateGroup {

    private int id;
    private final String name;
    private final TemplateType templateType;
    private final CalculationType calculationType;
    private final List<Template> templates;

    public DefaultTemplateGroup(int id, String name, TemplateType templateType, CalculationType calculationType, List<Template> templates) {
        this.id = id;
        this.name = name;
        this.templateType = templateType;
        this.calculationType = calculationType;
        this.templates = templates;
    }

    public DefaultTemplateGroup(int id, String name, TemplateType templateType, CalculationType calculationType) {
        this(id, name, templateType, calculationType, new ArrayList<>());
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public TemplateType getTemplateType() {
        return this.templateType;
    }

    @Override
    public CalculationType getCalculationType() {
        return this.calculationType;
    }

    @Override
    public List<TemplateCategory> getCategories() {
        List<TemplateCategory> categories = new ArrayList<>();
        for (Template template : templates) {
            if(!categories.contains(template.getCategory())){
                categories.add(template.getCategory());
            }
        }
        return categories;
    }

    @Override
    public List<Template> getTemplates() {
        return this.templates;
    }

    @Override
    public boolean removeTemplate(Template template) {
        Validate.notNull(template);
        boolean removed = this.templates.remove(template);
        if(removed) {
            DefaultDKBans.getInstance().getStorage().getTemplate().delete()
                    .where("Id", template.getId())
                    .execute();
            DefaultDKBans.getInstance().getTemplateManager().sendUpdate();
        }
        return removed;
    }

    @Override
    public Iterator<Template> iterator() {
        return this.templates.iterator();
    }


    @Internal
    public void addTemplateInternal(Template template) {
        this.templates.add(template);
    }

    @Internal
    public void addTemplatesInternal(List<Template> templates) {
        this.templates.addAll(templates);
    }

    @Internal
    public void setIdInternal(int id) {
        this.id = id;
    }
}
