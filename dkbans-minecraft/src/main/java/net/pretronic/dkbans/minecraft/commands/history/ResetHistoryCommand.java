/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.07.20, 11:35
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

package net.pretronic.dkbans.minecraft.commands.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

import java.time.Duration;

public class ResetHistoryCommand extends BasicCommand {

    public ResetHistoryCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    //last 10     //last 10d // by <staff>
    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_RESET_HISTORY_HELP);
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0],true);
        if(player == null) return;

        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);
        PlayerHistory history = dkBansPlayer.getHistory();

        if(arguments.length >= 2){
            if(arguments.length >= 3){
                String action = arguments[1];
                if(StringUtil.equalsOne(action,"last","l")){

                    if(GeneralUtil.isNaturalNumber(arguments[2])){
                        history.clearLast(Integer.parseInt(arguments[2]));//history player last 10
                    }else{
                        Duration duration = CommandUtil.parseDuration(sender,arguments[2]);
                        if(duration == null) return;
                        history.clearLast(duration);
                    }
                    sender.sendMessage(Messages.COMMAND_RESET_HISTORY_MANY, VariableSet.create()
                            .addDescribed("player",dkBansPlayer));
                }else if(StringUtil.equalsOne(action,"by")){
                    DKBansExecutor executor = DKBans.getInstance().getPlayerManager().getExecutor(arguments[2]);
                    if(executor != null){
                        history.clearByStaff(executor);
                        sender.sendMessage(Messages.COMMAND_RESET_HISTORY_MANY, VariableSet.create()
                                .addDescribed("player",dkBansPlayer));
                    }
                }

            }else{
                if(GeneralUtil.isNaturalNumber(arguments[1])){
                    int id = Integer.parseInt(arguments[1]);
                    history.delete(id);
                    sender.sendMessage(Messages.COMMAND_RESET_HISTORY_ONE, VariableSet.create()
                            .add("id",id)
                            .addDescribed("player",dkBansPlayer));
                }else{
                    sender.sendMessage(Messages.COMMAND_RESET_HISTORY_HELP);
                }
            }
        }else{
            history.clear();
            sender.sendMessage(Messages.COMMAND_RESET_HISTORY_MANY, VariableSet.create()
                    .addDescribed("player",dkBansPlayer));
        }
    }
}
