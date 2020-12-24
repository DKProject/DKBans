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

package net.pretronic.dkbans.api;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.util.UUID;

public interface DKBansExecutor {

    DKBansExecutor CONSOLE = new SpecialExecutor(new UUID(0,0),"Console");

    DKBansExecutor IP_ADDRESS_BLOCK = new SpecialExecutor(new UUID(0,1),"Alt Manager");

    String getName();

    UUID getUniqueId();

    default boolean isPlayer(){
        return getPlayer() != null;
    }

    @Nullable
    DKBansPlayer getPlayer();


    class SpecialExecutor implements DKBansExecutor{

        private final UUID uniqueId;
        private final String name;

        public SpecialExecutor(UUID uniqueId, String name) {
            this.uniqueId = uniqueId;
            this.name = name;
        }

        @Override
        public UUID getUniqueId() {
            return uniqueId;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public DKBansPlayer getPlayer() {
            return null;
        }

    }

}
