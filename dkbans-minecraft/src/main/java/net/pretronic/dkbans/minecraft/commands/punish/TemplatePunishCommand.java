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

package net.pretronic.dkbans.minecraft.commands.punish;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

public class TemplatePunishCommand extends BasicCommand {

    private final TemplateGroup templates;

    public TemplatePunishCommand(ObjectOwner owner, CommandConfiguration configuration,TemplateGroup template) {
        super(owner, configuration);
        this.templates = template;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length <= 1){
            sendTemplates(sender);
            return;
        }
        MinecraftPlayer player = CommandUtil.getPlayer(sender, arguments[0],true);
        if(player == null) return;
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);
        if(CommandUtil.checkBypass(sender,dkBansPlayer)) return;

        PunishmentTemplate template = (PunishmentTemplate) templates.getTemplate(arguments[1]);
        if(template == null){
            sendTemplates(sender);
            return;
        }
        if(!sender.hasPermission(template.getPermission())){
            sender.sendMessage(Messages.PUNISH_TEMPLATE_NO_PERMISSION,
                    VariableSet.create()
                            .add("prefix",Messages.PREFIX)
                            .addDescribed("template",template));
            return;
        }

        PunishmentTemplateEntry entry = template.getNextEntry(dkBansPlayer);
        if(entry == null){
            sender.sendMessage(Messages.ERROR_INTERNAL,VariableSet.create().add("prefix",Messages.PREFIX));
            return;
        }

        boolean override = arguments.length >= 3 && arguments[2].equalsIgnoreCase("--override");
        int messageIndex = override ? 4 : 3;
        String message = null;

        if(arguments.length > messageIndex){
            message = CommandUtil.readStringFromArguments(arguments,messageIndex);
        }

        if(dkBansPlayer.hasActivePunish(entry.getType())){
            if(override && CommandUtil.canOverridePunish(sender,dkBansPlayer,entry.getType())){
                dkBansPlayer.unpunish(CommandUtil.getExecutor(sender),entry.getType(),"Overriding punishment with a new one");
            }else{
                String command = "/"+getConfiguration().getName()+" "+arguments[0]+" "+arguments[1]+" --override ";
                if(message != null) command += message;
                CommandUtil.sendAlreadyPunished(sender,dkBansPlayer,entry.getType(),command);
                return;
            }
        }

        PlayerHistoryEntrySnapshot result = dkBansPlayer.punish(CommandUtil.getExecutor(sender),template);
        if(message != null) result.getEntry().createNote(CommandUtil.getExecutor(sender),message);
        CommandUtil.sendPunishResultMessage(sender,dkBansPlayer,result);
    }

    private void sendTemplates(CommandSender sender){
        sender.sendMessage(Messages.PUNISH_TEMPLATE_LIST,VariableSet.create()
                .add("prefix",Messages.PREFIX)
                .add("templates",templates.getTemplates())
                .add("command",getConfiguration().getName()));
    }
}
