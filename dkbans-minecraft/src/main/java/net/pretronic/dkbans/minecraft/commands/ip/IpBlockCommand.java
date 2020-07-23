/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.07.20, 19:00
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

package net.pretronic.dkbans.minecraft.commands.ip;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.ipblacklist.IpAddressBlock;
import net.pretronic.dkbans.api.player.ipblacklist.IpAddressBlockType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.regex.Pattern;

/*
/ipblock <address> <type> <reason> <duration> <forReason> <forDuration>
 /ipblock <address> <type> <reason> <duration> <forTemplate>
 */
public class IpBlockCommand extends BasicCommand {

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
    private static final Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);;

    public IpBlockCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 5 || args.length == 6) {
            DKBansExecutor executor;
            if(sender.equals(McNative.getInstance().getConsoleSender())) {
                executor = DKBansExecutor.CONSOLE;
            } else {
                executor = ((MinecraftPlayer)sender).getAs(DKBansPlayer.class);
            }
            String address = args[0];
            if(!pattern.matcher(address).matches()) {
                sender.sendMessage(Messages.ERROR_INVALID_IP_ADDRESS, VariableSet.create().add("address", address).add("prefix", Messages.PREFIX));
                return;
            }
            String type0 = args[1];
            IpAddressBlockType blockType = IpAddressBlockType.parse(type0);
            if(blockType == null) {
                sender.sendMessage(Messages.COMMAND_IP_BlOCK_INVALID_TYPE, VariableSet.create().add("type", type0));
                return;
            }
            String reason = args[2];
            String duration0 = args[3];
            long duration = 0;
            try {
                duration = DurationProcessor.getStandard().parse(duration0).toMillis();
            } catch (IllegalArgumentException ignored) {
                sender.sendMessage(Messages.ERROR_INVALID_DURATION_FORMAT, VariableSet.create().add("prefix", Messages.PREFIX).add("duration", duration0));
                return;
            }
            if(args.length == 5) {
                String templateSpecifier = args[4];
                if(!templateSpecifier.contains("@")) {
                    sender.sendMessage(Messages.ERROR_INVALID_TEMPLATE_SPECIFIER_FORMAT, VariableSet.create().add("prefix", Messages.PREFIX));
                    return;
                }
                String[] templateSpecifierSplit = templateSpecifier.split("@");

                TemplateGroup templateGroup = DKBans.getInstance().getTemplateManager().getTemplateGroup(templateSpecifierSplit[0]);
                if(templateGroup == null) {
                    sender.sendMessage(Messages.ERROR_TEMPLATE_GROUP_NOT_EXISTS, VariableSet.create().add("templateGroup", templateSpecifierSplit[0]));
                    return;
                }
                Template template = templateGroup.getTemplate(templateSpecifierSplit[1]);
                if(!(template instanceof PunishmentTemplate)) {
                    sender.sendMessage(Messages.ERROR_TEMPLATE_NOT_EXISTS, VariableSet.create().add("template", templateSpecifierSplit[1]));
                    return;
                }
                IpAddressBlock block = DKBans.getInstance().getIpAddressBlacklistManager().blockIpAddress(address, blockType, executor, reason,
                        System.currentTimeMillis()+duration, (PunishmentTemplate) template);
                sender.sendMessage(Messages.COMMAND_IP_BLOCK, VariableSet.create().add("block", block));
            } else {
                String forReason = args[4];
                String forDuration0 = args[5];
                long forDuration = 0;
                try {
                    forDuration = DurationProcessor.getStandard().parse(forDuration0).toMillis();
                } catch (IllegalArgumentException ignored) {
                    sender.sendMessage(Messages.ERROR_INVALID_DURATION_FORMAT, VariableSet.create().add("prefix", Messages.PREFIX).add("duration", forDuration0));
                    return;
                }
                IpAddressBlock block = DKBans.getInstance().getIpAddressBlacklistManager().blockIpAddress(address, blockType, executor, reason,
                        System.currentTimeMillis()+duration, forReason, forDuration);
                sender.sendMessage(Messages.COMMAND_IP_BLOCK, VariableSet.create().add("block", block));
            }
        } else {
            sender.sendMessage(Messages.COMMAND_IP_BLOCK_USAGE);
        }
    }
}
