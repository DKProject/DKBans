/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.06.20, 17:19
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

package net.pretronic.dkbans.common;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.libraries.document.Document;

import java.util.UUID;

public class DefaultDKBansScope implements DKBansScope {

    private static final UUID ZERO_UUID = new UUID(0, 0);

    private final String type;
    private final String name;
    private final UUID id;

    public DefaultDKBansScope(String type, String name, UUID id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public boolean isCurrent() {
        return true;//@Todo implement current scope check
    }

    public static DefaultDKBansScope fromData(Document data) {
        return new DefaultDKBansScope(data.getString("scopeType"), data.getString("scopeName"), ZERO_UUID);
    }
}
