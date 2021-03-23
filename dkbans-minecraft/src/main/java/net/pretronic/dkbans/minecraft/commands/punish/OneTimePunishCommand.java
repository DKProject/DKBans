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
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.Collection;

public class OneTimePunishCommand extends BasicCommand implements Completable {

    private final PunishmentType punishmentType;
    private final PlayerHistoryType historyType;
    private final DKBansScope scope;

    public OneTimePunishCommand(ObjectOwner owner, CommandConfiguration configuration
            , PunishmentType punishmentType, PlayerHistoryType historyType, DKBansScope scope) {
        super(owner, configuration);
        this.punishmentType = punishmentType;
        this.historyType = historyType;
        this.scope = scope;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length <= 1){
            sender.sendMessage(Messages.COMMAND_PUNISH_HELP_ONE_TIME, VariableSet.create()
                    .add("command",getConfiguration().getName()));
            return;
        }
        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0],true);
        if(player == null) return;

        if(!player.isOnline()){
            sender.sendMessage(Messages.PLAYER_NOT_ONLINE, VariableSet.create()
                    .add("prefix",Messages.PREFIX)
                    .addDescribed("player",player));
            return;
        }

        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);
        if(CommandUtil.checkBypass(sender,dkBansPlayer)) return;

        String reason = CommandUtil.readStringFromArguments(arguments,1);
        PlayerHistoryEntrySnapshot snapshot = dkBansPlayer.punish()
                .scope(scope)
                .staff(CommandUtil.getExecutor(sender))
                .punishmentType(punishmentType)
                .historyType(historyType)
                .reason(reason)
                .execute();
        CommandUtil.sendPunishResultMessage(sender,dkBansPlayer,snapshot);
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        return CommandUtil.completePlayer(args);
    }
}
