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
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.network.component.server.MinecraftServer;
import org.mcnative.runtime.api.network.component.server.ServerStatusResponse;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

public class JumptoCommand extends BasicCommand implements Completable {

    public JumptoCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(!(sender instanceof OnlineMinecraftPlayer)){
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_JUMPTO_HELP);
            return;
        }
        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0],true);
        if(player == null) return;

        OnlineMinecraftPlayer onlinePlayer = player.getAsOnlinePlayer();
        if(onlinePlayer == null){
            sender.sendMessage(Messages.PLAYER_NOT_ONLINE, VariableSet.create()
                    .add("prefix",Messages.PREFIX_NETWORK)
                    .addDescribed("player",player));
            return;
        }

        MinecraftServer server = onlinePlayer.getServer();
        if(server == null){
            sender.sendMessage(Messages.SERVER_NOT_FOUND, VariableSet.create()
                    .add("prefix",Messages.PREFIX_NETWORK));
            return;
        }

        if(server.getName().equals(((OnlineMinecraftPlayer) sender).getServer().getName())){
            sender.sendMessage(Messages.SERVER_ALREADY_CONNECTED, VariableSet.create()
                    .add("prefix",Messages.PREFIX_NETWORK)
                    .addDescribed("player",player)
                    .addDescribed("server",server));
            return;
        }

        sender.sendMessage(Messages.SERVER_CONNECTING, VariableSet.create()
                .add("prefix",Messages.PREFIX_NETWORK)
                .addDescribed("player",player)
                .addDescribed("server",server));
        ((OnlineMinecraftPlayer) sender).connect(server);
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        if(sender instanceof  OnlineMinecraftPlayer){
            if(args.length == 0) {
                return Iterators.map(McNative.getInstance().getLocal().getConnectedPlayers()
                        , ConnectedMinecraftPlayer::getName
                        , player -> !player.getServer().equals(((OnlineMinecraftPlayer) sender).getServer()));
            }else if(args.length == 1) {
                return Iterators.map(McNative.getInstance().getLocal().getConnectedPlayers()
                        , ConnectedMinecraftPlayer::getName
                        , player -> !player.getServer().equals(((OnlineMinecraftPlayer) sender).getServer())
                                && player.getName().toLowerCase().startsWith(args[0].toLowerCase()));
            }
        }
        return Collections.emptyList();
    }
}
