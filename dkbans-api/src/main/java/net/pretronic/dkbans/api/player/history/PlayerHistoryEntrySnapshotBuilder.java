package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.libraries.document.Document;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface PlayerHistoryEntrySnapshotBuilder {


    PlayerHistoryEntrySnapshotBuilder historyType(PlayerHistoryType type);

    PlayerHistoryEntrySnapshotBuilder punishmentType(PunishmentType type);


    PlayerHistoryEntrySnapshotBuilder reason(String reason);

    PlayerHistoryEntrySnapshotBuilder timeout(long timeout);

    PlayerHistoryEntrySnapshotBuilder duration(long time, TimeUnit unit);

    PlayerHistoryEntrySnapshotBuilder duration(Duration duration);

    PlayerHistoryEntrySnapshotBuilder stuff(DKBansExecutor executor);

    PlayerHistoryEntrySnapshotBuilder points(int points);

    PlayerHistoryEntrySnapshotBuilder active(boolean active);

    PlayerHistoryEntrySnapshotBuilder properties(Document properties);


    PlayerHistoryEntrySnapshotBuilder template(Template template);

    PlayerHistoryEntrySnapshotBuilder scope(DKBansScope scope);



    PlayerHistoryEntrySnapshotBuilder revokeReason(String reason);

    PlayerHistoryEntrySnapshotBuilder revokeTemplate(Template template);


    PlayerHistoryEntrySnapshot execute();

}
