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

import net.pretronic.dkbans.api.player.history.CalculationType;
import net.pretronic.libraries.utility.Iterators;

import java.util.List;

public interface TemplateGroup extends Iterable<Template> {

    int getId();

    String getName();

    TemplateType getTemplateType();

    CalculationType getCalculationType();

    List<Template> getTemplates();

    void addTemplate(Template template);

    void addTemplates(List<Template> templates);

    default Template getTemplate(String name){
        return Iterators.findOne(getTemplates(), template -> template.hasName(name));
    }

}
