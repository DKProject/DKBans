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

import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
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
            //@Todo send help
            return;
        }
        if(sender instanceof OnlineMinecraftPlayer){
            OnlineMinecraftPlayer player = (OnlineMinecraftPlayer) sender;
            String action = arguments[0];
            boolean current = player.hasSetting("DKBans","TeamChatLogin",true);

            if(StringUtil.equalsOne(action,"logout","out")) changeLogin(player,current,false);
            else if(StringUtil.equalsOne(action,"login","in")) changeLogin(player,current,true);
            else if(StringUtil.equalsOne(action,"toggle","tog"))changeLogin(player,current,!current);
            else if(sender.hasPermission(Permissions.COMMAND_TEAMCHAT_SEND)){
                //@Todo send message for player
            }else{
                //@Todo no permissions
            }
        }else{
            //@Todo send message for console
        }
    }
    private void changeLogin(OnlineMinecraftPlayer player, boolean current, boolean action){
        if(current == action){
            player.sendMessage(Messages.STAFF_STATUS_ALREADY, VariableSet.create()
                    .add("status",action)
                    .add("statusFormatted", action ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
        }else{
            player.sendMessage(Messages.STAFF_STATUS_CHANGE, VariableSet.create()
                    .add("status",action)
                    .add("statusFormatted", action ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
            player.setSetting("DKBans","TeamChatLogin",action);
        }
    }
}
