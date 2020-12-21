/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 18.12.20, 17:04
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
import net.pretronic.libraries.command.command.object.DefinedNotFindable;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.command.object.ObjectNotFindable;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Arrays;

public class BroadcastCommand extends MainObjectCommand<Broadcast> implements ObjectNotFindable, DefinedNotFindable<Broadcast> {

    private final BroadcastCreateCommand createCommand;
    private final BroadcastListCommand listCommand;

    public BroadcastCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
        this.createCommand = new BroadcastCreateCommand(owner);
        this.listCommand = new BroadcastListCommand(owner);
        registerCommand(new BroadcastDirectCommand(owner));
        registerCommand(new BroadcastDeleteCommand(owner));
    }

    @Override
    public Broadcast getObject(CommandSender commandSender, String value) {
        return DKBans.getInstance().getBroadcastManager().searchBroadcast(value);
    }

    @Override
    public void objectNotFound(CommandSender commandSender, String command, String[] args) {
        if(command.equalsIgnoreCase("help")) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_HELP);
        } else if(command.equalsIgnoreCase("list")) {
            this.listCommand.execute(commandSender, args);
        } else if(args.length > 0 && args[0].equalsIgnoreCase("create")) {
            createCommand.execute(commandSender, command, Arrays.copyOfRange(args, 1, args.length));
        } else {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_NOT_FOUND, VariableSet.create().add("broadcast", command));
        }
    }

    @Override
    public void commandNotFound(CommandSender commandSender, Broadcast broadcast, String command, String[] args) {
        if(broadcast != null) {
            if(command.equalsIgnoreCase("create")) {
                commandSender.sendMessage(Messages.COMMAND_BROADCAST_ALREADY_EXISTS, VariableSet.create().add("name", broadcast.getName()));
            } else commandSender.sendMessage(Messages.COMMAND_BROADCAST_HELP);
        } else {
            listCommand.execute(commandSender, args);
        }
    }
}
