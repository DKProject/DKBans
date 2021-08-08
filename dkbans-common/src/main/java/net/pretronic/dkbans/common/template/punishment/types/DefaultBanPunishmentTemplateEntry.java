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
import net.pretronic.dkbans.api.template.punishment.types.BanPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.duration.DurationProcessor;

import java.time.Duration;

public class DefaultBanPunishmentTemplateEntry extends DefaultPunishmentTemplateEntry implements BanPunishmentTemplateEntry {

    private final Duration duration;

    public DefaultBanPunishmentTemplateEntry(DKBansScope scope, Duration duration) {
        super(PunishmentType.BAN, scope);
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
                if(rawDuration == null || rawDuration.trim().equals("-1") || rawDuration.trim().equals("-1.0")
                        || rawDuration.trim().equals("0") || rawDuration.trim().equals("0.0")) {
                    duration = Duration.ofMillis(-1);
                } else {
                    duration = DurationProcessor.getStandard().parse(rawDuration);
                }
                System.out.println("loaded SCOPE: "+data.getString("scope")+" "+DKBansScope.parse(data.getString("scope")));
                return new DefaultBanPunishmentTemplateEntry(DKBansScope.parse(data.getString("scope")),duration);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(String.format("Can't parse duration (%s) of ban punishment template entry", rawDuration));
            }
        }

        @Override
        public Document createData(PunishmentTemplateEntry entry0) {
            BanPunishmentTemplateEntry entry = (BanPunishmentTemplateEntry) entry0;

            Document data = Document.newDocument()
                    .add("type", entry.getType().getName())
                    .add("duration", DurationProcessor.getStandard().formatShort(entry.getDuration()));
            if(entry.getScope() != null) data.set("scope",entry.getScope().toString());
            return data;
        }
    }
}
