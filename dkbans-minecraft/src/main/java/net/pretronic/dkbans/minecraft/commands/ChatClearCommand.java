/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 17:26
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

package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.text.Text;

import java.util.*;
import java.util.function.Predicate;

public class ChatClearCommand extends BasicCommand implements Completable {

    private final static List<String> COMMANDS = Arrays.asList("local","global");

    public ChatClearCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length > 0 && sender.hasPermission(Permissions.COMMAND_CHAT_CLEAR)){
            if(arguments[0].equalsIgnoreCase("local")){
                if(sender instanceof OnlineMinecraftPlayer){
                    OnlineMinecraftPlayer player = (OnlineMinecraftPlayer) sender;
                    MinecraftServer server = player.getServer();
                    for (int i = 0; i < 100; i++) {
                        server.broadcast(Text.EMPTY);
                    }
                    server.broadcast(Messages.COMMAND_CHAT_CLEAR_ALL);
                }else{
                    for (int i = 0; i < 100; i++) {
                        McNative.getInstance().getLocal().broadcast(Text.EMPTY);
                    }
                    McNative.getInstance().getLocal().broadcast(Messages.COMMAND_CHAT_CLEAR_ALL);
                }
            }else if(arguments[0].equalsIgnoreCase("global")){
                for (int i = 0; i < 100; i++) {
                    McNative.getInstance().getNetwork().broadcast(Text.EMPTY);
                }
                McNative.getInstance().getNetwork().broadcast(Messages.COMMAND_CHAT_CLEAR_ALL);
            }else{
                sender.sendMessage(Messages.COMMAND_CHAT_CLEAR_HELP);
            }
        }else{
            for (int i = 0; i < 100; i++) {
                sender.sendMessage(Text.EMPTY);
            }
            sender.sendMessage(Messages.COMMAND_CHAT_CLEAR_MY);
        }
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        return CommandUtil.completeSimple(COMMANDS,args);
    }
}
