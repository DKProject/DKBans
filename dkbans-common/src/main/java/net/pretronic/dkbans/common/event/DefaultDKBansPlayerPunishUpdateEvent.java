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

import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishUpdateEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;

import java.util.UUID;

public class DefaultDKBansPlayerPunishUpdateEvent implements DKBansPlayerPunishUpdateEvent {

    private final DKBansPlayer player;
    private final PlayerHistoryEntrySnapshot oldSnapshot;
    private final PlayerHistoryEntrySnapshot newSnapshot;

    public DefaultDKBansPlayerPunishUpdateEvent(DKBansPlayer player, PlayerHistoryEntrySnapshot oldSnapshot, PlayerHistoryEntrySnapshot newSnapshot) {
        this.player = player;
        this.oldSnapshot = oldSnapshot;
        this.newSnapshot = newSnapshot;
    }

    @Override
    public UUID getUniqueId() {
        return null;
    }

    @Override
    public DKBansPlayer getPlayer() {
        return player;
    }

    @Override
    public PlayerHistoryEntry getEntry() {
        return newSnapshot.getEntry();
    }

    @Override
    public PlayerHistoryEntrySnapshot getNewSnapshot() {
        return newSnapshot;
    }

    @Override
    public PlayerHistoryEntrySnapshot getOldSnapshot() {
        return oldSnapshot;
    }
}
