/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 30.06.20, 15:32
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

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

public class ReportTakeCommand extends BasicCommand {

    public ReportTakeCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder().name("take").permission(Permissions.COMMAND_REPORT_STUFF).create());
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(!(sender instanceof OnlineMinecraftPlayer)) {
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        if(arguments.length == 0) {
            sender.sendMessage(Messages.COMMAND_REPORT_TAKE_USAGE);
            return;
        }
        DKBansPlayer staff = ((OnlineMinecraftPlayer) sender).getAs(DKBansPlayer.class);

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
        if(player == null) return;

        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

        PlayerReport report = dkBansPlayer.getReport();
        if(report == null){
            sender.sendMessage(Messages.COMMAND_REPORT_NOT_REPORTED, VariableSet.create()
                    .addDescribed("target", player));
            return;
        }

        if(report.isWatched()) {
            sender.sendMessage(Messages.COMMAND_REPORT_TAKE_ALREADY, VariableSet.create()
                    .addDescribed("target", player)
                    .addDescribed("report", report));
        } else {
            report.setWatcher(staff);
            sender.sendMessage(Messages.COMMAND_REPORT_TAKE, VariableSet.create()
                    .addDescribed("target", player)
                    .addDescribed("report", report));
        }
    }
}
