/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.12.20, 17:05
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
import net.pretronic.dkbans.api.broadcast.BroadcastProperty;
import net.pretronic.dkbans.api.broadcast.BroadcastVisibility;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.ObjectCommand;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.*;

public class BroadcastCreateCommand extends ObjectCommand<String> implements Completable {

    public BroadcastCreateCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("create"));
    }

    @Override
    public void execute(CommandSender commandSender, String name, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_CREATE_HELP);
            return;
        }
        if(DKBans.getInstance().getBroadcastManager().getBroadcast(name) != null) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_ALREADY_EXISTS, VariableSet.create().add("name", name));
            return;
        }
        BroadcastVisibility visibility = BroadcastVisibility.parse(args[0]);
        if(visibility == null) {
            commandSender.sendMessage(Messages.COMMAND_BROADCAST_VISIBILITY_NOT_EXISTS, VariableSet.create().add("name", args[0]));
            return;
        }
        StringBuilder text = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            text.append(" ").append(args[i]);
        }
        Broadcast broadcast = DKBans.getInstance().getBroadcastManager()
                .createBroadcast(name, visibility, Document.newDocument().add(BroadcastProperty.TEXT, text.substring(1)));
        commandSender.sendMessage(Messages.COMMAND_BROADCAST_CREATED, VariableSet.create().addDescribed("broadcast", broadcast));
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        if(args.length == 0){
            Collection<String> result = new ArrayList<>();
            for (BroadcastVisibility value : BroadcastVisibility.values()) result.add(value.name());
            return result;
        }else if(args.length == 1){
            Collection<String> result = new ArrayList<>();
            for (BroadcastVisibility value : BroadcastVisibility.values()){
                if(value.name().startsWith(args[0].toUpperCase())) result.add(value.name());
            }
            return result;
        }else return Collections.emptyList();
    }
}
