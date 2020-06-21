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

package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplate;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public final class TemplateType {

    private static final Collection<TemplateType> REGISTRY = new ArrayList<>();

    public static final TemplateType PUNISHMENT = register("PUNISHMENT", PunishmentTemplate.class);
    public static final TemplateType UNPUNISHMENT = register("UNPUNISHMENT", UnPunishmentTemplate.class);
    public static final TemplateType REPORT = register("REPORT", ReportTemplate.class);


    private final String name;
    private final Class<? extends Template> templateClass;

    private TemplateType(String name, Class<? extends Template> templateClass) {
        this.name = name;
        this.templateClass = templateClass;
    }

    public String getName() {
        return this.name;
    }

    public Class<? extends Template> getTemplateClass() {
        return this.templateClass;
    }


    public static TemplateType register(String name, Class<? extends Template> templateClass) {
        TemplateType templateType = new TemplateType(name, templateClass);
        REGISTRY.add(templateType);
        return templateType;
    }

    public static TemplateType byName(String name) {
        Validate.notNull(name);
        TemplateType templateType = Iterators.findOne(REGISTRY, type -> type.getName().equalsIgnoreCase(name));
        if(templateType == null) throw new IllegalArgumentException("No template type for name " + name + " found");
        return templateType;
    }

    public static TemplateType byNameOrNull(String name) {
        return Iterators.findOne(REGISTRY, type -> type.getName().equalsIgnoreCase(name));
    }
}
