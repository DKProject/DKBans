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

import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.dkbans.api.player.chatlog.ServerChatLog;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.libraries.utility.Validate;

import java.util.UUID;

public class DefaultServerChatLog extends DefaultChatLog implements ServerChatLog {

    private final String serverName;
    private final UUID serverId;

    public DefaultServerChatLog(String serverName, UUID serverId) {
        Validate.isTrue(serverName != null || serverId != null);
        this.serverName = serverName;
        this.serverId = serverId;
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
    public FindQuery getBaseQuery() {
        FindQuery query = ((DefaultDKBans)DefaultDKBans.getInstance()).getStorage().getPlayerChatLog().find();
        if(serverName != null) query.where("ServerName", serverName);
        if(serverId != null) query.where("ServerId", serverId);
        return query;
    }
}
