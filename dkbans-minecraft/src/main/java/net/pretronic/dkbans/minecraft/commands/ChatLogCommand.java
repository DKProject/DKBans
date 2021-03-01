/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 15:48
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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.chatlog.ChatLog;
import net.pretronic.dkbans.api.player.chatlog.ChatLogEntry;
import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.api.player.chatlog.ServerChatLog;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.util.List;
import java.util.UUID;

/*
- chatlog player <name> 1,2
- chatlog server <name, id>
 */
public class ChatLogCommand extends BasicCommand {

    public ChatLogCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Messages.COMMAND_CHATLOG_USAGE);
            return;
        }
        if(args[0].equalsIgnoreCase("player")) {
            String playerName = args[1];
            MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(playerName);
            if(player == null) {
                sender.sendMessage(Messages.PLAYER_NOT_FOUND, VariableSet.create()
                        .add("name", playerName));
                return;
            }
            ChatLog chatLog = DKBans.getInstance().getChatLogManager().getPlayerChatLog(player.getUniqueId());
            printChatLog(sender, chatLog, args,Messages.COMMAND_CHATLOG_PLAYER_LIST);
        } else if(args[0].equalsIgnoreCase("server")) {
            ChatLog chatLog;
            String server0 = args[1];
            try {
                UUID serverId = Convert.toUUID(server0);
                chatLog = DKBans.getInstance().getChatLogManager().getServerChatLog(serverId);
            } catch (Exception ignored) {
                chatLog = DKBans.getInstance().getChatLogManager().getServerChatLog(server0);
            }
            printChatLog(sender, chatLog, args,Messages.COMMAND_CHATLOG_SERVER_LIST);
        } else {
            sender.sendMessage(Messages.COMMAND_CHATLOG_USAGE);
        }
    }

    private void printChatLog(CommandSender sender, ChatLog chatLog, String[] args, MessageComponent<?> message) {
        int page = 1;
        if(args.length == 3) {
            page = Convert.toInteger(args[2]);
        }
        List<ChatLogEntry> entries = chatLog.getPage(page, 10);
        VariableSet variableSet = VariableSet.create()
                .add("page",page)
                .add("prefix",Messages.PREFIX_CHAT)
                .addDescribed("entries", entries);
        if(chatLog instanceof PlayerChatLog) {
            variableSet.addDescribed("player", ((PlayerChatLog)chatLog).getPlayer());
        } else if(chatLog instanceof ServerChatLog) {
            variableSet.add("serverName", ((ServerChatLog)chatLog).getServerName())
                    .add("serverId", ((ServerChatLog)chatLog).getServerId().toString());
        }
        sender.sendMessage(message, variableSet);
    }
}
