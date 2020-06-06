package net.pretronic.dkbans.common.template.punishment;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
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
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.map.Pair;
import net.pretronic.libraries.utility.map.Triple;

import java.util.*;

public class DefaultPunishmentTemplate extends DefaultTemplate implements PunishmentTemplate {

    private final Map<Integer, PunishmentTemplateEntry> durations;
    private final Map<Integer, PunishmentTemplateEntry> points;
    private final int addedPoints;
    private final double pointsDivider;

    public DefaultPunishmentTemplate(int id, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                                     PlayerHistoryType historyType, PunishmentType punishmentType, boolean enabled, boolean hidden,
                                     Collection<? extends DKBansScope> scopes, TemplateCategory category, Document data) {
        super(id, name, group, displayName, permission, aliases, historyType, punishmentType, enabled, hidden, scopes, category, data);

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

    @Override
    public void build(DKBansPlayer player, DKBansExecutor executor, PlayerHistoryEntrySnapshotBuilder builder) {
        PunishmentTemplateEntry entry = getNextEntry(player);
        builder.historyType(getHistoryType())
                .punishmentType(entry.getType())
                .stuff(executor)
                .reason(getDisplayName())
                .template(this)
                .points(addedPoints);
        if(entry instanceof DurationAble) {
            builder.duration(((DurationAble)entry).getDuration());
        }

    }

    @Override
    public PunishmentTemplateEntry getNextEntry(DKBansPlayer player) {
        Map<Integer, PunishmentTemplateEntry> data;
        if(getGroup().getCalculationType() == CalculationType.AMOUNT) data = durations;
        else data = points;

        int count = player.getHistory().calculate(getGroup().getCalculationType(), getHistoryType());
        if(getGroup().getCalculationType() == CalculationType.POINTS) {
            count = (int) Math.round(count/pointsDivider);
        }

        PunishmentTemplateEntry punishmentTemplateEntry = null;

        for (Map.Entry<Integer, PunishmentTemplateEntry> entry : data.entrySet()) {
            if(count <= entry.getKey()) {
                punishmentTemplateEntry = entry.getValue();
            }
        }
        return punishmentTemplateEntry;
    }

    private Map<Integer, PunishmentTemplateEntry> loadDurations(Document data) {
        System.out.println(DocumentFileType.JSON.getWriter().write(data, true));
        Map<Integer, PunishmentTemplateEntry> durations = new TreeMap<>(Integer::compare);

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

    private static Document entryToDocument(DefaultPunishmentTemplate template) {
        Document data = Document.newDocument();
        for (Map.Entry<Integer, PunishmentTemplateEntry> entry : template.durations.entrySet()) {
            data.add(entry.getKey().toString(), PunishmentTemplateEntryFactory.toData(entry.getValue()));
        }
        return data;
    }

    public static class Factory extends TemplateFactory {

        @Override
        public Template create(int id, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                               PlayerHistoryType historyType, PunishmentType punishmentType, boolean enabled, boolean hidden,
                               Collection<? extends DKBansScope> scopes, TemplateCategory category, Document data) {
            return new DefaultPunishmentTemplate(id, name, group, displayName, permission, aliases, historyType, punishmentType, enabled,
                    hidden, scopes, category, data);
        }

        @Override
        public Document createData(Template template0) {
            DefaultPunishmentTemplate template = (DefaultPunishmentTemplate) template0;

            Document data = Document.newDocument();

            data.add("durations", entryToDocument(template));
            data.add("points", entryToDocument(template)
                    .add("addedPoints", template.addedPoints)
                    .add("pointsDivider", template.pointsDivider));
            return data;
        }
    }
}
