/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 29.06.20, 17:08
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

package net.pretronic.dkbans.minecraft.commands.unpunish;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

public class UnpunishCommand extends BasicCommand {

    private final PunishmentType type;

    public UnpunishCommand(ObjectOwner owner, CommandConfiguration configuration, PunishmentType type) {
        super(owner, configuration);
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_UNPUNISH_HELP, VariableSet.create()
                    .add("command",getConfiguration().getName()));
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
        if(player == null) return;

        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

        if(!dkBansPlayer.hasActivePunish(type)){
            CommandUtil.sendNotPunished(sender,dkBansPlayer,type);
            return;
        }

        String reason = "Unknown";
        if(arguments.length >= 2) reason = CommandUtil.readStringFromArguments(arguments,1);

        PlayerHistoryEntrySnapshot result = dkBansPlayer.unpunish(CommandUtil.getExecutor(sender),type,reason);
        CommandUtil.sendUnpunishResultMessage(sender,result);
    }
}
