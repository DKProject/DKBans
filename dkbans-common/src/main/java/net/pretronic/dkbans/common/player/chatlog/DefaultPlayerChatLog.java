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
import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.libraries.utility.Validate;

import java.util.UUID;

public class DefaultPlayerChatLog extends DefaultChatLog implements PlayerChatLog {

    private final UUID playerId;

    public DefaultPlayerChatLog(UUID playerId) {
        Validate.notNull(playerId);
        this.playerId = playerId;
    }

    @Override
    public UUID getPlayerId() {
        return this.playerId;
    }

    @Override
    public FindQuery getBaseQuery() {
        return DefaultDKBans.getInstance().getStorage().getPlayerChatLog().find()
                .where("PlayerId", playerId);
    }
}
