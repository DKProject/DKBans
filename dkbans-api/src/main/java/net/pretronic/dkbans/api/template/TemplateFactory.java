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

package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class TemplateFactory {

    private static final Map<TemplateType, TemplateFactory> FACTORY = new HashMap<>();


    public abstract Template create(int id, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                                    PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data);

    public abstract Document createData(Template template);


    public static void register(TemplateType templateType, TemplateFactory factory) {
        FACTORY.put(templateType, factory);
    }

    public static Template create(TemplateType templateType, int id, String name, TemplateGroup group, String displayName,
                                  String permission, Collection<String> aliases, PlayerHistoryType historyType,
                                  boolean enabled, boolean hidden, TemplateCategory category, Document data) {

        Validate.notNull(templateType, name, category);
        TemplateFactory factory = FACTORY.get(templateType);
        if(factory == null) throw new IllegalArgumentException("No template factory for template type " + templateType.getName() + " found");

        return factory.create(id, name, group, displayName, permission, aliases, historyType, enabled, hidden, category, data);
    }

    public static Document toData(Template template) {

        Validate.notNull(template);
        TemplateFactory factory = FACTORY.get(template.getGroup().getTemplateType());
        if(factory == null) throw new IllegalArgumentException("No template factory for template type " + template.getGroup().getTemplateType().getName() + " found");

        return factory.createData(template);
    }
}
