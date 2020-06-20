package net.pretronic.dkbans.api.template.unpunishment;

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

public abstract class UnPunishmentTemplateEntryFactory {

    private static UnPunishmentTemplateEntryFactory FACTORY = null;


    public abstract UnPunishmentTemplateEntry create(Document data);

    public abstract Document createData(UnPunishmentTemplateEntry entry);


    public static UnPunishmentTemplateEntry createEntry(Document data) {
        UnPunishmentTemplateEntryFactory factory = FACTORY;
        if(factory == null) throw new IllegalArgumentException("UnPunishmentTemplateEntryFactory not set");
        return factory.create(data);
    }

    public static Document toData(UnPunishmentTemplateEntry entry) {
        Validate.notNull(entry);
        UnPunishmentTemplateEntryFactory factory = FACTORY;
        if(factory == null) throw new IllegalArgumentException("UnPunishmentTemplateEntryFactory not set");
        return factory.createData(entry);
    }

    public static void setFactory(UnPunishmentTemplateEntryFactory factory) {
        FACTORY = factory;
    }
}
