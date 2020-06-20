package net.pretronic.dkbans.common.template.unpunishment;

import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntryOption;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;

import java.util.HashMap;
import java.util.Map;

public class DefaultUnPunishmentTemplateEntry implements UnPunishmentTemplateEntry {

    private final Map<PunishmentType, UnPunishmentTemplateEntryOption> options;

    public DefaultUnPunishmentTemplateEntry(Map<PunishmentType, UnPunishmentTemplateEntryOption> options) {
        this.options = options;
    }

    @Override
    public Map<PunishmentType, UnPunishmentTemplateEntryOption> getOptions() {
        return this.options;
    }

    private static Document entryOptionToDocument(UnPunishmentTemplateEntryOption option) {
        Document data = Document.newDocument();
        data.add("removeTime", option.getRemoveTime());
        data.add("divider", option.getDivider());
        option.getProperties().forEach(data::add);
        return data;
    }

    public static class Factory extends UnPunishmentTemplateEntryFactory {

        @Override
        public UnPunishmentTemplateEntry create(Document data0) {
            Map<PunishmentType, UnPunishmentTemplateEntryOption> options = new HashMap<>();

            for (DocumentEntry data1 : data0) {

                String punishmentType0 = data1.getKey();
                PunishmentType punishmentType = PunishmentType.getPunishmentType(punishmentType0);

                Map<String, Object> properties = new HashMap<>();
                long removeTime = 0;
                double divider = 0;
                for (DocumentEntry entry : data1.toDocument()) {
                    if(entry.getKey().equalsIgnoreCase("removeTime")) {
                        removeTime = entry.toPrimitive().getAsLong();
                    } else if(entry.getKey().equalsIgnoreCase("divider")) {
                        divider = entry.toPrimitive().getAsDouble();
                    } else {
                        properties.put(entry.getKey(), entry.toPrimitive().getAsObject());
                    }
                }
                options.put(punishmentType, new DefaultUnPunishmentTemplateEntryOption(removeTime, divider, properties));
            }
            return new DefaultUnPunishmentTemplateEntry(options);
        }

        @Override
        public Document createData(UnPunishmentTemplateEntry entry) {
            Document data = Document.newDocument();
            entry.getOptions().forEach((punishmentType, entryOption) -> data.add(punishmentType.getName(), entryOptionToDocument(entryOption)));
            return data;
        }
    }
}
