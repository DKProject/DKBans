/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 17:34
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
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.common.player.OnlineMinecraftPlayer;

public class ReportAcceptCommand extends BasicCommand {

    public ReportAcceptCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder().name("accept").permission(Permissions.COMMAND_REPORT_STUFF).create());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length == 0) {
            sender.sendMessage(Messages.COMMAND_REPORT_ACCEPT_USAGE);
            return;
        }

        Pair<OnlineMinecraftPlayer, PlayerReport> data = CommandUtil.checkAndGetTargetReport(sender, args[0]);
        if(data != null) {
            PlayerReport report = data.getValue();
            DKBansPlayer player = ((OnlineMinecraftPlayer)sender).getAs(DKBansPlayer.class);

            if(!player.equals(report.getWatcher())) {
                sender.sendMessage(Messages.COMMAND_REPORT_ACCEPT_NOT_WATCHING);
                return;
            }
            sender.sendMessage(Messages.COMMAND_REPORT_ACCEPT_LIST_ENTRIES, VariableSet.create()
                    .addDescribed("entries", report.getEntries()));
        }
    }
}