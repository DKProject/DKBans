/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.08.21, 16:37
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

package net.pretronic.dkbans.minecraft.migration;

import net.pretronic.databasequery.api.driver.DatabaseDriver;
import net.pretronic.databasequery.sql.SQLDatabase;
import net.pretronic.databasequery.sql.driver.SQLDatabaseDriver;
import net.pretronic.dkbans.api.migration.Migration;
import net.pretronic.dkbans.api.migration.MigrationResult;
import net.pretronic.dkbans.api.migration.MigrationResultBuilder;
import net.pretronic.dkbans.common.DefaultDKBans;

public class SQLHistoryTable extends Migration {

    private static final String FOREIGN_KEY_GETTER_QUERY = "SELECT CONSTRAINT_NAME,concat('USE `',CONSTRAINT_SCHEMA,'`;ALTER TABLE `',CONSTRAINT_SCHEMA,'`.`', TABLE_NAME, '` DROP FOREIGN KEY `', CONSTRAINT_NAME, '`;')\n" +
            "FROM information_schema.key_column_usage\n" +
            "WHERE CONSTRAINT_SCHEMA = '%database%'\n" +
            "AND TABLE_NAME='dkbans_history'\n" +
            "AND REFERENCED_TABLE_NAME IS NOT NULL;";

    private static final String CREATE_FOREIGN_KEY_QUERY = "alter table %database%.dkbans_history " +
            "add constraint `%constraint_name%` " +
            "foreign key (SessionId) references %database%.dkbans_player_sessions (Id) " +
            "on update set null on delete set null;";

    public SQLHistoryTable() {
        super("sql-history-table");
    }

    @Override
    public MigrationResult migrate() {
        long start = System.currentTimeMillis();

        SQLDatabase database = (SQLDatabase) DefaultDKBans.getInstance().getStorage().getHistory().getDatabase();
        String query = FOREIGN_KEY_GETTER_QUERY.replace("%database%", database.getName());
        database.executeResultQuery(query, true, preparedStatement -> {}, resultSet -> {
            while (resultSet.next()) {
                String constraintName = resultSet.getString(1);
                String dropQuery = resultSet.getString(2);
                database.executeUpdateQuery(dropQuery, true, preparedStatement -> {});

                String createForeignKeyQuery = CREATE_FOREIGN_KEY_QUERY
                        .replace("%database%", database.getName())
                        .replace("%constraint_name%", constraintName);
                database.executeUpdateQuery(createForeignKeyQuery, true, preparedStatement -> {});
            }

            return true;
        });


        long time = System.currentTimeMillis()-start;
        return new MigrationResultBuilder()
                .success(true)
                .time(time)
                .create();
    }

    @Override
    public boolean isAvailable() {
        DatabaseDriver driver = DefaultDKBans.getInstance().getStorage().getHistory().getDatabase().getDriver();
        return driver instanceof SQLDatabaseDriver;
    }
}
