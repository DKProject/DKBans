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

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

public class OnlineTimeCommand extends BasicCommand {

    public OnlineTimeCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length == 1){
            if(sender instanceof MinecraftPlayer){
                DKBansPlayer player = ((MinecraftPlayer) sender).getAs(DKBansPlayer.class);
                sender.sendMessage(Messages.ONLINE_TIME_SELF, VariableSet.create()
                        .add("online-time-formatted-short", DurationProcessor.getStandard().formatShort(player.getOnlineTime()))
                        .add("online-time-formatted", DurationProcessor.getStandard().format(player.getOnlineTime()))
                        .addDescribed("player",player));
            }else{
                sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            }
        }else{
            MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(arguments[0]);
            if(player == null){
                sender.sendMessage(Messages.PLAYER_NOT_FOUND, VariableSet.create()
                        .add("prefix",Messages.PREFIX_NETWORK)
                        .add("name",arguments[0]));
                return;
            }
            DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

            sender.sendMessage(Messages.ONLINE_TIME_OTHER, VariableSet.create()
                    .add("online-time-formatted-short", DurationProcessor.getStandard().formatShort(dkBansPlayer.getOnlineTime()))
                    .add("online-time-formatted", DurationProcessor.getStandard().format(dkBansPlayer.getOnlineTime()))
                    .addDescribed("player",player));
        }
    }
}
