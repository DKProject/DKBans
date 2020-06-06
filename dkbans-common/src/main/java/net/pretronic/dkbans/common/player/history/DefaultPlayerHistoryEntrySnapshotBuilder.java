package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.map.Pair;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class DefaultPlayerHistoryEntrySnapshotBuilder implements PlayerHistoryEntrySnapshotBuilder {

    private final DKBansPlayer player;
    private final PlayerHistory history;
    private final PlayerHistoryEntry entry;

    private PlayerHistoryType historyType;
    private PunishmentType punishmentType;
    private String reason = "None";
    private long timeout = -1;
    private Template template = null;
    private DKBansExecutor stuff = null;
    private DKBansScope scope = null;
    private int points = 0;
    private boolean active = true;
    private Document properties;
    private String revokeReason = null;
    private Template revokeTemplate = null;
    private DKBansExecutor modifier;

    public DefaultPlayerHistoryEntrySnapshotBuilder(DKBansPlayer player, PlayerHistoryEntry entry) {
        this.player = player;
        this.history = player.getHistory();
        this.entry = entry;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder historyType(PlayerHistoryType historyType) {
        this.historyType = historyType;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder punishmentType(PunishmentType punishmentType) {
        this.punishmentType = punishmentType;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder reason(String reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder duration(long time, TimeUnit unit) {
        return timeout(System.currentTimeMillis()+unit.toMillis(time));
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder duration(Duration duration) {
        return duration(duration.getNano(),TimeUnit.NANOSECONDS);
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder template(Template template) {
        this.template = template;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder stuff(DKBansExecutor stuff) {
        this.stuff = stuff;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder scope(DKBansScope scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder points(int points) {
        this.points = points;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public DefaultPlayerHistoryEntrySnapshotBuilder properties(Document properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder revokeReason(String reason) {
        this.revokeReason = reason;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder revokeTemplate(Template template) {
        this.revokeTemplate = template;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder modifiedBy(DKBansExecutor executor) {
        modifier = executor;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshot execute() {
        //@Todo validate settings
        if(modifier == null) modifier = stuff;

        DefaultPlayerHistoryEntrySnapshot snapshot;
        if(entry == null){
            snapshot = new DefaultPlayerHistoryEntrySnapshot(null, -1, historyType, punishmentType,
                    reason, timeout, template, stuff, scope, points, active, properties, revokeReason, revokeTemplate,
                    true, System.currentTimeMillis(), modifier);

            Pair<PlayerHistoryEntry, Integer> result = DKBans.getInstance().getStorage().createHistoryEntry(player, snapshot);
            snapshot.setInsertResult(result);
        }else{
            snapshot = new DefaultPlayerHistoryEntrySnapshot(entry, -1, historyType,
                    punishmentType, reason, timeout, template, stuff, scope, points, active, properties, revokeReason,
                    revokeTemplate, true, System.currentTimeMillis(), modifier);
            int id = DKBans.getInstance().getStorage().insertHistoryEntrySnapshot(snapshot);
            snapshot.setId(id);
        }
        return snapshot;
    }

}
