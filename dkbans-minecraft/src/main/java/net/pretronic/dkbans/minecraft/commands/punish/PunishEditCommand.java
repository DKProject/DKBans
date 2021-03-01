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
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.time.Duration;
import java.util.List;

public class PunishEditCommand extends BasicCommand {

    public PunishEditCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 3){
            sender.sendMessage(Messages.COMMAND_PUNISH_EDIT_HELP, VariableSet.create());
            return;
        }
        PlayerHistoryEntry entry;

        if(GeneralUtil.isNaturalNumber(arguments[0])){
            int id = Integer.parseInt(arguments[0]);
            entry = DKBans.getInstance().getHistoryManager().getHistoryEntry(id);
            if(entry == null || !entry.isActive()){
                sender.sendMessage(Messages.PUNISH_NOT_FOUND,VariableSet.create()
                        .addDescribed("id",arguments[0]));
                return;
            }
        }else{
            MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
            if(player == null) return;
            DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

            List<PlayerHistoryEntry> entries = dkBansPlayer.getHistory().getActiveEntries();
            if(entries.size() == 0){
                sender.sendMessage(Messages.PUNISH_EMPTY,VariableSet.create()
                        .addDescribed("player",player));
                return;
            }else if(entries.size() == 1){
                entry = entries.get(0);
            }else{
                sender.sendMessage(Messages.COMMAND_PUNISH_INFO_MULTIPLE,VariableSet.create()
                        .addDescribed("entries",entries)
                        .addDescribed("player",player));
                return;
            }
        }

        PlayerHistoryEntrySnapshotBuilder builder =  entry.newSnapshot(CommandUtil.getExecutor(sender));

        String action = arguments[1];
        if(action.equalsIgnoreCase("setType")){
            builder.punishmentType(PunishmentType.getPunishmentType(arguments[2]));
        }else if(action.equalsIgnoreCase("setHistory")){
            builder.historyType(DKBans.getInstance().getHistoryManager().getHistoryType(arguments[2]));
        }else if(action.equalsIgnoreCase("setReason")){
            builder.reason(CommandUtil.readStringFromArguments(arguments,2));
        }else if(action.equalsIgnoreCase("setDuration")){
            Duration duration = CommandUtil.parseDuration(sender,arguments[2]);
            if(duration == null) return;
            builder.duration(duration);
        }else if(action.equalsIgnoreCase("addDuration")){
            Duration duration = CommandUtil.parseDuration(sender,arguments[2]);
            if(duration == null) return;
            Duration newDuration = entry.getCurrent().getDuration().plus(duration);
            builder.duration(newDuration);
        }else if(action.equalsIgnoreCase("removeDuration")){
            Duration duration = CommandUtil.parseDuration(sender,arguments[2]);
            if(duration == null) return;
            Duration newDuration = entry.getCurrent().getDuration().minus(duration);
            if(newDuration.isNegative()) builder.active(false);
            else builder.duration(newDuration);
        }else if(action.equalsIgnoreCase("setStuff")){
            MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[2]);
            if(player == null) return;
            builder.staff(player.getAs(DKBansPlayer.class));
        }else{
            sender.sendMessage(Messages.COMMAND_PUNISH_EDIT_HELP, VariableSet.create());
            return;
        }
        PlayerHistoryEntrySnapshot result = builder.execute();
        sender.sendMessage(Messages.COMMAND_PUNISH_EDIT_DONE, VariableSet.create()
                .addDescribed("entry",entry)
                .addDescribed("snapshot",result));
    }
}
