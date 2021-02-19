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
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlock;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlockType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.time.Duration;

public class IpBlockCommand extends BasicCommand {

    public IpBlockCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 3){
            sender.sendMessage(Messages.COMMAND_IP_BLOCK_HELP);
            return;
        }

        String address = arguments[0];
        String reason = arguments[1];

        Duration duration = CommandUtil.parseDuration(sender,arguments[2]);
        if(duration == null) return;

        IpAddressBlock result = null;
        if(arguments.length > 3){
            String action = arguments[3];
            if(action.equalsIgnoreCase("template") && arguments.length > 4){

                if(!arguments[4].contains("@")) {
                    sender.sendMessage(Messages.ERROR_INVALID_TEMPLATE_SPECIFIER_FORMAT, VariableSet.create()
                            .add("prefix", Messages.PREFIX));
                    return;
                }
                String[] parts = arguments[4].split("@");
                TemplateGroup templateGroup = DKBans.getInstance().getTemplateManager().getTemplateGroup(parts[0]);
                if(templateGroup == null) {
                    sender.sendMessage(Messages.ERROR_TEMPLATE_GROUP_NOT_EXISTS, VariableSet.create()
                            .add("templateGroup", parts[0]));
                    return;
                }
                Template template = templateGroup.getTemplate(parts[1]);
                if(!(template instanceof PunishmentTemplate)) {
                    sender.sendMessage(Messages.ERROR_TEMPLATE_NOT_EXISTS, VariableSet.create()
                            .add("template", parts[1]));
                    return;
                }

                result = DKBans.getInstance().getIpAddressManager().blockIpAddress(IpAddressBlockType.BAN,address,reason
                        ,duration,CommandUtil.getExecutor(sender), (PunishmentTemplate) template);
            }else if(action.equalsIgnoreCase("temporary") && arguments.length > 5){
                Duration forDuration = CommandUtil.parseDuration(sender,arguments[5]);
                if(forDuration == null) return;
                result = DKBans.getInstance().getIpAddressManager().blockIpAddress(IpAddressBlockType.BAN,address,reason
                        ,duration,CommandUtil.getExecutor(sender), CommandUtil.readStringFromArguments(arguments,6),forDuration);
            }else if(action.equalsIgnoreCase("permanently") && arguments.length > 4){
                result = DKBans.getInstance().getIpAddressManager().blockIpAddress(IpAddressBlockType.BAN,address,reason
                        ,duration,CommandUtil.getExecutor(sender), CommandUtil.readStringFromArguments(arguments,5));
            }else{
                sender.sendMessage(Messages.COMMAND_IP_BLOCK_HELP);
                return;
            }
        }else{
            result = DKBans.getInstance().getIpAddressManager().blockIpAddress(address,reason,duration,CommandUtil.getExecutor(sender));
        }
        sender.sendMessage(Messages.COMMAND_IP_BLOCK, VariableSet.create().addDescribed("block", result));
    }
}
