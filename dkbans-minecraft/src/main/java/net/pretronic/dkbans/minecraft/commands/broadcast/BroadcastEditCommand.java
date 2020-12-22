/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 22.12.20, 14:05
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
import net.pretronic.dkbans.api.broadcast.BroadcastVisibility;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class BroadcastEditCommand extends ObjectCommand<Broadcast> {

    public BroadcastEditCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("edit"));
    }

    @Override
    public void execute(CommandSender commandSender, Broadcast broadcast, String[] args) {
        if(args.length < 2 || args[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_EDIT_HELP);
            return;
        }
        switch (args[0].toLowerCase()) {
            case "name": {
                String name = args[1];
                if(DKBans.getInstance().getBroadcastManager().getBroadcast(name) != null) {
                    commandSender.sendMessage(Messages.COMMAND_BROADCAST_ALREADY_EXISTS, VariableSet.create().add("name", name));
                    return;
                }
                broadcast.setName(name);
                sendEditedMessage(commandSender, "name", name);
                return;
            }
            case "visibility": {
                String visibility0 = args[1];
                BroadcastVisibility visibility = BroadcastVisibility.parse(visibility0);
                if(visibility == null) {
                    commandSender.sendMessage(Messages.COMMAND_BROADCAST_VISIBILITY_NOT_EXISTS, VariableSet.create().add("name", visibility0));
                    return;
                }
                broadcast.setVisibility(visibility);
                sendEditedMessage(commandSender, "visibility", visibility);
                return;
            }
            case "text": {
                StringBuilder text = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    text.append(args[i]);
                }
                broadcast.setText(text.toString());
                sendEditedMessage(commandSender, "text", text.toString());
                return;
            }
            default: {
                commandSender.sendMessage(Messages.COMMAND_BROADCAST_EDIT_HELP);
            }
        }
    }

    private void sendEditedMessage(CommandSender sender, String property, Object value) {
        sender.sendMessage(Messages.COMMAND_BROADCAST_EDIT_EDITED, VariableSet.create().add("property", property).add("value", value));
    }
}
