/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.07.20, 20:02
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
import net.pretronic.dkbans.api.joinme.JoinMe;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.network.component.server.MinecraftServer;
import org.mcnative.common.player.OnlineMinecraftPlayer;
import org.mcnative.common.player.PlayerSetting;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JoinMeCommand extends BasicCommand {

    public JoinMeCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof OnlineMinecraftPlayer)){
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        OnlineMinecraftPlayer player = (OnlineMinecraftPlayer) sender;
        if(args.length == 0) {
            if (!player.hasPermission(Permissions.COMMAND_JOINME)) {
                PlayerSetting setting = player.getSetting("DKBans", PlayerSettingsKey.JOINME_AMOUNT);
                if (setting == null || setting.getIntValue() == 0) {
                    player.sendMessage(Messages.COMMAND_JOINME_NOT_ENOUGH_AMOUNT);
                    return;
                }
                player.setSetting("DKBans", PlayerSettingsKey.JOINME_AMOUNT, setting.getIntValue() - 1);
            }
            sendJoinMe(player);
        } else if(args.length == 1) {
            try {
                UUID joinMeCreator = UUID.fromString(args[0]);

                JoinMe joinMe = DKBans.getInstance().getJoinMeManager().getJoinMe(joinMeCreator);
                if(joinMe == null){
                    player.sendMessage(Messages.COMMAND_JOINME_NOT_EXIST);
                    return;
                }

                MinecraftServer server = McNative.getInstance().getNetwork().getServer(joinMe.getServer());
                if(server == null) return;

                if(joinMe.getServer().equals(((OnlineMinecraftPlayer) sender).getServer().getName())){
                    sender.sendMessage(Messages.SERVER_ALREADY_CONNECTED, VariableSet.create()
                            .add("prefix",Messages.PREFIX_NETWORK)
                            .addDescribed("player",player)
                            .addDescribed("server",server));
                    return;
                }

                sender.sendMessage(Messages.SERVER_CONNECTING, VariableSet.create()
                        .add("prefix",Messages.PREFIX_NETWORK)
                        .addDescribed("player",player)
                        .addDescribed("server",server));

                player.connect(server);
            } catch (IllegalArgumentException exception) {
                player.sendMessage(Messages.COMMAND_JOINME_NOT_EXIST);
            }
        } else {
            player.sendMessage(Messages.COMMAND_JOINME_USAGE);
        }
    }

    private void sendJoinMe(OnlineMinecraftPlayer player) {
        DKBans.getInstance().getJoinMeManager().sendJoinMe(player.getAs(DKBansPlayer.class), player.getServer().getName(), 5*1000*60);
    }
}
