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

import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.chatlog.*;
import net.pretronic.dkbans.common.DefaultDKBans;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultChatLogManager implements ChatLogManager {

    @Override
    public ChatLogEntry getChatLogEntry(int id) {
        QueryResultEntry entry = DefaultDKBans.getInstance().getStorage().getPlayerChatLog().find().where("Id", id).execute().firstOrNull();
        if(entry == null) return null;
        return new DefaultChatLogEntry(entry.getInt("Id"),
                entry.getUniqueId("PlayerId"),
                entry.getString("Message"),
                entry.getLong("Time"),
                entry.getString("ServerName"),
                entry.getUniqueId("ServerId"),
                entry.getString("FilterAffiliationArea"));
    }

    @Override
    public ServerChatLog getServerChatLog(String serverName, UUID serverId) {
        return new DefaultServerChatLog(serverName, serverId);
    }

    @Override
    public ServerChatLog getServerChatLog(String serverName) {
        return new DefaultServerChatLog(serverName, null);
    }

    @Override
    public ServerChatLog getServerChatLog(UUID serverId) {
        return new DefaultServerChatLog(null, serverId);
    }

    @Override
    public PlayerChatLog getPlayerChatLog(UUID playerId) {
        return new DefaultPlayerChatLog(playerId);
    }

    @Override
    public ChatLogEntry createChatLogEntry(UUID playerId, String message, long time, String serverName, UUID serverId, String filterAffiliationArea) {
        return DKBans.getInstance().getStorage().createChatLogEntry(playerId, message, time, serverName, serverId, filterAffiliationArea);
    }

    @Override
    public CompletableFuture<ChatLogEntry> createChatLogEntryAsync(UUID playerId, String message, long time, String serverName, UUID serverId, String filterAffiliationArea) {
        return DKBans.getInstance().getStorage().createChatLogEntryAsync(playerId, message, time, serverName, serverId, filterAffiliationArea);
    }
}
