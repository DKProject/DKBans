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

package net.pretronic.dkbans.api.player;

import net.pretronic.dkbans.api.DKBansExecutor;

import java.util.Collection;
import java.util.UUID;

public interface DKBansPlayerManager {

    DKBansPlayer getPlayer(UUID uniqueId);

    DKBansPlayer getPlayer(String name);

    Collection<DKBansPlayer> getPlayers(String address);

    Collection<DKBansPlayer> getLoadedPlayers();


    DKBansExecutor getExecutor(UUID uniqueId);

    DKBansExecutor getExecutor(String name);


    void registerSpecialExecutor(DKBansExecutor executor);
}
