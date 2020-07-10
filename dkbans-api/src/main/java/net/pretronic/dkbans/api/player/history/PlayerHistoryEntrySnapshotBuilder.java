/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 17:26
 * @web %web%
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

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


    PlayerHistoryEntrySnapshotBuilder modifiedBy(DKBansExecutor executor);

    PlayerHistoryEntrySnapshotBuilder modifiedTime(long timestamp) ;

    PlayerHistoryEntrySnapshot execute();

}
