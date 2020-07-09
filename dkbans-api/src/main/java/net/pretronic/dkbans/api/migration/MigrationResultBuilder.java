/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.07.20, 17:39
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

package net.pretronic.dkbans.api.migration;

import java.util.Map;

public class MigrationResultBuilder {

    private boolean success;
    private long time;
    private Map<String, String> migrated;

    public MigrationResultBuilder success(boolean success) {
        this.success = success;
        return this;
    }

    public MigrationResultBuilder time(long time) {
        this.time = time;
        return this;
    }

    public MigrationResultBuilder migrated(Map<String, String> migrated) {
        this.migrated = migrated;
        return this;
    }

    public MigrationResultBuilder addMigrated(String key, String value) {
        this.migrated.put(key, value);
        return this;
    }

    public MigrationResultBuilder addMigrated(String key, int amount) {
        return addMigrated(key, String.valueOf(amount));
    }

    public MigrationResult create() {
        return new MigrationResult(success, time, migrated);
    }
}