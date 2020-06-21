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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;

import java.util.Collection;

public class DefaultTemplateCategory implements TemplateCategory {

    private final int id;
    private final String name;
    private final String displayName;

    public DefaultTemplateCategory(int id, String name, String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
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
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public Collection<Template> getTemplates() {
        return DKBans.getInstance().getTemplateManager().getTemplates(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TemplateCategory &&
                ((TemplateCategory)o).getId() == getId();
    }
}
