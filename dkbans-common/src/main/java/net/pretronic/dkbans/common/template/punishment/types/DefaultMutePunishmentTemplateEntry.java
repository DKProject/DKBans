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
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.punishment.types.MutePunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.duration.DurationProcessor;

import java.time.Duration;

public class DefaultMutePunishmentTemplateEntry extends DefaultPunishmentTemplateEntry implements MutePunishmentTemplateEntry {

    private final Duration duration;

    public DefaultMutePunishmentTemplateEntry(DKBansScope scope, Duration duration) {
        super(PunishmentType.MUTE, scope);
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }

    public static class Factory extends PunishmentTemplateEntryFactory {

        @Override
        public PunishmentTemplateEntry create(Document data) {
            String rawDuration = data.getString("duration");
            try {
                Duration duration;
                if(rawDuration == null || rawDuration.trim().equals("-1") || rawDuration.trim().equals("0")) duration = Duration.ofMillis(-1);
                else duration = DurationProcessor.getStandard().parse(rawDuration);
                return new DefaultMutePunishmentTemplateEntry(DKBansScope.fromData(data),duration);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(String.format("Can't parse duration (%s) of mute punishment template entry", rawDuration));
            }
        }

        @Override
        public Document createData(PunishmentTemplateEntry entry0) {
            MutePunishmentTemplateEntry entry = (MutePunishmentTemplateEntry) entry0;
            Document data = Document.newDocument()
                    .add("type", entry.getType().getName())
                    .add("duration", DurationProcessor.getStandard().format(entry.getDuration()));
            if(entry.getScope() != null) {
                data.add("scopeType", entry.getScope().getType())
                        .add("scopeName", entry.getScope().getName());
            }
            return data;
        }
    }
}
