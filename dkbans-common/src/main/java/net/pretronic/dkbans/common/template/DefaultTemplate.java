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

import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.Collection;

public class DefaultTemplate implements Template {

    private int id;
    private final int inGroupId;
    private final String name;
    private final TemplateGroup group;
    private final String displayName;
    private final String permission;
    private final Collection<String> aliases;
    private final PlayerHistoryType historyType;
    private final boolean enabled;
    private final boolean hidden;
    private final TemplateCategory category;
    private final Document data;

    public DefaultTemplate(int id, int inGroupId, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases, PlayerHistoryType historyType,
                           boolean enabled, boolean hidden, TemplateCategory category, Document data) {
        Validate.notNull(name, group, displayName, category, data);
        this.id = id;
        this.inGroupId = inGroupId;
        this.name = name;
        this.group = group;
        this.displayName = displayName;
        this.permission = permission;
        this.aliases = aliases;
        this.historyType = historyType;
        this.enabled = enabled;
        this.hidden = hidden;
        this.category = category;
        this.data = data;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getInGroupId() {
        return this.inGroupId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public TemplateGroup getGroup() {
        return this.group;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public Collection<String> getAliases() {
        return this.aliases;
    }

    @Override
    public PlayerHistoryType getHistoryType() {
        return this.historyType;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public TemplateCategory getCategory() {
        return this.category;
    }

    @Override
    public Document getData() {
        return this.data;
    }


    @Internal
    public void setIdInternal(int id) {
        this.id = id;
    }
}
