/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 21.03.21, 20:17
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

import net.pretronic.dkbans.api.event.DKBansBypassCheckEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;

import java.util.UUID;

public class DefaultDKBansBypassCheckEvent implements DKBansBypassCheckEvent {

    private final DKBansPlayer player;
    private boolean bypass;

    public DefaultDKBansBypassCheckEvent(DKBansPlayer player, boolean bypass) {
        this.player = player;
        this.bypass = bypass;
    }

    @Override
    public boolean hasBypass() {
        return bypass;
    }

    @Override
    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

    @Override
    public UUID getPlayerId() {
        return player.getUniqueId();
    }

    @Override
    public DKBansPlayer getPlayer() {
        return player;
    }
}
