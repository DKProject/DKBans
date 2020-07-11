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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateFactory;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplate;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntryFactory;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.document.entry.PrimitiveEntry;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.libraries.utility.map.Triple;

import java.util.*;

public class DefaultUnPunishmentTemplate extends DefaultTemplate implements UnPunishmentTemplate {

    private final Collection<BlacklistedTemplate> blacklistedTemplates;
    private final Collection<? extends DKBansScope> scopes;

    private final Map<Integer, UnPunishmentTemplateEntry> durations;

    private final Map<Integer, UnPunishmentTemplateEntry> points;
    private final int removedPoints;
    private final double pointsDivider;

    public DefaultUnPunishmentTemplate(int id, int inGroupId, String name, TemplateGroup group, String displayName, String permission,
                                       Collection<String> aliases, PlayerHistoryType historyType, boolean enabled, boolean hidden,
                                       TemplateCategory category, Document data) {
        super(id, inGroupId, name, group, displayName, permission, aliases, historyType, enabled, hidden, category, data);

        this.blacklistedTemplates = loadBlacklistedTemplates(data.getDocument("blacklisted"));
        this.scopes = loadScopes(data.getDocument("scopes"));
        this.durations = loadDurations(data.getDocument("durations"));

        Triple<Map<Integer, UnPunishmentTemplateEntry>, Integer, Double> points = loadPoints(data.getDocument("points"));
        this.points = points.getFirst();
        this.removedPoints = points.getSecond();
        this.pointsDivider = points.getThird();
    }

    @Override
    public Collection<PunishmentTemplate> getBlacklistedTemplates() {
        return Iterators.map(this.blacklistedTemplates, BlacklistedTemplate::getTemplate);
    }

    @Override
    public Collection<? extends DKBansScope> getScopes() {
        return this.scopes;
    }

    @Override
    public Map<Integer, UnPunishmentTemplateEntry> getDurations() {
        return this.durations;
    }

    @Override
    public Map<Integer, UnPunishmentTemplateEntry> getPoints() {
        return this.points;
    }

    @Override
    public int getRemovedPoints() {
        return this.removedPoints;
    }

    @Override
    public double getPointsDivider() {
        return this.pointsDivider;
    }

    @Internal
    public void addBlacklistedTemplate(BlacklistedTemplate blacklistedTemplate) {
        this.blacklistedTemplates.add(blacklistedTemplate);
    }


    private Collection<BlacklistedTemplate> loadBlacklistedTemplates(Document data) {
        if(data != null) {
            for (DocumentEntry blacklisted : data) {
                String[] splitted = blacklisted.toPrimitive().getAsString().split("@");

                if(splitted.length != 2) throw new IllegalArgumentException("Wrong blacklisted un punishment length. Invalid format. Use TemplateGroup@TemplateName");

                this.blacklistedTemplates.add(new BlacklistedTemplate(splitted[0], splitted[1], 0));
            }
        }
        return new ArrayList<>();
    }

    private Map<Integer, UnPunishmentTemplateEntry> loadDurations(Document data) {
        if(data != null) {
            Map<Integer, UnPunishmentTemplateEntry> durations = new TreeMap<>(Integer::compare);

            for (DocumentEntry entry0 : data) {
                Pair<Integer, UnPunishmentTemplateEntry> entry = loadEntry(entry0);
                durations.put(entry.getKey(), entry.getValue());
            }
        }

        return durations;
    }

    private Triple<Map<Integer, UnPunishmentTemplateEntry>, Integer, Double> loadPoints(Document data) {
        int addedPoints = data.getInt("removedPoints");
        double pointsDivider = data.getDouble("pointsDivider");
        Map<Integer, UnPunishmentTemplateEntry> points = new HashMap<>();

        for (DocumentEntry entry0 : data.getDocument("durations")) {
            Pair<Integer, UnPunishmentTemplateEntry> entry = loadEntry(entry0);
            points.put(entry.getKey(), entry.getValue());
        }

        return new Triple<>(points, addedPoints, pointsDivider);
    }

    private Pair<Integer, UnPunishmentTemplateEntry> loadEntry(DocumentEntry entry) {
        Document entryData = entry.toDocument();

        int id = Convert.toInteger(entryData.getKey());

        UnPunishmentTemplateEntry templateEntry = UnPunishmentTemplateEntryFactory.createEntry(entryData);
        return new Pair<>(id, templateEntry);
    }

    private Collection<? extends DKBansScope> loadScopes(Document data) {
        Collection<DKBansScope> scopes = new ArrayList<>();
        for (DocumentEntry scope0 : data) {
            Document scope = scope0.toDocument();
            if(scope.size() == 1) {
                PrimitiveEntry firstEntry = scope.getEntry(0).toPrimitive();
                scopes.add(new DKBansScope(firstEntry.getKey(), firstEntry.getAsString(), null));
            } else {
                scopes.add(scope.getAsObject(DKBansScope.class));
            }
        }
        return scopes;
    }

    private static Document entryToDocument(DefaultUnPunishmentTemplate template) {
        Document data = Document.newDocument();
        for (Map.Entry<Integer, UnPunishmentTemplateEntry> entry : template.durations.entrySet()) {
            data.add(entry.getKey().toString(), UnPunishmentTemplateEntryFactory.toData(entry.getValue()));
        }
        return data;
    }

    public static class Factory extends TemplateFactory {

        @Override
        public Template create(int id, int inGroupId, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                               PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data) {
            return new DefaultUnPunishmentTemplate(id, inGroupId, name, group, displayName, permission, aliases, historyType, enabled,
                    hidden, category, data);
        }

        @Override
        public Document createData(Template template0) {
            DefaultUnPunishmentTemplate template = (DefaultUnPunishmentTemplate) template0;

            Document data = Document.newDocument();

            data.add("scopes", template.getScopes());
            data.add("durations", entryToDocument(template));
            data.add("points", Document.newDocument()
                    .add("durations", entryToDocument(template))
                    .add("removedPoints", template.removedPoints)
                    .add("pointsDivider", template.pointsDivider));
            return data;
        }
    }

    @Internal
    public static class BlacklistedTemplate {

        private final String templateGroupName;
        private final String templateName;
        private final int templateId;
        private PunishmentTemplate template;

        public BlacklistedTemplate(String templateGroupName, String templateName, int templateId) {
            this.templateGroupName = templateGroupName;
            this.templateName = templateName;
            this.templateId = templateId;
        }

        public PunishmentTemplate getTemplate() {
            if(template == null && templateGroupName != null && (templateName != null || templateId != 0)) {
                TemplateGroup templateGroup = DKBans.getInstance().getTemplateManager().getTemplateGroup(templateGroupName);
                if(templateGroup == null) {
                    return null;
                }
                Template template;
                if(templateName != null) template = templateGroup.getTemplate(templateName);
                else template = templateGroup.getTemplate(templateId);
                if(!(template instanceof PunishmentTemplate)) {
                    return null;
                }
                this.template = (PunishmentTemplate) template;
            }
            return this.template;
        }
    }
}
