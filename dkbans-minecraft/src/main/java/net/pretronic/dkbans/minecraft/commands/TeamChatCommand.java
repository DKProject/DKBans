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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.minecraft.BroadcastMessageChannels;
import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.OnlineMinecraftPlayer;

public class TeamChatCommand extends BasicCommand {

    public TeamChatCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_TEAM_CHAT_HELP);
            return;
        }
        if(sender instanceof OnlineMinecraftPlayer){
            OnlineMinecraftPlayer player = (OnlineMinecraftPlayer) sender;
            String action = arguments[0];
            boolean current = player.hasSetting("DKBans", PlayerSettingsKey.TEAM_CHAT_LOGIN,true);

            if(StringUtil.equalsOne(action,"logout","out")) changeLogin(player,current,false);
            else if(StringUtil.equalsOne(action,"login","in")) changeLogin(player,current,true);
            else if(StringUtil.equalsOne(action,"toggle","tog")) changeLogin(player,current,!current);
            else if(sender.hasPermission(Permissions.COMMAND_TEAMCHAT_SEND)){
                String message = CommandUtil.readStringFromArguments(arguments,0);
                DKBans.getInstance().broadcastMessage(BroadcastMessageChannels.TEAM_CHAT,CommandUtil.getExecutor(sender),message);
            }else{
                sender.sendMessage(Messages.ERROR_NO_PERMISSIONS);
            }
        }else{
            String message = CommandUtil.readStringFromArguments(arguments,0);
            DKBans.getInstance().broadcastMessage(BroadcastMessageChannels.TEAM_CHAT,CommandUtil.getExecutor(sender),message);
        }
    }

    private void changeLogin(OnlineMinecraftPlayer player, boolean current, boolean action){
        CommandUtil.changeLogin(Messages.PREFIX_TEAMCHAT,PlayerSettingsKey.TEAM_CHAT_LOGIN,player,current,action);
    }

}
