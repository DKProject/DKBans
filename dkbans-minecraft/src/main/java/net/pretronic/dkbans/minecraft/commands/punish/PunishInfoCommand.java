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

package net.pretronic.dkbans.minecraft.commands.punish;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.List;

public class PunishInfoCommand extends BasicCommand {

    public PunishInfoCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_PUNISH_INFO_HELP, VariableSet.create());
            return;
        }
        if(GeneralUtil.isNaturalNumber(arguments[0])){
            int id = Integer.parseInt(arguments[0]);
            PlayerHistoryEntry entry = DKBans.getInstance().getHistoryManager().getHistoryEntry(id);
            if(entry == null || !entry.isActive()){
                sender.sendMessage(Messages.COMMAND_PUNISH_INFO_NOT_FOUND,VariableSet.create()
                        .addDescribed("id",arguments[0]));
                return;
            }
            sender.sendMessage(Messages.COMMAND_HISTORY_INFO,VariableSet.create()
                    .addDescribed("player",entry.getHistory().getPlayer())
                    .addDescribed("entry",entry));
        }else{
            MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
            if(player == null) return;
            DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

            List<PlayerHistoryEntry> entries = dkBansPlayer.getHistory().getActiveEntries();

            if(entries.size() == 0){
                sender.sendMessage(Messages.COMMAND_PUNISH_INFO_EMPTY,VariableSet.create()
                        .addDescribed("player",player));
            }else if(entries.size() == 1){
                PlayerHistoryEntry entry = entries.get(0);
                sender.sendMessage(Messages.COMMAND_HISTORY_INFO,VariableSet.create()
                        .addDescribed("player",player)
                        .addDescribed("entry",entry));
            }else{
                sender.sendMessage(Messages.COMMAND_PUNISH_INFO_MULTIPLE,VariableSet.create()
                        .addDescribed("entries",entries)
                        .addDescribed("player",player));
            }
        }
    }
}
