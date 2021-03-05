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

package net.pretronic.dkbans.common.template.report;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateFactory;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultReportTemplate extends DefaultTemplate implements ReportTemplate {

    private PunishmentTemplate targetTemplate;
    private final Map<String, Object> properties;

    private String targetTemplateGroupName;
    private String targetTemplateName;
    private int targetTemplateId;

    public DefaultReportTemplate(int id, int inGroupId, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                                 PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data) {
        super(id, inGroupId, name, group, displayName, permission, aliases, historyType, enabled, hidden, category, data);

        this.properties = load(data);
    }

    @Override
    public String getWatchPermission() {
        return getData().getString("watchPermission");
    }

    @Override
    public PunishmentTemplate getTargetTemplate() {
        if(targetTemplate == null && targetTemplateGroupName != null && (targetTemplateName != null || targetTemplateId != 0)) {
            TemplateGroup templateGroup = DKBans.getInstance().getTemplateManager().getTemplateGroup(targetTemplateGroupName);
            if(templateGroup == null) {
                return null;
            }
            Template template;
            if(targetTemplateName != null) template = templateGroup.getTemplate(targetTemplateName);
            else template = templateGroup.getTemplate(targetTemplateId);
            if(!(template instanceof PunishmentTemplate)) {
                return null;
            }
            this.targetTemplate = (PunishmentTemplate) template;
        }
        return this.targetTemplate;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    private Map<String, Object> load(Document data) {
        if(data.contains("targetPunishment")) {
            String targetTemplate = data.getString("targetPunishment");
            String[] splitted = targetTemplate.split("@");

            if(splitted.length != 2) throw new IllegalArgumentException("Wrong targetPunishment length. Invalid format. Use TemplateGroup@TemplateName");

            this.targetTemplateGroupName = splitted[0];
            this.targetTemplateName = splitted[1];


            Map<String, Object> properties = new HashMap<>();
            for (DocumentEntry entry : data) {
                if(entry.getKey().equalsIgnoreCase("targetPunishment")) {
                    properties.put(entry.getKey(), entry.toPrimitive().getAsObject());
                }
            }
            return properties;
        }
        return new HashMap<>();
    }

    public void setTargetTemplateGroupName(String targetTemplateGroupName) {
        this.targetTemplateGroupName = targetTemplateGroupName;
    }

    public void setTargetTemplateName(String targetTemplateName) {
        this.targetTemplateName = targetTemplateName;
    }

    public void setTargetTemplateId(int targetTemplateId) {
        this.targetTemplateId = targetTemplateId;
    }

    public static class Factory extends TemplateFactory {

        @Override
        public Template create(int id, int inGroupId, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                               PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data) {
            return new DefaultReportTemplate(id, inGroupId, name, group, displayName, permission, aliases, historyType, enabled, hidden, category, data);
        }

        @Override
        public Document createData(Template template0) {
            ReportTemplate template = (ReportTemplate)template0;
            Document data = Document.newDocument();

            if(template.getTargetTemplate() != null) {
                data.set("targetPunishment", template.getTargetTemplate().getGroup().getName() + "@" + template.getTargetTemplate().getName());
            }

            template.getProperties().forEach(data::set);
            return data;
        }
    }
}
