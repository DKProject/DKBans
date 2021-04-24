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
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.time.Duration;

public class BroadcastGroupCreateCommand extends ObjectCommand<String> {

    public BroadcastGroupCreateCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("create"));
    }

    @Override
    public void execute(CommandSender sender, String name, String[] args) {
        if(args.length != 1) {
            sender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_CREATE_HELP);
            return;
        }
        if(DKBans.getInstance().getBroadcastManager().getGroup(name) != null) {
            sender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_ALREADY_EXISTS, VariableSet.create().add("name", name));
            return;
        }

        Duration duration = CommandUtil.parseDuration(sender,args[0]);
        if(duration == null) return;

        long interval = duration.getSeconds();
        BroadcastGroup group = DefaultDKBans.getInstance().getBroadcastManager().createGroup(name, Math.toIntExact(interval));
        sender.sendMessage(Messages.COMMAND_BROADCAST_GROUP_CREATED, VariableSet.create().addDescribed("group", group));
    }
}
