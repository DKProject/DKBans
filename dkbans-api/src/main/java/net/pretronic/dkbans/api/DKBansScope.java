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

import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

import java.util.Collection;
import java.util.UUID;

public class DKBansScope {

    private static final UUID ZERO_UUID = new UUID(0, 0);

    public static final DKBansScope GLOBAL = new DKBansScope("GLOBAL","GLOBAL",ZERO_UUID);
    public static final String DEFAULT_SERVER_GROUP = "SERVER_GROUP";
    public static final String DEFAULT_SERVER = "SERVER";
    public static final String DEFAULT_WORLD = "WORLD";


    private final String type;
    private final String name;
    private final UUID id;

    public DKBansScope(String type, String name) {
        this(type,name,null);
    }

    public DKBansScope(String type, String name, UUID id) {
        Validate.notNull(type, "Scope type can't be null");
        Validate.notNull(name, "Scope name can't be null");
        this.type = type;
        this.name = name;
        this.id = id == null ? ZERO_UUID : id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public boolean matches(DKBansScope scope){
        Validate.notNull(scope);
        return getType().equalsIgnoreCase(scope.getType()) && getName().equalsIgnoreCase(scope.getName());
    }

    public boolean matches(Collection<DKBansScope> scopes){
        Validate.notNull(scopes);
        for (DKBansScope scope : scopes) if(matches(scope)) return true;
        return false;
    }

    public boolean matchesExact(DKBansScope scope){
        Validate.notNull(scope);
        return matches(scope) && getId().equals(scope.getId());
    }

    public boolean matchesExact(Collection<DKBansScope> scopes){
        Validate.notNull(scopes);
        for (DKBansScope scope : scopes) if(matchesExact(scope)) return true;
        return false;
    }

    public static DKBansScope fromData(Document data) {
        if(data == null || (!data.contains("scopeType") && !data.contains("scopeName"))) return null;
        return new DKBansScope(data.getString("scopeType"), data.getString("scopeName"), ZERO_UUID);
    }
}
