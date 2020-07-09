/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.07.20, 17:45
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

package net.pretronic.dkbans.common.migration;

import net.pretronic.dkbans.api.migration.Migration;
import net.pretronic.dkbans.api.migration.MigrationManager;
import net.pretronic.dkbans.api.migration.MigrationResult;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class DefaultMigrationManager implements MigrationManager {

    private final Collection<Migration> migrations;

    public DefaultMigrationManager() {
        this.migrations = new ArrayList<>();
    }

    @Override
    public Collection<Migration> getMigrations() {
        return this.migrations;
    }

    @Override
    public Collection<Migration> getAvailableMigrations() {
        return Iterators.filter(this.migrations, Migration::isAvailable);
    }

    @Override
    public Migration getMigration(String name) {
        return Iterators.findOne(this.migrations, migration -> migration.getName().equalsIgnoreCase(name));
    }

    @Override
    public void registerMigration(Migration migration) {
        Validate.notNull(migration);
        Validate.isTrue(getMigration(migration.getName()) == null, "Migration with name %a already registered", migration.getName());
        this.migrations.add(migration);
    }
}
