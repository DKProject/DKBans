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

package net.pretronic.dkbans.api.template.punishment;

import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

import java.util.HashMap;
import java.util.Map;

public abstract class PunishmentTemplateEntryFactory {

    private static final Map<PunishmentType, PunishmentTemplateEntryFactory> FACTORY = new HashMap<>();


    public abstract PunishmentTemplateEntry create(Document data);

    public abstract Document createData(PunishmentTemplateEntry entry);


    public static PunishmentTemplateEntry create(PunishmentType punishmentType, Document data) {
        Validate.notNull(punishmentType, data);
        PunishmentTemplateEntryFactory factory = FACTORY.get(punishmentType);
        if(factory == null) throw new IllegalArgumentException("No punishment entry factory for punishment type " + punishmentType.getName() + " found");
        return factory.create(data);
    }

    public static Document toData(PunishmentTemplateEntry entry) {
        Validate.notNull(entry);
        PunishmentTemplateEntryFactory factory = FACTORY.get(entry.getType());
        if(factory == null) throw new IllegalArgumentException("No punishment entry factory for punishment type " + entry.getType().getName() + " found");
        return factory.createData(entry);
    }

    public static void register(PunishmentType punishmentType, PunishmentTemplateEntryFactory factory) {
        Validate.notNull(factory);
        FACTORY.put(punishmentType, factory);
    }
}
