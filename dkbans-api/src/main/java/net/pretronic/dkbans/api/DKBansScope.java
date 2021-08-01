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

import java.util.Objects;

public class DKBansScope {

    public static final DKBansScope GLOBAL = new DKBansScope("GLOBAL","GLOBAL");

    public static final String DEFAULT_SERVER_GROUP = "SERVER_GROUP";
    public static final String DEFAULT_SERVER = "SERVER";

    private final String type;
    private final String name;

    private DKBansScope(String type, String name) {
        Validate.notNull(type, "Scope type can't be null");
        Validate.notNull(name, "Scope name can't be null");
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isGlobal(){
        return this.equals(GLOBAL);
    }

    public static DKBansScope fromData(Document data) {
        if(data == null || (!data.contains("scopeType") && !data.contains("scopeName"))) return null;
        return new DKBansScope(data.getString("scopeType"), data.getString("scopeName"));
    }

    public static DKBansScope parse(String scope){
        if(scope == null) return null;
        String[] parts = scope.split(":");
        if(parts.length == 2){
            return DKBansScope.of(parts[0],parts[1]);
        }
        throw new IllegalArgumentException("Invalid scope format (type:name)");
    }

    public static DKBansScope of(String type, String name){
        return new DKBansScope(type,name);
    }

    public static DKBansScope ofServer(String name){
        return new DKBansScope(DEFAULT_SERVER,name);
    }

    public static DKBansScope ofServerGroup(String name){
        return new DKBansScope(DEFAULT_SERVER_GROUP,name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DKBansScope that = (DKBansScope) o;
        return getType().equalsIgnoreCase(that.getType()) && getName().equalsIgnoreCase(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return type+":"+name;
    }
}
