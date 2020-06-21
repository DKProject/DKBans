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

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.punishment.types.WarnPunishmentTemplateEntry;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;

public class DefaultWarnPunishmentTemplateEntry extends DefaultPunishmentTemplateEntry implements WarnPunishmentTemplateEntry {

    private final boolean shouldKick;
    private final PunishmentTemplate punishmentTemplate;

    public DefaultWarnPunishmentTemplateEntry(DKBansScope scope, boolean shouldKick, PunishmentTemplate punishmentTemplate) {
        super(PunishmentType.WARN, scope);
        this.shouldKick = shouldKick;
        this.punishmentTemplate = punishmentTemplate;
    }

    @Override
    public boolean shouldKick() {
        return this.shouldKick;
    }

    @Override
    public PunishmentTemplate getPunishmentTemplate() {
        return this.punishmentTemplate;
    }

    public static class Factory extends PunishmentTemplateEntryFactory {

        @Override
        public PunishmentTemplateEntry create(Document data) {
            //@Todo link to ban template
            PunishmentTemplate template = null;
            if(data.contains("targetPunishment")) {
                String targetPunishment = data.getString("targetPunishment");

                String templateGroup = null;
                String templateName = null;
                if(targetPunishment.contains("@")) {
                    String[] splitted = targetPunishment.split("@");
                }
            }
            return new DefaultWarnPunishmentTemplateEntry(DefaultDKBansScope.fromData(data), data.getBoolean("kick"), null);
        }

        @Override
        public Document createData(PunishmentTemplateEntry entry0) {
            WarnPunishmentTemplateEntry entry = (WarnPunishmentTemplateEntry) entry0;
            Document data = Document.newDocument()
                    .add("type", entry.getType().getName())
                    .add("kick", entry.shouldKick());//@Todo add link to punishment template
            if(entry.getScope() != null) {
                data.add("scopeType", entry.getScope().getType())
                        .add("scopeName", entry.getScope().getName());
            }
            return data;
        }
    }
}
