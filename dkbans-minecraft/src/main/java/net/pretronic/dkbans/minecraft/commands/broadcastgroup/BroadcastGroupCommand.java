/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.12.20, 14:31
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
import net.pretronic.dkbans.api.broadcast.BroadcastGroup;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.DefinedNotFindable;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.command.object.ObjectNotFindable;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Arrays;

public class BroadcastGroupCommand extends MainObjectCommand<BroadcastGroup> implements ObjectNotFindable, DefinedNotFindable<BroadcastGroup> {

    private final BroadcastGroupCreateCommand createCommand;
    private final BroadcastGroupListCommand listCommand;
    private final BroadcastGroupInfoCommand infoCommand;

    public BroadcastGroupCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
        this.createCommand = new BroadcastGroupCreateCommand(owner);
        this.listCommand = new BroadcastGroupListCommand(owner);
        registerCommand(new BroadcastGroupAssignmentCommand(owner));
        registerCommand(new BroadcastGroupEditCommand(owner));
        this.infoCommand = new BroadcastGroupInfoCommand(owner);
        registerCommand(infoCommand);
    }

    @Override
    public BroadcastGroup getObject(CommandSender commandSender, String search) {
        return DKBans.getInstance().getBroadcastManager().searchGroup(search);
    }

    @Override
    public void objectNotFound(CommandSender commandSender, String command, String[] args) {
        if(command.equalsIgnoreCase("list")) {
            listCommand.execute(commandSender, args);
        } else if(command.equalsIgnoreCase("help")) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_HELP);
        } else if(args.length > 0 && args[0].equalsIgnoreCase("create")) {
            createCommand.execute(commandSender, command, Arrays.copyOfRange(args, 1, args.length));
        } else {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_NOT_FOUND, VariableSet.create().add("group", command));
        }
    }

    @Override
    public void commandNotFound(CommandSender commandSender, BroadcastGroup group, String command, String[] args) {
        if(group != null) {
            if(command == null) infoCommand.execute(commandSender, group, args);
            else commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_HELP);
        } else {
            listCommand.execute(commandSender, args);
        }
    }
}
