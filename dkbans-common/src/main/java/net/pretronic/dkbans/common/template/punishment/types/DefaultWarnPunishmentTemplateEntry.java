package net.pretronic.dkbans.common.template.punishment.types;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.punishment.types.WarnPunishmentTemplateEntry;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;

public class DefaultWarnPunishmentTemplateEntry extends DefaultPunishmentTemplateEntry implements WarnPunishmentTemplateEntry {

    private final boolean shouldKick;
    private final PunishmentTemplate punishmentTemplate;

    public DefaultWarnPunishmentTemplateEntry(DKBansScope scope, boolean shouldKick, PunishmentTemplate punishmentTemplate) {
        super(PunishmentType.WARN, scope);
        this.shouldKick = shouldKick;
        this.punishmentTemplate = punishmentTemplate;
    }

    @Override
    public boolean shouldKick() {
        return this.shouldKick;
    }

    @Override
    public PunishmentTemplate getPunishmentTemplate() {
        return this.punishmentTemplate;
    }

    public static class Factory extends PunishmentTemplateEntryFactory {

        @Override
        public PunishmentTemplateEntry create(Document data) {
            //@Todo link to ban template
            return new DefaultWarnPunishmentTemplateEntry(DefaultDKBansScope.fromData(data), data.getBoolean("kick"), null);
        }
    }
}
