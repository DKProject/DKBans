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

package net.pretronic.dkbans.common.template.unpunishment;

import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntryOption;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;

import java.util.HashMap;
import java.util.Map;

public class DefaultUnPunishmentTemplateEntry implements UnPunishmentTemplateEntry {

    private final Map<PunishmentType, UnPunishmentTemplateEntryOption> options;

    public DefaultUnPunishmentTemplateEntry(Map<PunishmentType, UnPunishmentTemplateEntryOption> options) {
        this.options = options;
    }

    @Override
    public Map<PunishmentType, UnPunishmentTemplateEntryOption> getOptions() {
        return this.options;
    }

    private static Document entryOptionToDocument(UnPunishmentTemplateEntryOption option) {
        Document data = Document.newDocument();
        data.add("removeTime", option.getRemoveTime());
        data.add("divider", option.getDivider());
        option.getProperties().forEach(data::add);
        return data;
    }

    public static class Factory extends UnPunishmentTemplateEntryFactory {

        @Override
        public UnPunishmentTemplateEntry create(Document data0) {
            Map<PunishmentType, UnPunishmentTemplateEntryOption> options = new HashMap<>();

            for (DocumentEntry data1 : data0) {

                String punishmentType0 = data1.getKey();
                PunishmentType punishmentType = PunishmentType.getPunishmentType(punishmentType0);

                Map<String, Object> properties = new HashMap<>();
                long removeTime = 0;
                double divider = 0;
                for (DocumentEntry entry : data1.toDocument()) {
                    if(entry.getKey().equalsIgnoreCase("removeTime")) {
                        removeTime = entry.toPrimitive().getAsLong();
                    } else if(entry.getKey().equalsIgnoreCase("divider")) {
                        divider = entry.toPrimitive().getAsDouble();
                    } else {
                        properties.put(entry.getKey(), entry.toPrimitive().getAsObject());
                    }
                }
                options.put(punishmentType, new DefaultUnPunishmentTemplateEntryOption(removeTime, divider, properties));
            }
            return new DefaultUnPunishmentTemplateEntry(options);
        }

        @Override
        public Document createData(UnPunishmentTemplateEntry entry) {
            Document data = Document.newDocument();
            entry.getOptions().forEach((punishmentType, entryOption) -> data.add(punishmentType.getName(), entryOptionToDocument(entryOption)));
            return data;
        }
    }
}
