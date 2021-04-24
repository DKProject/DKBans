/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.07.20, 17:57
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

package net.pretronic.dkbans.minecraft.commands.dkbans;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.migration.Migration;
import net.pretronic.dkbans.api.migration.MigrationResult;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;

import java.util.concurrent.TimeUnit;

public class MigrationCommand extends BasicCommand {

    public MigrationCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder().name("migrate").permission(CommandConfig.PERMISSION_ADMIN).create());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.equals(McNative.getInstance().getConsoleSender())) {
            sender.sendMessage(Messages.ERROR_ONLY_CONSOLE, VariableSet.create()
                    .add("prefix", Messages.PREFIX));
            return;
        }
        if(args.length != 1) {
            sendHelpMessage(sender);
            return;
        }
        Migration migration = DKBans.getInstance().getMigrationManager().getMigration(args[0]);
        if(migration == null) {
            sendHelpMessage(sender);
            return;
        }

        try {
            MigrationResult result = migration.migrate();
            if(result.isSuccess()) {
                DKBans.getInstance().getLogger().info("Migration was successful");

                result.getMigrated().forEach((key, value) -> DKBans.getInstance().getLogger().info("- {} with an amount of {}", key, value));

                DKBans.getInstance().getLogger().info(" ");

                long millis = result.getTime();
                String time = String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                        TimeUnit.MILLISECONDS.toSeconds(millis)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                DKBans.getInstance().getLogger().info("It took " + time);
            } else {
                DKBans.getInstance().getLogger().error("Migration failed");
            }
        } catch (Exception exception) {
            DKBans.getInstance().getLogger().error("Migration failed");
            throw new RuntimeException(exception);
        }
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("${dkbans.prefix} &cUse /dkbans migrate <name>");
        sender.sendMessage("Following migrations are available:");
        DKBans.getInstance().getMigrationManager().getAvailableMigrations().forEach(migration -> sender.sendMessage("- " + migration.getName()));
    }
}
