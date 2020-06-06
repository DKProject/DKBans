package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.libraries.document.Document;

public class DefaultPlayerHistoryEntrySnapshot implements PlayerHistoryEntrySnapshot {

    private final PlayerHistoryEntry entry;
    private final int id;
    private final PlayerHistoryType historyType;
    private final PunishmentType punishmentType;

    private final String reason;
    private final long timeout;

    private final Template template;

    private final DKBansExecutor stuff;
    private final DKBansScope scope;

    private final int points;
    private final boolean active;

    private final Document properties;
    private final String revokeMessage;
    private final Template revokeTemplate;

    private final long modifyTime;
    private final DKBansExecutor modifier;

    public DefaultPlayerHistoryEntrySnapshot(PlayerHistoryEntry entry, int id, PlayerHistoryType historyType, PunishmentType punishmentType
            , String reason, long timeout, Template template, DKBansExecutor stuff, DKBansScope scope, int points
            , boolean active, Document properties, String revokeMessage, Template revokeTemplate, long modifyTime, DKBansExecutor modifier) {
        this.entry = entry;
        this.id = id;
        this.historyType = historyType;
        this.punishmentType = punishmentType;
        this.reason = reason;
        this.timeout = timeout;
        this.template = template;
        this.stuff = stuff;
        this.scope = scope;
        this.points = points;
        this.active = active;
        this.properties = properties;
        this.revokeMessage = revokeMessage;
        this.revokeTemplate = revokeTemplate;
        this.modifyTime = modifyTime;
        this.modifier = modifier;
    }

    @Override
    public PlayerHistoryEntry getEntry() {
        return entry;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PlayerHistoryType getHistoryType() {
        return historyType;
    }

    @Override
    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public DKBansExecutor getStuff() {
        return stuff;
    }

    @Override
    public DKBansScope getScope() {
        return scope;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Document getProperties() {
        return properties;
    }

    @Override
    public String getRevokeReason() {
        return revokeMessage;
    }

    @Override
    public Template getRevokeTemplate() {
        return revokeTemplate;
    }

    @Override
    public long getModifyTime() {
        return modifyTime;
    }

    @Override
    public DKBansExecutor getModifiedBy() {
        return modifier;
    }
}
