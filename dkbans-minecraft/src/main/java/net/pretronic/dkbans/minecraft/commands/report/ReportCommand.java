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
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.NotFindable;
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.OnlineMinecraftPlayer;

import java.util.Comparator;
import java.util.List;

/*
report <login, logout, toggle> -
report take <name>
report list

report <name> <reason> -
report accept <name> <duration> <reason>
report other <name>
report deny <name>
 */
public class ReportCommand extends MainCommand implements NotFindable {

    private final TemplateGroup templateGroup;

    public ReportCommand(ObjectOwner owner, CommandConfiguration configuration, String templateGroupName) {
        super(owner, configuration);
        if(templateGroupName != null) {
            TemplateGroup templateGroup0 = DKBans.getInstance().getTemplateManager().getTemplateGroup(templateGroupName);
            if(templateGroup0 == null) {
                throw new IllegalArgumentException("Can't register report template command. Template group does't exist");
            }
            this.templateGroup = templateGroup0;
        } else {
            this.templateGroup = null;
        }
        registerCommand(new ReportLoginCommand(owner));
        registerCommand(new ReportLogoutCommand(owner));
        registerCommand(new ReportToggleCommand(owner));
        registerCommand(new ReportTakeCommand(owner));
        registerCommand(new ReportAcceptCommand(owner));
        registerCommand(new ReportDeclineCommand(owner));
        registerCommand(new ReportListCommand(owner));
    }

    @Override
    public void commandNotFound(CommandSender sender, String playerIdentifier, String[] args) {
        if(!(sender instanceof OnlineMinecraftPlayer)) {
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }

        OnlineMinecraftPlayer player = (OnlineMinecraftPlayer) sender;
        if(playerIdentifier == null || playerIdentifier.length() == 0) {
            list(player);
            return;
        }

        MinecraftPlayer target0 = CommandUtil.getPlayer(sender,playerIdentifier,true);
        if(target0 == null) return;

        OnlineMinecraftPlayer target = target0.getAsOnlinePlayer();
        if(target == null) {
            player.sendMessage(Messages.PLAYER_NOT_ONLINE,VariableSet.create()
                    .add("name",playerIdentifier)
                    .addDescribed("player",target0)
                    .add("prefix",Messages.PREFIX_REPORT));
            return;
        }
        if(args.length < 1) {
            list(player);
            return;
        }
        String reason = args[0];
        if(this.templateGroup != null) {
            Template template = this.templateGroup.getTemplate(reason);
            if(!(template instanceof ReportTemplate)) {
                player.sendMessage(Messages.COMMAND_REPORT_TEMPLATE_NOT_EXIST, VariableSet.create()
                        .add("name", reason));
                return;
            }
            PlayerReportEntry reportEntry = target.getAs(DKBansPlayer.class).report(player.getAs(DKBansPlayer.class), (ReportTemplate) template);
            report(player, target, reportEntry);
        } else {
            PlayerReportEntry reportEntry = target.getAs(DKBansPlayer.class).report(player.getAs(DKBansPlayer.class), reason);
            report(player, target, reportEntry);
        }
    }

    private void list(OnlineMinecraftPlayer player) {
        if(this.templateGroup == null) {
            player.sendMessage(Messages.COMMAND_REPORT_HELP);
        } else {
            List<Template> availableTemplates = Iterators.filter(this.templateGroup.getTemplates(), template -> template instanceof ReportTemplate
                    && player.hasPermission(template.getPermission()));
            availableTemplates.sort(Comparator.comparingInt(Template::getInGroupId));
            player.sendMessage(Messages.COMMAND_REPORT_LIST_TEMPLATE, VariableSet.create().addDescribed("templates", availableTemplates));
        }
    }

    private void report(OnlineMinecraftPlayer player, OnlineMinecraftPlayer target, PlayerReportEntry reportEntry) {
        if(reportEntry == null) {
            player.sendMessage(Messages.COMMAND_REPORT_ALREADY_REPORTED, VariableSet.create().addDescribed("player", target));
            return;
        }
        String reason;
        if(reportEntry.getTemplate() != null) {
            reason = reportEntry.getTemplate().getDisplayName();
        } else {
            reason = reportEntry.getReason();
        }
        player.sendMessage(Messages.COMMAND_REPORT_REPORTED, VariableSet.create()
                .addDescribed("player", target)
                .add("reason", reason));
    }
}
