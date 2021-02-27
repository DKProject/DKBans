/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 27.12.20, 12:51
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

package net.pretronic.dkbans.api.event.punish;

import net.pretronic.dkbans.api.event.DKBansEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.libraries.event.network.NetworkEvent;

import java.util.UUID;

@NetworkEvent(ignoreNetworkException = true)
public interface DKBansPlayerPunishEvent extends DKBansEvent {

    UUID getPlayerId();

    DKBansPlayer getPlayer();

    PlayerHistoryEntry getEntry();

    PlayerHistoryEntrySnapshot getSnapshot();

}
