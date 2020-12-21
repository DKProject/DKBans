/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.12.20, 17:28
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

package net.pretronic.dkbans.minecraft.commands.broadcast;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.broadcast.Broadcast;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class BroadcastDeleteCommand extends ObjectCommand<Broadcast> {

    public BroadcastDeleteCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("delete"));
    }

    @Override
    public void execute(CommandSender commandSender, Broadcast broadcast, String[] strings) {
        DKBans.getInstance().getBroadcastManager().deleteBroadcast(broadcast);
        commandSender.sendMessage(Messages.COMMAND_BROADCAST_DELETED, VariableSet.create().add("broadcast", broadcast));
    }
}
