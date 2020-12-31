/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.06.20, 17:45
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

package net.pretronic.dkbans.common.event;

import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;

public class DefaultDKBansPlayerPunishEvent implements DKBansPlayerPunishEvent {

    private final DKBansPlayer player;
    private final PlayerHistoryEntrySnapshot snapshot;

    public DefaultDKBansPlayerPunishEvent(DKBansPlayer player, PlayerHistoryEntrySnapshot snapshot) {
        this.player = player;
        this.snapshot = snapshot;
    }

    @Override
    public DKBansPlayer getPlayer() {
        return player;
    }

    @Override
    public PlayerHistoryEntry getEntry() {
        return snapshot.getEntry();
    }

    @Override
    public PlayerHistoryEntrySnapshot getSnapshot() {
        return snapshot;
    }
}
