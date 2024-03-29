/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.06.20, 17:45
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
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PunishNoteCommand extends BasicCommand implements Completable {

    private final static List<String> COMMANDS = Arrays.asList("list","add","clear");

    public PunishNoteCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_PUNISH_NOTES_HELP);
            return;
        }

        if(!GeneralUtil.isNaturalNumber(arguments[0])){
            sender.sendMessage(Messages.ERROR_INVALID_NUMBER,VariableSet.create().add("prefix",Messages.PREFIX));
            return;
        }

        PlayerHistoryEntry entry = DKBans.getInstance().getHistoryManager().getHistoryEntry(Integer.parseInt(arguments[0]));

        if(entry == null){
            sender.sendMessage(Messages.PUNISH_NOT_FOUND,VariableSet.create().add("id",arguments[0]));
            return;
        }

        DKBansPlayer player = entry.getHistory().getPlayer();

        String argument = arguments.length > 1 ? arguments[1] : "list";
        if(StringUtil.equalsOne(argument,"list","l")){
            int page = 1;
            if(arguments.length > 3 && GeneralUtil.isNaturalNumber(arguments[2])){
                page = Integer.parseInt(arguments[2]);
            }else if(arguments.length > 2 && GeneralUtil.isNaturalNumber(arguments[1])){
                page = Integer.parseInt(arguments[1]);
            }

            List<PlayerNote> notes = entry.getNotes().getPage(page,15);
            sender.sendMessage(Messages.COMMAND_PUNISH_NOTES_LIST, VariableSet.create()
                    .addDescribed("player",player)
                    .addDescribed("entry",entry)
                    .addDescribed("page",page)
                    .addDescribed("nextPage",page+1)
                    .addDescribed("previousPage",page == 1 ? 1 : page-1)
                    .addDescribed("notes",notes));

        }else if(StringUtil.equalsOne(argument,"add","a","create","c")){
            PlayerNote note = entry.createNote(CommandUtil.getExecutor(sender)
                    ,CommandUtil.readStringFromArguments(arguments,2));
            sender.sendMessage(Messages.COMMAND_PUNISH_NOTES_ADDED, VariableSet.create()
                    .addDescribed("entry",entry)
                    .addDescribed("note",note));
        }else if(StringUtil.equalsOne(argument,"clear","c")){
            entry.clearNotes();
            sender.sendMessage(Messages.COMMAND_PUNISH_NOTES_CLEARED, VariableSet.create()
                    .addDescribed("entry",entry)
                    .addDescribed("player",player));
        }else {
            sender.sendMessage(Messages.COMMAND_PUNISH_NOTES_HELP);
        }
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        if(args.length == 2) return COMMANDS;
        else return Collections.emptyList();
    }
}
