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

package net.pretronic.dkbans.common.template.punishment;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;

public class DefaultPunishmentTemplateEntry implements PunishmentTemplateEntry {

    private final PunishmentType type;
    private final DKBansScope scope;

    public DefaultPunishmentTemplateEntry(PunishmentType type, DKBansScope scope) {
        this.type = type;
        this.scope = scope;
    }

    @Override
    public PunishmentType getType() {
        return this.type;
    }

    @Override
    public DKBansScope getScope() {
        return this.scope;
    }
}
