/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.07.20, 18:06
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

import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TemplateCommand extends MainCommand {

    public TemplateCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.newBuilder()
                .name("template")
                .permission(CommandConfig.PERMISSION_ADMIN)
                .create());
        registerCommand(new TemplateImportCommand(owner));
        registerCommand(new TemplateExportCommand(owner));
    }
}
