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
import net.pretronic.dkbans.api.broadcast.Broadcast;
import net.pretronic.dkbans.api.broadcast.BroadcastAssignment;
import net.pretronic.dkbans.api.broadcast.BroadcastGroup;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Arrays;

public class BroadcastGroupAssignmentCommand extends ObjectCommand<BroadcastGroup> {

    public BroadcastGroupAssignmentCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("assignment"));
    }

    @Override
    public void execute(CommandSender commandSender, BroadcastGroup group, String[] args) {
        System.out.println("Assignment:" + Arrays.toString(args) + ":" + group);
        if(args[0].equalsIgnoreCase("list")) {
            list(commandSender, group);
        } else if(args[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ASSIGNMENT_HELP);
        } else {
            String rawIdentifier = args[0];
            if(args.length > 1) {
                if(args[1].equalsIgnoreCase("add")) {
                    Broadcast broadcast = DKBans.getInstance().getBroadcastManager().searchBroadcast(rawIdentifier);
                    if(broadcast == null) {
                        commandSender.sendMessage(Messages.COMMAND_BROADCAST_NOT_FOUND, VariableSet.create().add("broadcast", rawIdentifier));
                        return;
                    }
                    BroadcastAssignment assignment = group.addBroadcast(broadcast);
                    commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ASSIGNMENT_ADD, VariableSet.create().addDescribed("assignment", assignment));
                } else if(args[1].equalsIgnoreCase("remove")) {
                    BroadcastAssignment assignment = group.searchAssignment(rawIdentifier);
                    if(assignment == null) {
                        commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ASSIGNMENT_NOT_FOUND, VariableSet.create().add("assignment", rawIdentifier));
                        return;
                    }
                    group.removeAssignment(assignment);
                    commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ASSIGNMENT_REMOVE, VariableSet.create().addDescribed("assignment", assignment));
                } else if(args[1].equalsIgnoreCase("info")) {
                    info(commandSender, group, rawIdentifier);
                }
            } else {
                info(commandSender, group, rawIdentifier);
            }
        }
    }

    private void list(CommandSender commandSender, BroadcastGroup group) {
        commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ASSIGNMENT_LIST, VariableSet.create().addDescribed("assignments", group.getAssignments()));
    }

    private void info(CommandSender commandSender, BroadcastGroup group, String rawIdentifier) {
        BroadcastAssignment assignment = group.searchAssignment(rawIdentifier);
        if(assignment == null) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ASSIGNMENT_NOT_FOUND, VariableSet.create().add("assignment", rawIdentifier));
            return;
        }
        commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ASSIGNMENT_INFO, VariableSet.create().addDescribed("assignment", assignment));
    }
}
