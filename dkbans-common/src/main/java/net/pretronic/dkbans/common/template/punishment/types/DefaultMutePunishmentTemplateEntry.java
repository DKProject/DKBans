package net.pretronic.dkbans.common.template.punishment.types;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntryFactory;
import net.pretronic.dkbans.api.template.punishment.types.MutePunishmentTemplateEntry;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.duration.DurationProcessor;

import java.time.Duration;

public class DefaultMutePunishmentTemplateEntry extends DefaultPunishmentTemplateEntry implements MutePunishmentTemplateEntry {

    private final Duration duration;

    public DefaultMutePunishmentTemplateEntry(DKBansScope scope, Duration duration) {
        super(PunishmentType.MUTE, scope);
        this.duration = duration;
    }

    @Override
    public Duration getDuration() {
        return this.duration;
    }

    public static class Factory extends PunishmentTemplateEntryFactory {

        @Override
        public PunishmentTemplateEntry create(Document data) {
            return new DefaultMutePunishmentTemplateEntry(DefaultDKBansScope.fromData(data), DurationProcessor.getStandard().parse(data.getString("duration")));
        }

        @Override
        public Document createData(PunishmentTemplateEntry entry0) {
            MutePunishmentTemplateEntry entry = (MutePunishmentTemplateEntry) entry0;
            return Document.newDocument()
                    .add("type", entry.getType().getName())
                    .add("scopeType", entry.getScope().getType())
                    .add("scopeName", entry.getScope().getName())
                    .add("duration", DurationProcessor.getStandard().format(entry.getDuration()));
        }
    }
}
