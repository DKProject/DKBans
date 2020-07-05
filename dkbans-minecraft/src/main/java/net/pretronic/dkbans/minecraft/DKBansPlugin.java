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

package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistory;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntry;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryType;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.dkbans.common.template.DefaultTemplateCategory;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.dkbans.minecraft.commands.*;
import net.pretronic.dkbans.minecraft.commands.punish.KickCommand;
import net.pretronic.dkbans.minecraft.commands.punish.PermaPunishCommand;
import net.pretronic.dkbans.minecraft.commands.punish.TempPunishCommand;
import net.pretronic.dkbans.minecraft.commands.punish.TemplatePunishCommand;
import net.pretronic.dkbans.minecraft.commands.report.ReportCommand;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.listeners.InternalListener;
import net.pretronic.dkbans.minecraft.listeners.PlayerListener;
import net.pretronic.dkbans.minecraft.player.MinecraftPlayerManager;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import org.mcnative.common.McNative;
import org.mcnative.common.plugin.MinecraftPlugin;
import org.mcnative.common.plugin.configuration.ConfigurationProvider;

import java.util.Map;

public class DKBansPlugin extends MinecraftPlugin {

    private DefaultDKBans dkBans;

    @Lifecycle(state = LifecycleState.LOAD)
    public void onLoad(LifecycleState state){
        getLogger().info("DKBans is starting, please wait..");

        MinecraftPlayerManager playerManager = new MinecraftPlayerManager();
        this.dkBans = new DefaultDKBans(getLogger()
                ,McNative.getInstance().getLocal().getEventBus()
                ,getRuntime().getRegistry().getService(ConfigurationProvider.class).getDatabase(this, true)
                ,playerManager);

        DKBans.setInstance(dkBans);

        dkBans.getTemplateManager().initialize();

        getConfiguration().load(DKBansConfig.class);
        getConfiguration("commands").load(CommandConfig.class);

        DKBansConfig.load(dkBans);

        registerCommands();
        registerDescribers();

        dkBans.getFilterManager().initialize();

        getRuntime().getLocal().getEventBus().subscribe(this,new PlayerListener());
        getRuntime().getLocal().getEventBus().subscribe(this,new InternalListener());

        getRuntime().getPlayerManager().registerPlayerAdapter(DKBansPlayer.class, player -> playerManager.getPlayer(player.getUniqueId()));

        getLogger().info("DKBans started successfully");
    }

    private void registerCommands(){
        getRuntime().getLocal().getCommandManager().registerCommand(new JumptoCommand(this, CommandConfig.COMMAND_JUMP_TO));
        getRuntime().getLocal().getCommandManager().registerCommand(new OnlineTimeCommand(this, CommandConfig.COMMAND_ONLINE_TIME));
        getRuntime().getLocal().getCommandManager().registerCommand(new PingCommand(this, CommandConfig.COMMAND_PING));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerInfoCommand(this, CommandConfig.COMMAND_PLAYER_INFO));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerInfoCommand(this, CommandConfig.COMMAND_PLAYER_INFO));
        getRuntime().getLocal().getCommandManager().registerCommand(new TeamChatCommand(this, CommandConfig.COMMAND_TEAMCHAT));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerNoteCommand(this, CommandConfig.COMMAND_PLAYER_NOTES));
        getRuntime().getLocal().getCommandManager().registerCommand(new PunishNotifyCommand(this, CommandConfig.COMMAND_PUNISH_NOTIFY));
        getRuntime().getLocal().getCommandManager().registerCommand(new NotifyCommand(this, CommandConfig.COMMAND_NOTIFY));

        getRuntime().getLocal().getCommandManager().registerCommand(new HelpCommand(this, CommandConfig.COMMAND_HELP));
        getRuntime().getLocal().getCommandManager().registerCommand(new FilterCommand(this, CommandConfig.COMMAND_FILTER));

        getRuntime().getLocal().getCommandManager().registerCommand(new KickCommand(this, CommandConfig.COMMAND_PUNISH_KICK));

        getRuntime().getLocal().getCommandManager().registerCommand(new PermaPunishCommand(this, CommandConfig.COMMAND_PUNISH_BAN_PERMANENT, PunishmentType.BAN));
        getRuntime().getLocal().getCommandManager().registerCommand(new TempPunishCommand(this, CommandConfig.COMMAND_PUNISH_BAN_TEMPORARY, PunishmentType.BAN));
        getRuntime().getLocal().getCommandManager().registerCommand(new PermaPunishCommand(this, CommandConfig.COMMAND_PUNISH_MUTE_PERMANENT, PunishmentType.MUTE));
        getRuntime().getLocal().getCommandManager().registerCommand(new TempPunishCommand(this, CommandConfig.COMMAND_PUNISH_MUTE_TEMPORARY, PunishmentType.MUTE));

        for (Map.Entry<String, CommandConfiguration> entry : CommandConfig.COMMAND_TEMPLATE_PUNISHMENT.entrySet()) {
            TemplateGroup group = dkBans.getTemplateManager().getTemplateGroup(entry.getKey());
            if(group == null){
                getLogger().warn("Configured template group "+entry.getKey()+" does not exist");
            }else{
                getRuntime().getLocal().getCommandManager().registerCommand(new TemplatePunishCommand(this,entry.getValue(),group));
            }
        }
        for (Map.Entry<String, CommandConfiguration> entry : CommandConfig.COMMAND_TEMPLATE_REPORT.entrySet()) {
            TemplateGroup group = dkBans.getTemplateManager().getTemplateGroup(entry.getKey());
            if(group == null){
                getLogger().warn("Configured template group "+entry.getKey()+" does not exist");
            }else{
                getRuntime().getLocal().getCommandManager().registerCommand(new ReportCommand(this,entry.getValue(),group));
            }
        }
    }

    private void registerDescribers(){
        VariableDescriberRegistry.registerDescriber(DKBansPlayer.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistory.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryEntrySnapshot.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryEntry.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryType.class);

        VariableDescriberRegistry.registerDescriber(DefaultTemplate.class);
        VariableDescriberRegistry.registerDescriber(DefaultTemplateCategory.class);
        VariableDescriberRegistry.registerDescriber(DefaultTemplateGroup.class);
    }
}
