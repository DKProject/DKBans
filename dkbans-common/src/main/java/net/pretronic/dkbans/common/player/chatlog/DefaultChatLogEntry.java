/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 15:48
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

package net.pretronic.dkbans.common.player.chatlog;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.chatlog.ChatLogEntry;

import java.util.UUID;

public class DefaultChatLogEntry implements ChatLogEntry {

    private final int id;
    private final UUID playerId;
    private final String message;
    private final long time;
    private final String serverName;
    private final UUID serverId;
    private final String filterAffiliationArea;

    public DefaultChatLogEntry(int id, UUID playerId, String message, long time, String serverName, UUID serverId, String filterAffiliationArea) {
        this.id = id;
        this.playerId = playerId;
        this.message = message;
        this.time = time;
        this.serverName = serverName;
        this.serverId = serverId;
        this.filterAffiliationArea = filterAffiliationArea;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public DKBansPlayer getPlayer() {
        return DKBans.getInstance().getPlayerManager().getPlayer(playerId);
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }

    @Override
    public UUID getServerId() {
        return this.serverId;
    }

    @Override
    public String getBlockedByFilterAffiliation() {
        return this.filterAffiliationArea;
    }
}
