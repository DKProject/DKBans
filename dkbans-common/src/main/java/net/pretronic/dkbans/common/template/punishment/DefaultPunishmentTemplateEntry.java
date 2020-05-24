package net.pretronic.dkbans.common.template.punishment;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;

public class DefaultPunishmentTemplateEntry implements PunishmentTemplateEntry {

    private final PunishmentType type;
    private final DKBansScope scope;

    public DefaultPunishmentTemplateEntry(PunishmentType type, DKBansScope scope) {
        this.type = type;
        this.scope = scope;
    }

    @Override
    public PunishmentType getType() {
        return this.type;
    }

    @Override
    public DKBansScope getScope() {
        return this.scope;
    }
}
