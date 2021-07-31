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
import net.pretronic.dkbans.minecraft.commands.util.CommandUtil;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.Completable;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.StringUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class TeamChatCommand extends BasicCommand implements Completable {

    private final static List<String> COMMANDS = Arrays.asList("login","logout","toggle","list");

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
            else if(StringUtil.equalsOne(action,"list","l")) sendOnlineList(player);
            else if(sender.hasPermission(CommandConfig.PERMISSION_COMMAND_TEAMCHAT_SEND)){
                if(!current){
                    player.sendMessage(Messages.STAFF_STATUS_NOT, VariableSet.create()
                            .add("prefix",Messages.PREFIX_TEAMCHAT)
                            .add("status",action)
                            .add("statusFormatted", Messages.STAFF_STATUS_LOGIN));
                    return;
                }

                String message = CommandUtil.readStringFromArguments(arguments,0);
                DKBans.getInstance().broadcastMessage(BroadcastMessageChannels.TEAM_CHAT,CommandUtil.getExecutor(sender),message);
            }else{
                sender.sendMessage(Messages.ERROR_NO_PERMISSIONS,VariableSet.create()
                        .add("prefix",Messages.PREFIX_TEAMCHAT));
            }
        }else{
            String message = CommandUtil.readStringFromArguments(arguments,0);
            DKBans.getInstance().broadcastMessage(BroadcastMessageChannels.TEAM_CHAT,CommandUtil.getExecutor(sender),message);
        }
    }

    @Override
    public Collection<String> complete(CommandSender sender, String[] args) {
        return CommandUtil.completeSimple(COMMANDS,args);
    }

    private void changeLogin(OnlineMinecraftPlayer player, boolean current, boolean action){
        CommandUtil.changeLogin(Messages.PREFIX_TEAMCHAT,PlayerSettingsKey.TEAM_CHAT_LOGIN,player,current,action);
    }

    private void sendOnlineList(OnlineMinecraftPlayer player){
        Collection<OnlineMinecraftPlayer> players;
        if(McNative.getInstance().isNetworkAvailable()){
            players = McNative.getInstance().getNetwork().getOnlinePlayers();
        }else{
            players = McNative.getInstance().getLocal().getOnlinePlayers();
        }

        List<OnlineMinecraftPlayer> members = Iterators.filter(players, player1 ->
                player1.hasPermission(CommandConfig.PERMISSION_COMMAND_TEAMCHAT_RECEIVE)
                && player1.hasSetting("DKBans", PlayerSettingsKey.TEAM_CHAT_LOGIN,true));
        members.sort(Comparator.comparingInt(o -> o.getDesign().getPriority()));

        player.sendMessage(Messages.TEAMCHAT_LIST,VariableSet.create().addDescribed("players",members));

    }


}
