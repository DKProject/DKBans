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

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

//OneTimePunish
public class KickCommand extends BasicCommand {

    public KickCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length <= 1){
            sender.sendMessage(Messages.COMMAND_KICK_HELP);
            return;
        }
        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0],true);
        if(player == null) return;

        if(!player.isOnline()){
            sender.sendMessage(Messages.PLAYER_NOT_ONLINE, VariableSet.create().addDescribed("player",player));
            return;
        }

        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);
        if(CommandUtil.checkBypass(sender,dkBansPlayer)) return;

        String reason = CommandUtil.readStringFromArguments(arguments,1);
        PlayerHistoryEntrySnapshot snapshot = dkBansPlayer.kick(CommandUtil.getExecutor(sender),reason);
        CommandUtil.sendPunishResultMessage(sender,snapshot);
    }
}
