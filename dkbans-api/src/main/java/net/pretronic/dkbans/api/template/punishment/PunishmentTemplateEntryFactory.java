package net.pretronic.dkbans.api.template.punishment;

import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

import java.util.HashMap;
import java.util.Map;

public abstract class PunishmentTemplateEntryFactory {

    private static final Map<PunishmentType, PunishmentTemplateEntryFactory> FACTORY = new HashMap<>();


    public abstract PunishmentTemplateEntry create(Document data);

    public abstract Document createData(PunishmentTemplateEntry entry);


    public static PunishmentTemplateEntry create(PunishmentType punishmentType, Document data) {
        Validate.notNull(punishmentType, data);
        PunishmentTemplateEntryFactory factory = FACTORY.get(punishmentType);
        if(factory == null) throw new IllegalArgumentException("No punishment entry factory for punishment type " + punishmentType.getName() + " found");
        return factory.create(data);
    }

    public static Document toData(PunishmentTemplateEntry entry) {
        Validate.notNull(entry);
        PunishmentTemplateEntryFactory factory = FACTORY.get(entry.getType());
        if(factory == null) throw new IllegalArgumentException("No punishment entry factory for punishment type " + entry.getType().getName() + " found");
        return factory.createData(entry);
    }

    public static void register(PunishmentType punishmentType, PunishmentTemplateEntryFactory factory) {
        Validate.notNull(factory);
        FACTORY.put(punishmentType, factory);
    }
}
