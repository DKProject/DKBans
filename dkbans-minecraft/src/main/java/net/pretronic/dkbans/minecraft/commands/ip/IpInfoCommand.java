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
import net.pretronic.dkbans.api.player.ipblacklist.IpAddressBlock;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class IpInfoCommand extends BasicCommand {

    public IpInfoCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 1) {
            sender.sendMessage(Messages.COMMAND_IP_INFO_USAGE);
            return;
        }
        String ipAddress = args[0];
        IpAddressBlock ipAddressBlock = DKBans.getInstance().getIpAddressBlacklistManager().getIpAddressBlock(ipAddress);
        if(ipAddressBlock == null) {
            sender.sendMessage(Messages.COMMAND_IP_INFO_NOT_BLOCKED, VariableSet.create()
                    .add("address", ipAddress));
            return;
        }
        sender.sendMessage(Messages.COMMAND_IP_INFO_BLOCKED, VariableSet.create()
                .addDescribed("block", ipAddressBlock));
    }
}