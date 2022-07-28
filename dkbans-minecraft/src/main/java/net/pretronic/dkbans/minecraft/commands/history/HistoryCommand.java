/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 11.07.20, 12:46
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

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.bukkit.entity.Player;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.Collection;
import java.util.List;

public class HistoryCommand extends BasicCommand implements Completable {

    public HistoryCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_HISTORY_HELP);
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
        if(player == null) return;
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

        PlayerHistory history = dkBansPlayer.getHistory();

        int page = 0;

        if(arguments.length > 1){

            if(arguments[1].startsWith("--page:")) {

                try {
                    page = Integer.parseInt(arguments[1].split(":")[1]);
                } catch (NumberFormatException e) {
                    //ignore
                }
                this.sendHistoryMessage(sender, player, page, history.getEntries());
                return;
            }

            if(!GeneralUtil.isNaturalNumber(arguments[1])) {
                sender.sendMessage(Messages.ERROR_INVALID_NUMBER,VariableSet.create()
                        .add("prefix",Messages.PREFIX));
               return;
            }

            int id = Integer.parseInt(arguments[1]);

            PlayerHistoryEntry entry = history.getEntry(id);

            if(entry == null){
                sender.sendMessage(Messages.COMMAND_HISTORY_ENTRY_NOT_FOUND);
                return;
            }

            if(arguments.length > 2){
                if(arguments[2].equalsIgnoreCase("changes")){
                    sender.sendMessage(Messages.COMMAND_HISTORY_VERSION_LIST,VariableSet.create()
                            .addDescribed("player",player)
                            .addDescribed("snapshots",entry.getAll())
                            .addDescribed("entry",entry));
                }else if(!GeneralUtil.isNaturalNumber(arguments[2])){
                    sender.sendMessage(Messages.ERROR_INVALID_NUMBER,VariableSet.create()
                            .add("prefix",Messages.PREFIX));
                }else{
                    int snapshotId = Integer.parseInt(arguments[2]);
                    PlayerHistoryEntrySnapshot snapshot = entry.get(snapshotId);
                    if(snapshot == null){
                        sender.sendMessage(Messages.COMMAND_HISTORY_ENTRY_NOT_FOUND);
                        return;
                    }

                    sender.sendMessage(Messages.COMMAND_HISTORY_VERSION_INFO,VariableSet.create()
                            .addDescribed("player",player)
                            .addDescribed("snapshot",snapshot)
                            .addDescribed("entry",entry));
                }
            }else{
                sender.sendMessage(Messages.COMMAND_HISTORY_INFO,VariableSet.create()
                        .addDescribed("player",player)
                        .addDescribed("entry",entry));
            }
        }else{
            this.sendHistoryMessage(sender, player, page, history.getEntries());
        }
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        return CommandUtil.completePlayer(args);
    }

    private void sendHistoryMessage(CommandSender sender, MinecraftPlayer player, int page, List<PlayerHistoryEntry> entries) {

        int maxEntries;

        if(page != 0)
            maxEntries = page * 22;
        else
            maxEntries = 22;

        boolean hasNextPage = entries.size() > maxEntries;
        boolean hasPreviousPage = page != 0;

        //remove all irrelevant entries
        entries.removeIf(entry -> entries.indexOf(entry) < (maxEntries - 22) || entries.indexOf(entry) > maxEntries);

        //send the message
        sender.sendMessage(Messages.COMMAND_HISTORY_LIST, VariableSet.create()
                .add("prefix",Messages.PREFIX)
                .addDescribed("hasPages", hasNextPage || hasPreviousPage)
                .addDescribed("previous", page-1)
                .addDescribed("next", page+1)
                .addDescribed("hasNext", hasNextPage)
                .addDescribed("hasPrevious", hasPreviousPage)
                .addDescribed("player", player)
                .addDescribed("entries", entries));
    }
}
