/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.07.20, 18:34
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

package net.pretronic.dkbans.minecraft.commands.ip;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlock;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressInfo;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.Collection;

public class IpInfoCommand extends BasicCommand {

    public IpInfoCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1) {
            sender.sendMessage(Messages.COMMAND_IP_INFO_HELP);
            return;
        }

        if(CommandUtil.IPADDRESS_PATTERN.matcher(arguments[0]).matches()){
            String address = arguments[0];

            if(arguments.length > 1 && arguments[1].equalsIgnoreCase("details")){
                IpAddressBlock block = DKBans.getInstance().getIpAddressManager().getIpAddressBlock(address);
                if(block == null){
                    sender.sendMessage(Messages.ERROR_INVALID_IP_ADDRESS, VariableSet.create()
                            .add("address",address)
                            .addDescribed("prefix", Messages.PREFIX));
                    return;
                }
                sender.sendMessage(Messages.COMMAND_IP_INFO_ADDRESS_DETAILS, VariableSet.create()
                        .addDescribed("block", block));
            }else{
                IpAddressInfo info = DKBans.getInstance().getIpAddressManager().getIpAddressInfo(address);

                Collection<DKBansPlayer> players = DKBans.getInstance().getPlayerManager().getPlayers(address);

                sender.sendMessage(Messages.COMMAND_IP_INFO_ADDRESS, VariableSet.create()
                        .addDescribed("info", info)
                        .addDescribed("players", players)
                        .addDescribed("address", address));
            }
        }else{
            MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
            if(player == null) return;
            DKBansPlayer dkbansPlayer = player.getAs(DKBansPlayer.class);
            sender.sendMessage(Messages.COMMAND_IP_INFO_PLAYER, VariableSet.create()
                    .addDescribed("addresses", dkbansPlayer.getIpAddresses())
                    .addDescribed("player", player));
        }
    }
}
