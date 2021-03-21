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

package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PunishNotifyCommand extends BasicCommand implements Completable {

    private final static List<String> COMMANDS = Arrays.asList("login","logout","toggle");

    public PunishNotifyCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_NOTIFY_HELP);
            return;
        }
        if(!(sender instanceof MinecraftPlayer)){
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        String argument = arguments[0];

        OnlineMinecraftPlayer player = (OnlineMinecraftPlayer) sender;
        boolean current = player.hasSetting("DKBans", PlayerSettingsKey.PUNISH_NOTIFY_LOGIN,true);

        if(StringUtil.equalsOne(argument,"logout","out")){
            changeLogin(player,current,false);
        }else if(StringUtil.equalsOne(argument,"login","in")){
            changeLogin(player,current,true);
        }else if(StringUtil.equalsOne(argument,"toggle","tog")){
            changeLogin(player,current,!current);
        }else{
            sender.sendMessage(Messages.COMMAND_NOTIFY_HELP);
        }
    }

    private void changeLogin(OnlineMinecraftPlayer player, boolean current, boolean action){
        CommandUtil.changeLogin(Messages.PREFIX, PlayerSettingsKey.PUNISH_NOTIFY_LOGIN,player,current,action);
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        return CommandUtil.completeSimple(COMMANDS,args);
    }
}
