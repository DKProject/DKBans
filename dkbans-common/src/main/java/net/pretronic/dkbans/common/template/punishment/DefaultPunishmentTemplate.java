package net.pretronic.dkbans.common.template.punishment;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateFactory;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.libraries.utility.map.Triple;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultPunishmentTemplate extends DefaultTemplate implements PunishmentTemplate {

    private final Map<Integer, PunishmentTemplateEntry> durations;
    private final Map<Integer, PunishmentTemplateEntry> points;
    private final int addedPoints;
    private final double pointsDivider;

    public DefaultPunishmentTemplate(int id, String name, String displayName, String permission, Collection<String> aliases,
                                     PlayerHistoryType historyType, PunishmentType punishmentType, boolean enabled, boolean hidden,
                                     Collection<? extends DKBansScope> scopes, TemplateCategory category, Document data) {
        super(id, name, displayName, permission, aliases, historyType, punishmentType, enabled, hidden, scopes, category, data);

        this.durations = loadDurations(data.getDocument("durations"));

        Triple<Map<Integer, PunishmentTemplateEntry>, Integer, Double> points = loadPoints(data.getDocument("points"));
        this.points = points.getFirst();
        this.addedPoints = points.getSecond();
        this.pointsDivider = points.getThird();
    }

    @Override
    public Map<Integer, PunishmentTemplateEntry> getDurations() {
        return this.durations;
    }

    @Override
    public Map<Integer, PunishmentTemplateEntry> getPoints() {
        return this.points;
    }

    @Override
    public int getAddedPoints() {
        return this.addedPoints;
    }

    @Override
    public double getPointsDivider() {
        return this.pointsDivider;
    }

    private Map<Integer, PunishmentTemplateEntry> loadDurations(Document data) {
        System.out.println(DocumentFileType.JSON.getWriter().write(data, true));
        Map<Integer, PunishmentTemplateEntry> durations = new HashMap<>();

        for (DocumentEntry entry0 : data) {
            Pair<Integer, PunishmentTemplateEntry> entry = loadEntry(entry0);
            durations.put(entry.getKey(), entry.getValue());
        }
        return durations;
    }

    private Triple<Map<Integer, PunishmentTemplateEntry>, Integer, Double> loadPoints(Document data) {
        System.out.println(DocumentFileType.JSON.getWriter().write(data, true));
        int addedPoints = data.getInt("addedPoints");
        double pointsDivider = data.getDouble("pointsDivider");
        Map<Integer, PunishmentTemplateEntry> points = new HashMap<>();

        for (DocumentEntry entry0 : data.getDocument("durations")) {
            Pair<Integer, PunishmentTemplateEntry> entry = loadEntry(entry0);
            points.put(entry.getKey(), entry.getValue());
        }

        return new Triple<>(points, addedPoints, pointsDivider);
    }

    private Pair<Integer, PunishmentTemplateEntry> loadEntry(DocumentEntry entry) {
        Document entryData = entry.toDocument();

        int id = Convert.toInteger(entryData.getKey());
        PunishmentType type = PunishmentType.getPunishmentType(entryData.getString("type"));

        PunishmentTemplateEntry templateEntry = PunishmentTemplateEntryFactory.create(type, entryData);
        return new Pair<>(id, templateEntry);
    }


    public static class Factory extends TemplateFactory {

        @Override
        public Template create(int id, String name, String displayName, String permission, Collection<String> aliases,
                               PlayerHistoryType historyType, PunishmentType punishmentType, boolean enabled, boolean hidden,
                               Collection<? extends DKBansScope> scopes, TemplateCategory category, Document data) {
            return new DefaultPunishmentTemplate(id, name, displayName, permission, aliases, historyType, punishmentType, enabled,
                    hidden, scopes, category, data);
        }
    }
}
