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

package net.pretronic.dkbans.common.template.punishment.types;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.punishment.types.WarnPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;

public class DefaultWarnPunishmentTemplateEntry extends DefaultPunishmentTemplateEntry implements WarnPunishmentTemplateEntry {

    private final boolean shouldKick;
    private final String punishmentTemplateGroupName;
    private final String punishmentTemplateName;
    private final int punishmentTemplateId;
    private PunishmentTemplate punishmentTemplate;

    public DefaultWarnPunishmentTemplateEntry(DKBansScope scope, boolean shouldKick, String punishmentTemplateGroupName, String punishmentTemplateName, int punishmentTemplateId) {
        super(PunishmentType.WARN, scope);
        this.shouldKick = shouldKick;
        this.punishmentTemplateGroupName = punishmentTemplateGroupName;
        this.punishmentTemplateName = punishmentTemplateName;
        this.punishmentTemplateId = punishmentTemplateId;
    }

    @Override
    public boolean shouldKick() {
        return this.shouldKick;
    }

    @Override
    public PunishmentTemplate getPunishmentTemplate() {
        if(punishmentTemplate == null && punishmentTemplateGroupName != null && (punishmentTemplateName != null || punishmentTemplateId != 0)) {
            TemplateGroup templateGroup = DKBans.getInstance().getTemplateManager().getTemplateGroup(punishmentTemplateGroupName);
            if(templateGroup == null) {
                return null;
            }
            Template template;
            if(punishmentTemplateName != null) template = templateGroup.getTemplate(punishmentTemplateName);
            else template = templateGroup.getTemplate(punishmentTemplateId);
            if(!(template instanceof PunishmentTemplate)) {
                return null;
            }
            this.punishmentTemplate = (PunishmentTemplate) template;
        }
        return this.punishmentTemplate;
    }

    public static class Factory extends PunishmentTemplateEntryFactory {

        @Override
        public PunishmentTemplateEntry create(Document data) {
            String groupName = null;
            String templateName = null;
            if(data.contains("targetPunishment")) {
                String[] splitted = data.getString("targetPunishment").split("@");

                groupName = splitted[0];
                templateName = splitted[1];
            }
            return new DefaultWarnPunishmentTemplateEntry(DKBansScope.fromData(data), data.getBoolean("kick"), groupName, templateName, 0);
        }

        @Override
        public Document createData(PunishmentTemplateEntry entry0) {
            WarnPunishmentTemplateEntry entry = (WarnPunishmentTemplateEntry) entry0;
            Document data = Document.newDocument()
                    .add("type", entry.getType().getName())
                    .add("kick", entry.shouldKick());
            if(entry.getPunishmentTemplate() != null) {
                data.add("targetPunishment", entry.getPunishmentTemplate().getGroup().getName() + "@" + entry.getPunishmentTemplate().getName());
            }
            if(entry.getScope() != null) {
                data.add("scopeType", entry.getScope().getType())
                        .add("scopeName", entry.getScope().getName());
            }
            return data;
        }
    }
}
