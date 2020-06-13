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

package net.pretronic.dkbans.common.template.punishment.types;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.punishment.types.KickPunishmentTemplateEntry;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;

public class DefaultKickPunishmentTemplateEntry extends DefaultPunishmentTemplateEntry implements KickPunishmentTemplateEntry {

    public DefaultKickPunishmentTemplateEntry(DKBansScope scope) {
        super(PunishmentType.KICK, scope);
    }

    public static class Factory extends PunishmentTemplateEntryFactory {

        @Override
        public PunishmentTemplateEntry create(Document data) {
            return new DefaultKickPunishmentTemplateEntry(DefaultDKBansScope.fromData(data));
        }

        @Override
        public Document createData(PunishmentTemplateEntry entry) {
            return Document.newDocument()
                    .add("type", entry.getType().getName())
                    .add("scopeType", entry.getScope().getType())
                    .add("scopeName", entry.getScope().getName());
        }
    }
}
