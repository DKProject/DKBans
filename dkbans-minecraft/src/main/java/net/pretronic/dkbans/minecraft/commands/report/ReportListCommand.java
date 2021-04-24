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

package net.pretronic.dkbans.minecraft.commands.report;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.player.MinecraftPlayer;

import java.util.Comparator;
import java.util.List;

public class ReportListCommand extends BasicCommand {

    public ReportListCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("list")
                .permission(CommandConfig.PERMISSION_COMMAND_REPORT_STAFF).create());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof MinecraftPlayer)) {
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        List<PlayerReport> reports = DKBans.getInstance().getReportManager().getNewReports();
        reports.sort(Comparator.comparingInt(PlayerReport::getCount));

        int page = 1;
        try {
            if(args.length == 1) page = Convert.toInteger(args[0]);
        } catch (IllegalArgumentException ignored) {}

        List<PlayerReport> shownReports = GeneralUtil.getItemsOnPage(reports, page, 15);
        sender.sendMessage(Messages.COMMAND_REPORT_LIST, VariableSet.create().addDescribed("reports", shownReports));
    }
}
