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

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

import java.time.Duration;

public class TemporaryPunishCommand extends BasicCommand {

    private final PunishmentType punishmentType;
    private final PlayerHistoryType historyType;
    private final DKBansScope scope;

    public TemporaryPunishCommand(ObjectOwner owner, CommandConfiguration configuration
            , PunishmentType punishmentType, PlayerHistoryType historyType, DKBansScope scope) {
        super(owner, configuration);
        this.punishmentType = punishmentType;
        this.historyType = historyType;
        this.scope = scope;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length <= 2){
            sender.sendMessage(Messages.COMMAND_PUNISH_HELP_TEMPORARY, VariableSet.create()
                    .add("command",getConfiguration().getName()));
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0],true);
        if(player == null) return;

        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);
        if(CommandUtil.checkBypass(sender,dkBansPlayer)) return;


        boolean override = arguments[2].equalsIgnoreCase("--override");
        String reason = CommandUtil.readStringFromArguments(arguments,override ? 3 : 2);

        if(dkBansPlayer.hasActivePunish(punishmentType,scope)){
            if(override && CommandUtil.canOverridePunish(sender,dkBansPlayer,punishmentType)){
                dkBansPlayer.unpunish(CommandUtil.getExecutor(sender),punishmentType,"Overriding punishment with a new one");
            }else{
                String command = "/"+getConfiguration().getName()+" "+arguments[0]+" "+arguments[1]+" --override "+reason;
                CommandUtil.sendAlreadyPunished(sender,dkBansPlayer,punishmentType,command);
                return;
            }
        }

        Duration duration = CommandUtil.parseDuration(sender,arguments[1]);
        if(sender instanceof OnlineMinecraftPlayer &&  CommandUtil.checkMaximumAllowedDuration((OnlineMinecraftPlayer) sender,punishmentType,duration));

        PlayerHistoryEntrySnapshot result = dkBansPlayer.punish()
                .staff(CommandUtil.getExecutor(sender))
                .punishmentType(punishmentType)
                .historyType(historyType)
                .scope(scope)
                .duration(duration)
                .reason(reason)
                .execute();
        CommandUtil.sendPunishResultMessage(sender,dkBansPlayer,result);
    }
}
