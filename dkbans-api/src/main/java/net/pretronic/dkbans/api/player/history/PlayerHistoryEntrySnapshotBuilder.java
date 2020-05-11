package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.libraries.document.Document;

public interface PlayerHistoryEntrySnapshotBuilder {


    PlayerHistoryEntrySnapshotBuilder historyType(PlayerHistoryType type);

    PlayerHistoryEntrySnapshotBuilder punishmentType(PunishmentType type);


    PlayerHistoryEntrySnapshotBuilder reason(String reason);

    PlayerHistoryEntrySnapshotBuilder timeout(long timeout);

    PlayerHistoryEntrySnapshotBuilder stuff(DKBansExecutor executor);

    PlayerHistoryEntrySnapshotBuilder stuff(int points);

    PlayerHistoryEntrySnapshotBuilder active(boolean active);

    PlayerHistoryEntrySnapshotBuilder properties(Document properties);

    PlayerHistoryEntrySnapshotBuilder revokeReason(String reason);

    PlayerHistoryEntrySnapshotBuilder revokeTemplate(String reason);

    PlayerHistoryEntrySnapshot execute();

}
