/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.12.20, 16:24
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
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.broadcast.BroadcastGroup;
import net.pretronic.dkbans.api.broadcast.BroadcastOrder;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.UUID;

public class BroadcastGroupEditCommand extends ObjectCommand<BroadcastGroup> {

    public BroadcastGroupEditCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("edit"));
    }

    @Override
    public void execute(CommandSender commandSender, BroadcastGroup group, String[] args) {
        if(args.length < 2 || args[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_EDIT_HELP);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "name": {
                String name = args[1];
                if(DKBans.getInstance().getBroadcastManager().getGroup(name) != null) {
                    commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ALREADY_EXISTS, VariableSet.create().add("name", name));
                    return;
                }
                group.setName(name);
                sendEditedMessage(commandSender, "name", name);
                return;
            }
            case "enabled": {
                try {
                    boolean enabled = Convert.toBoolean(args[1]);
                    group.setEnabled(enabled);
                    sendEditedMessage(commandSender, "enabled", enabled);
                } catch (IllegalArgumentException exception) {
                    commandSender.sendMessage(Messages.ERROR_INVALID_BOOLEAN, VariableSet.create().add("value", args[1]));
                }

                return;
            }
            case "permission": {
                group.setPermission(args[1]);
                sendEditedMessage(commandSender, "permission", args[1]);
                return;
            }
            case "order": {
                String rawOrder = args[1];
                BroadcastOrder order = BroadcastOrder.parse(rawOrder);
                if(order == null) {
                    commandSender.sendMessage(Messages.ERROR_INVALID_BROADCAST_ORDER, VariableSet.create().add("prefix", Messages.PREFIX).add("value", rawOrder));
                    return;
                }
                group.setOrder(order);
                sendEditedMessage(commandSender, "order", order);
                return;
            }
            case "interval": {
                String rawInterval = args[1];
                if(!GeneralUtil.isNaturalNumber(rawInterval)) {
                    commandSender.sendMessage(Messages.ERROR_INVALID_NUMBER, VariableSet.create().add("number", rawInterval));
                    return;
                }
                group.setInterval(Integer.parseInt(rawInterval));
                sendEditedMessage(commandSender, "interval", rawInterval);
                return;
            }
            case "scope": {
                String[] rawScope = args[1].split(":");
                if(rawScope.length < 2) {
                    commandSender.sendMessage(Messages.ERROR_INVALID_SCOPE, VariableSet.create().add("value", args[1]));
                    return;
                }
                String type = rawScope[0];
                String name = rawScope[1];

                String rawId = rawScope[2];
                UUID id = null;
                if(rawId != null) {
                    try {
                        id = Convert.toUUID(rawId);
                    } catch (IllegalArgumentException exception) {
                        commandSender.sendMessage(Messages.ERROR_INVALID_SCOPE, VariableSet.create().add("value", args[1]));
                        return;
                    }
                }
                group.setScope(new DKBansScope(type, name, id));
                sendEditedMessage(commandSender, "scope", args[1]);
                return;
            }
            default: {
                commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_EDIT_HELP);
            }
        }
    }

    private void sendEditedMessage(CommandSender sender, String property, Object value) {
        sender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_EDIT_EDITED, VariableSet.create().add("property", property).add("value", value));
    }
}
