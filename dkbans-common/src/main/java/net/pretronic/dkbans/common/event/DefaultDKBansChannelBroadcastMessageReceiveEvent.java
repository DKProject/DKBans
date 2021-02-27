/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.07.20, 20:39
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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.event.DKBansChannelBroadcastMessageReceiveEvent;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.event.network.NetworkEvent;
import net.pretronic.libraries.event.network.NetworkEventAdapter;

import java.util.UUID;

public class DefaultDKBansChannelBroadcastMessageReceiveEvent implements DKBansChannelBroadcastMessageReceiveEvent {

    private final String channel;
    private final String message;
    private final UUID executorId;

    private transient DKBansExecutor cachedExecutor;

    public DefaultDKBansChannelBroadcastMessageReceiveEvent(String channel, String message, UUID executorId,DKBansExecutor cachedExecutor) {
        this.channel = channel;
        this.message = message;
        this.executorId = executorId;
        this.cachedExecutor = cachedExecutor;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public UUID getExecutorId() {
        return executorId;
    }

    @Override
    public DKBansExecutor getExecutor() {
        if(cachedExecutor == null) cachedExecutor = DKBans.getInstance().getPlayerManager().getExecutor(executorId);
        return cachedExecutor;
    }
}
