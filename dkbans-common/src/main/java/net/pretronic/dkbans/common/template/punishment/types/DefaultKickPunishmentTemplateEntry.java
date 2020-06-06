package net.pretronic.dkbans.common.template.punishment.types;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.punishment.types.KickPunishmentTemplateEntry;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;

public class DefaultKickPunishmentTemplateEntry extends DefaultPunishmentTemplateEntry implements KickPunishmentTemplateEntry {

    public DefaultKickPunishmentTemplateEntry(DKBansScope scope) {
        super(PunishmentType.KICK, scope);
    }

    public static class Factory extends PunishmentTemplateEntryFactory {

        @Override
        public PunishmentTemplateEntry create(Document data) {
            return new DefaultKickPunishmentTemplateEntry(DefaultDKBansScope.fromData(data));
        }

        @Override
        public Document createData(PunishmentTemplateEntry entry) {
            return Document.newDocument().add("scopeType", entry.getScope().getType())
                    .add("scopeName", entry.getScope().getName());
        }
    }
}
