package net.pretronic.dkbans.common.template.unpunishment;

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
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.libraries.utility.map.Triple;

import java.util.*;

public class DefaultUnPunishmentTemplate extends DefaultTemplate implements UnPunishmentTemplate {

    private final Collection<PunishmentTemplate> blacklistedTemplates;
    private final Collection<? extends DKBansScope> scopes;

    private final Map<Integer, UnPunishmentTemplateEntry> durations;

    private final Map<Integer, UnPunishmentTemplateEntry> points;
    private final int removedPoints;
    private final double pointsDivider;

    public DefaultUnPunishmentTemplate(int id, String name, TemplateGroup group, String displayName, String permission,
                                       Collection<String> aliases, PlayerHistoryType historyType, boolean enabled, boolean hidden,
                                       TemplateCategory category, Document data) {
        super(id, name, group, displayName, permission, aliases, historyType, enabled, hidden, category, data);

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
        return this.blacklistedTemplates;
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

    private Collection<PunishmentTemplate> loadBlacklistedTemplates(Document data) {
        return new ArrayList<>();//@Todo implement blacklisted templates
    }

    private Map<Integer, UnPunishmentTemplateEntry> loadDurations(Document data) {
        System.out.println(DocumentFileType.JSON.getWriter().write(data, true));
        Map<Integer, UnPunishmentTemplateEntry> durations = new TreeMap<>(Integer::compare);

        for (DocumentEntry entry0 : data) {
            Pair<Integer, UnPunishmentTemplateEntry> entry = loadEntry(entry0);
            durations.put(entry.getKey(), entry.getValue());
        }
        return durations;
    }

    private Triple<Map<Integer, UnPunishmentTemplateEntry>, Integer, Double> loadPoints(Document data) {
        System.out.println(DocumentFileType.JSON.getWriter().write(data, true));
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
        System.out.println("loadScopes");
        System.out.println(DocumentFileType.JSON.getWriter().write(data, true));
        System.out.println("---");
        Collection<DefaultDKBansScope> scopes = new ArrayList<>();
        for (DocumentEntry scope : data) {
            if(scope.isPrimitive()) {
                scopes.add(new DefaultDKBansScope(scope.toPrimitive().getKey(), scope.toPrimitive().getAsString(), null));
            } else {
                scopes.add(scope.toDocument().getAsObject(DefaultDKBansScope.class));
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
        public Template create(int id, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                               PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data) {
            return new DefaultUnPunishmentTemplate(id, name, group, displayName, permission, aliases, historyType, enabled,
                    hidden, category, data);
        }

        @Override
        public Document createData(Template template0) {
            DefaultUnPunishmentTemplate template = (DefaultUnPunishmentTemplate) template0;

            Document data = Document.newDocument();

            System.out.println("create unpunish data");
            System.out.println(template.getScopes());
            System.out.println(DocumentFileType.JSON.getWriter().write(Document.newDocument(template.getScopes()), false));
            System.out.println("---");

            data.add("scopes", DocumentFileType.JSON.getWriter().write(Document.newDocument(template.getScopes()), false));
            data.add("durations", entryToDocument(template));
            data.add("points", Document.newDocument()
                    .add("durations", entryToDocument(template))
                    .add("removedPoints", template.removedPoints)
                    .add("pointsDivider", template.pointsDivider));
            return data;
        }
    }
}
