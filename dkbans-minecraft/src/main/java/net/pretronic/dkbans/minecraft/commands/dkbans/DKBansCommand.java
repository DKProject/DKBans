/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 09.07.20, 17:50
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

import net.pretronic.dkbans.minecraft.commands.dkbans.template.TemplateCommand;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.NoPermissionAble;
import net.pretronic.libraries.command.NotFindable;
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class DKBansCommand extends MainCommand implements NotFindable, NoPermissionAble {

    private final InfoCommand infoCommand;

    public DKBansCommand(ObjectOwner owner) {
        super(owner,  CommandConfiguration.newBuilder()
                .name("dkbans")
                .permission(CommandConfig.PERMISSION_ADMIN)
                .create());
        infoCommand = new InfoCommand(owner);
        registerCommand(new MigrationCommand(owner));
        registerCommand(new TemplateCommand(owner));
        registerCommand(infoCommand);
    }

    @Override
    public void commandNotFound(CommandSender sender, String s, String[] strings) {
        sender.sendMessage(Messages.COMMAND_DKBANS_HELP);
    }

    @Override
    public void noPermission(CommandSender sender, String s, String s1, String[] arguments) {
        infoCommand.execute(sender,arguments);
    }
}
