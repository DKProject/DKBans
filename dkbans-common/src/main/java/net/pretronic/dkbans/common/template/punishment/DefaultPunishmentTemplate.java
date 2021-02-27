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

package net.pretronic.dkbans.common.template.punishment;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.CalculationType;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class DefaultPunishmentTemplate extends DefaultTemplate implements PunishmentTemplate {

    private final Map<Integer, PunishmentTemplateEntry> durations;
    private final int addedPoints;
    private double pointsDivider;

    public DefaultPunishmentTemplate(int id, int inGroupId, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                                     PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data) {
        super(id, inGroupId, name, group, displayName, permission, aliases, historyType, enabled, hidden, category, data);
        Validate.notNull(historyType, "History can't be null");

        Map<Integer, PunishmentTemplateEntry> durations = loadDurations(data.getDocument("durations"));

        this.durations = new TreeMap<>(durations);

        Pair<Integer, Double> points = loadPoints(data.getDocument("points"));

        this.addedPoints = points.getKey();
        this.pointsDivider = points.getValue();
    }

    @Override
    public Map<Integer, PunishmentTemplateEntry> getDurations() {
        return this.durations;
    }

    @Override
    public int getAddedPoints() {
        return this.addedPoints;
    }

    @Override
    public double getPointsDivider() {
        return this.pointsDivider;
    }

    @Override
    public void build(DKBansPlayer player, DKBansExecutor executor, PlayerHistoryEntrySnapshotBuilder builder) {
        PunishmentTemplateEntry entry = getNextEntry(player);
        builder.historyType(getHistoryType())
                .punishmentType(entry.getType())
                .staff(executor)
                .reason(getDisplayName())
                .template(this)
                .points(addedPoints);
        if(entry instanceof DurationAble) {
            builder.duration(((DurationAble)entry).getDuration());
        }
    }

    @Override
    public PunishmentTemplateEntry getNextEntry(DKBansPlayer player) {
        int count = player.getHistory().calculate(getGroup().getCalculationType(), getHistoryType());
        if(getGroup().getCalculationType() == CalculationType.POINTS) {
            count = (int) Math.round(count/pointsDivider);
        }else{
            count++;
        }

        PunishmentTemplateEntry punishmentTemplateEntry = null;
        for (Map.Entry<Integer, PunishmentTemplateEntry> entry : durations.entrySet()) {
            if(entry.getKey() <= count) {
                punishmentTemplateEntry = entry.getValue();
            }
        }
        return punishmentTemplateEntry;
    }

    @Internal
    public void setPointsDivider(double divider) {
        this.pointsDivider = divider;
    }

    @Override
    public PunishmentType getFirstType() {
        return this.durations.values().iterator().next().getType();
    }

    private Map<Integer, PunishmentTemplateEntry> loadDurations(Document data) {
        Map<Integer, PunishmentTemplateEntry> durations = new TreeMap<>(Integer::compare);

        if(data != null) {
            for (DocumentEntry entry0 : data) {
                Pair<Integer, PunishmentTemplateEntry> entry = loadEntry(entry0);
                durations.put(entry.getKey(), entry.getValue());
            }
        }

        return durations;
    }

    private Pair<Integer, Double> loadPoints(Document data) {
        int addedPoints = 0;
        double pointsDivider = 0;

        if(data != null) {
            addedPoints = data.getInt("addedPoints");
            pointsDivider = data.getDouble("pointsDivider");
        }

        return new Pair<>(addedPoints, pointsDivider);
    }

    private Pair<Integer, PunishmentTemplateEntry> loadEntry(DocumentEntry entry) {
        Document entryData = entry.toDocument();

        int id = Convert.toInteger(entryData.getKey());
        PunishmentType type = PunishmentType.getPunishmentType(entryData.getString("type"));

        PunishmentTemplateEntry templateEntry = PunishmentTemplateEntryFactory.create(type, entryData);
        return new Pair<>(id, templateEntry);
    }

    private static Document entryToDocument(DefaultPunishmentTemplate template) {
        Document data = Document.newDocument();
        for (Map.Entry<Integer, PunishmentTemplateEntry> entry : template.durations.entrySet()) {
            data.add(entry.getKey().toString(), PunishmentTemplateEntryFactory.toData(entry.getValue()));
        }
        return data;
    }

    public static class Factory extends TemplateFactory {

        @Override
        public Template create(int id, int inGroupId, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                               PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data) {
            return new DefaultPunishmentTemplate(id, inGroupId, name, group, displayName, permission, aliases, historyType, enabled,
                    hidden, category, data);
        }

        @Override
        public Document createData(Template template0) {
            DefaultPunishmentTemplate template = (DefaultPunishmentTemplate) template0;

            Document data = Document.newDocument();

            data.add("durations", entryToDocument(template));
            data.add("points", Document.newDocument()
                    .add("durations", entryToDocument(template))
                    .add("addedPoints", template.addedPoints)
                    .add("pointsDivider", template.pointsDivider));
            return data;
        }
    }
}
