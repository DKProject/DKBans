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

package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlayerSessionsCommand extends BasicCommand implements Completable {

    public PlayerSessionsCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_PLAYER_SESSIONS_HELP);
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
        if(player == null) return;
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

        int page = 1;
        if(arguments.length > 1 && GeneralUtil.isNaturalNumber(arguments[1])){
            page = Integer.parseInt(arguments[1]);
        }

        List<PlayerSession> sessions = dkBansPlayer.getSessions().getPage(page,10);
        sender.sendMessage(Messages.COMMAND_PLAYER_SESSIONS_INFO, VariableSet.create()
                .add("page",page)
                .addDescribed("player",player)
                .addDescribed("sessions",sessions));
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        return CommandUtil.completePlayer(args);
    }
}
