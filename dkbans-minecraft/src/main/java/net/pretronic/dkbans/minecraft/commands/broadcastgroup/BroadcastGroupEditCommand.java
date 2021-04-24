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
import net.pretronic.dkbans.api.broadcast.BroadcastVisibility;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.*;

public class BroadcastGroupEditCommand extends ObjectCommand<BroadcastGroup> implements Completable {

    private final static List<String> COMMANDS = Arrays.asList("name","enabled","permission","order","interval","scope");
    private final static List<String> COMMANDS_BOOLEAN = Arrays.asList("true","false");
    private final static List<String> COMMANDS_SCOPE = Arrays.asList("GLOBAL","SERVER","SERVER_GROUP");

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
                try {
                    long interval = DurationProcessor.getStandard().parse(rawInterval).getSeconds();
                    group.setInterval(Math.toIntExact(interval));
                    sendEditedMessage(commandSender, "interval", rawInterval);
                } catch (IllegalArgumentException exception) {
                    commandSender.sendMessage(Messages.ERROR_INVALID_DURATION_FORMAT, VariableSet.create().addDescribed("duration", rawInterval));
                }

                return;
            }
            case "scope": {
                String type = args[1];

                if(!type.equalsIgnoreCase("global") && args.length < 3) {
                    commandSender.sendMessage(Messages.ERROR_INVALID_SCOPE, VariableSet.create().add("value", args[1]));
                    return;
                }
                String name = args.length < 3 ? "GLOBAL" : args[2];

                group.setScope(DKBansScope.of(type, name));
                sendEditedMessage(commandSender, "scope", args[1]+":"+args[2]);
                return;
            }
            default: {
                commandSender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_EDIT_HELP);
            }
        }
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        if(args.length == 0) return COMMANDS;
        else if(args.length == 1){
            return Iterators.filter(COMMANDS, command -> command.startsWith(args[0].toLowerCase()));
        }else if(args.length == 2){
            String command = args[0];
            if(command.equalsIgnoreCase("enabled")){
                return Iterators.filter(COMMANDS_BOOLEAN, command0 -> command0.startsWith(args[1].toLowerCase()));
            }else if(command.equalsIgnoreCase("order")){
                Collection<String> result = new ArrayList<>();
                for (BroadcastOrder value : BroadcastOrder.values()){
                    if(value.name().startsWith(args[0].toUpperCase())) result.add(value.name());
                }
                return result;
            }else if(command.equalsIgnoreCase("scope")){
                return Iterators.filter(COMMANDS_SCOPE, command0 -> command0.startsWith(args[1].toLowerCase()));
            }
        }
        return Collections.emptyList();
    }

    private void sendEditedMessage(CommandSender sender, String property, Object value) {
        sender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_EDIT_EDITED, VariableSet.create().add("property", property).add("value", value));
    }
}
