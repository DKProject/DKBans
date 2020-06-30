/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 30.06.20, 16:32
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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.OnlineMinecraftPlayer;

public class ReportAcceptCommand extends BasicCommand {

    public ReportAcceptCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder().name("accept").permission(Permissions.COMMAND_REPORT_STUFF).create());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof OnlineMinecraftPlayer)) {
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        if(args.length == 0) {
            sender.sendMessage(Messages.COMMAND_REPORT_ACCEPT_USAGE);
            return;
        }
        OnlineMinecraftPlayer target = McNative.getInstance().getLocal().getOnlinePlayer(args[0]);
        if(target == null) {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND);
            return;
        }

        PlayerReport report = DKBans.getInstance().getReportManager().getReport(target.getAs(DKBansPlayer.class));
        if(report == null) {
            sender.sendMessage(Messages.REPORT_NOT_FOUND, VariableSet.create().addDescribed("player", target));
            return;
        }
        DKBansPlayer player = ((OnlineMinecraftPlayer)sender).getAs(DKBansPlayer.class);

        if(!player.equals(report.getWatcher())) {
            sender.sendMessage(Messages.COMMAND_REPORT_ACCEPT_NOT_WATCHING);
            return;
        }
        report.accept(player);
        //@Todo message ...
    }
}
