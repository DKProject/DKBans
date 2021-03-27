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
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class OnlineTimeCommand extends BasicCommand implements Completable {

    public OnlineTimeCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length == 0 || !sender.hasPermission("dkbans.onlinetime.other")){
            if(sender instanceof MinecraftPlayer){
                DKBansPlayer player = ((MinecraftPlayer) sender).getAs(DKBansPlayer.class);
                long time = TimeUnit.MILLISECONDS.toSeconds(player.getOnlineTime());
                sender.sendMessage(Messages.ONLINE_TIME_SELF, VariableSet.create()
                        .add("online-time-formatted-short", DurationProcessor.getStandard().formatShort(time))
                        .add("online-time-formatted", DurationProcessor.getStandard().format(time))
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
            long time = TimeUnit.MILLISECONDS.toSeconds(dkBansPlayer.getOnlineTime());
            sender.sendMessage(Messages.ONLINE_TIME_OTHER, VariableSet.create()
                    .add("online-time-formatted-short", DurationProcessor.getStandard().formatShort(time))
                    .add("online-time-formatted", DurationProcessor.getStandard().format(time))
                    .addDescribed("player",player));
        }
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        if(sender.hasPermission("dkbans.onlinetime.other")) return CommandUtil.completePlayer(args);
        return Collections.emptyList();
    }
}
