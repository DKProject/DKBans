/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.12.20, 15:24
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

package net.pretronic.dkbans.minecraft.commands.broadcastgroup;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class BroadcastGroupListCommand extends BasicCommand {

    public BroadcastGroupListCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("list"));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        System.out.println(DKBans.getInstance().getBroadcastManager().getGroups());
        commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_LIST, VariableSet.create().addDescribed("groups", DKBans.getInstance().getBroadcastManager().getGroups()));
    }
}
