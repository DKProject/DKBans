/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 06.08.21, 19:08
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

package net.pretronic.dkbans.minecraft.commands.dkbans.template;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateFactory;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class TemplateExportCommand extends BasicCommand {

    public TemplateExportCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("export"));
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        File folder = new File("plugins/DKBans/templates/exports/export-"+System.currentTimeMillis()+"/");
        folder.mkdirs();

        AtomicInteger templateCount = new AtomicInteger();
        for (TemplateGroup templateGroup : DKBans.getInstance().getTemplateManager().getTemplateGroups()) {
            File location = new File(folder, templateGroup.getName()+".yml");
            Document data = constructExportData(templateGroup, templateCount);
            DocumentFileType.YAML.getWriter().write(location, data, true);
        }
        commandSender.sendMessage(Messages.COMMAND_TEMPLATE_EXPORT, VariableSet.create()
                .add("count", DKBans.getInstance().getTemplateManager().getTemplateGroups().size())
                .add("templateCount", templateCount.get())
                .add("location", folder.getAbsolutePath()));
    }

    private Document constructExportData(TemplateGroup templateGroup, AtomicInteger templateCount) {
        Document groupData = Document.newDocument()
                .add("type", templateGroup.getTemplateType().getName())
                .add("calculation", templateGroup.getCalculationType().toString())
                .add("name", templateGroup.getName());
        Document templates = Document.newDocument();
        for (Template template : templateGroup.getTemplates()) {
            Document templateData = TemplateFactory.toData(template);
            templateData.add("name", template.getName())
                    .add("display", template.getDisplayName())
                    .add("permission", template.getPermission())
                    .add("aliases", template.getAliases())
                    .add("hidden", template.isHidden())
                    .add("category", template.getCategory().getName());
            if(template.getHistoryType() != null) {
                templateData.add("historyType", template.getHistoryType().getName());
            }
            templates.add(String.valueOf(template.getInGroupId()), templateData);
            templateCount.incrementAndGet();
        }
        groupData.add("templates", templates);
        return groupData;
    }
}
