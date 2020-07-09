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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.NotFindable;
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class DKBansCommand extends MainCommand implements NotFindable {

    public DKBansCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("dkbans"));
        registerCommand(new DKBansMigrateCommand(owner));
    }

    @Override
    public void commandNotFound(CommandSender sender, String s, String[] strings) {
        if(sender.hasPermission(Permissions.ADMIN)) {
            sender.sendMessage(Messages.COMMAND_DKBANS_HELP);
        } else {
            sender.sendMessage(String.format("DKBans v%s was programmed by Pretronic (https://pretronic.net)",
                    DKBans.getInstance().getVersion()));
        }
    }
}
