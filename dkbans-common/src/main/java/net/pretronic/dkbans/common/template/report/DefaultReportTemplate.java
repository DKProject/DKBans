package net.pretronic.dkbans.common.template.report;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateFactory;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.map.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DefaultReportTemplate extends DefaultTemplate implements ReportTemplate {

    private final PunishmentTemplate targetTemplate;
    private final Map<String, Object> properties;

    public DefaultReportTemplate(int id, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                                 PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data) {
        super(id, name, group, displayName, permission, aliases, historyType, enabled, hidden, category, data);

        Pair<PunishmentTemplate, Map<String, Object>> loaded = load(data);
        this.targetTemplate = loaded.getKey();
        this.properties = loaded.getValue();
    }

    @Override
    public PunishmentTemplate getTargetTemplate() {
        return this.targetTemplate;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    private Pair<PunishmentTemplate, Map<String, Object>> load(Document data) {
        String targetTemplate = data.getString("targetPunishment");

        String[] splitted = targetTemplate.split("@");

        if(splitted.length != 2) throw new IllegalArgumentException("Wrong targetPunishment length. Invalid format. Use TemplateGroup@TemplateName");

        String templateGroup0 = splitted[0];
        String templateName0 = splitted[1];

        TemplateGroup templateGroup = DKBans.getInstance().getTemplateManager().getTemplateGroup(templateGroup0);
        Validate.notNull(templateGroup, "Template group %s is null", templateGroup0);

        Template template0 = templateGroup.getTemplate(templateName0);
        Validate.notNull(template0, "Template %s is null", templateName0);
        Validate.isTrue(template0 instanceof PunishmentTemplate, "Target punishment template is not a punishment template");

        PunishmentTemplate template = (PunishmentTemplate)template0;

        Map<String, Object> properties = new HashMap<>();
        for (DocumentEntry entry : data) {
            if(entry.getKey().equalsIgnoreCase("targetPunishment")) {
                properties.put(entry.getKey(), entry.toPrimitive().getAsObject());
            }
        }
        return new Pair<>(template, properties);
    }

    public static class Factory extends TemplateFactory {

        @Override
        public Template create(int id, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases,
                               PlayerHistoryType historyType, boolean enabled, boolean hidden, TemplateCategory category, Document data) {
            return new DefaultReportTemplate(id, name, group, displayName, permission, aliases, historyType, enabled, hidden, category, data);
        }

        @Override
        public Document createData(Template template0) {
            ReportTemplate template = (ReportTemplate)template0;
            Document data = Document.newDocument()
                    .add("targetPunishment", template.getTargetTemplate().getGroup().getName() + "@" + template.getTargetTemplate().getName());
            template.getProperties().forEach(data::add);
            return data;
        }
    }
}
