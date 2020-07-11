/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 18:52
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

package net.pretronic.dkbans.minecraft.commands.report;

import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.OnlineMinecraftPlayer;
import org.mcnative.common.player.PlayerSetting;

public class ReportToggleCommand extends BasicCommand {

    public ReportToggleCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("toggle")
                .permission(Permissions.COMMAND_REPORT_STUFF).create());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof OnlineMinecraftPlayer)) {
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        OnlineMinecraftPlayer player = (OnlineMinecraftPlayer) sender;

        boolean current = player.hasSetting("DKBans", PlayerSettingsKey.REPORT_CHAT_LOGIN,true);
        changeLogin(player, current,  !current);
    }


    private void changeLogin(OnlineMinecraftPlayer player, boolean current, boolean action){
        CommandUtil.changeLogin(Messages.PREFIX_REPORT,PlayerSettingsKey.REPORT_CHAT_LOGIN,player,current,action);
    }
}
