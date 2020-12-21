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
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.object.MainObjectCommand;
import net.pretronic.libraries.command.command.object.ObjectNotFindable;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.util.Arrays;

public class BroadcastCommand extends MainObjectCommand<Broadcast> implements ObjectNotFindable {

    private final BroadcastCreateCommand createCommand;

    public BroadcastCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
        this.createCommand = new BroadcastCreateCommand(owner);
    }

    @Override
    public Broadcast getObject(CommandSender commandSender, String search) {
        return DKBans.getInstance().getBroadcastManager().searchBroadcast(search);
    }

    @Override
    public void objectNotFound(CommandSender commandSender, String command, String[] args) {
        if(command.equalsIgnoreCase("list")) {

        } else if(command.equalsIgnoreCase("help")) {

        } else if(args.length > 0 && args[0].equalsIgnoreCase("create")) {
            createCommand.execute(commandSender, command, Arrays.copyOfRange(args, 1, args.length));
        }
    }
}
