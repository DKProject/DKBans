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
import net.pretronic.dkbans.common.filter.DefaultFilter;
import net.pretronic.dkbans.common.player.DefaultDKBansPlayer;
import net.pretronic.dkbans.common.player.chatlog.DefaultChatLogEntry;
import net.pretronic.dkbans.common.player.chatlog.DefaultPlayerChatLog;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistory;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntry;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryType;
import net.pretronic.dkbans.common.player.note.DefaultPlayerNote;
import net.pretronic.dkbans.common.player.report.DefaultPlayerReport;
import net.pretronic.dkbans.common.player.report.DefaultPlayerReportEntry;
import net.pretronic.dkbans.common.player.session.DefaultPlayerSession;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.dkbans.common.template.DefaultTemplateCategory;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplate;
import net.pretronic.dkbans.minecraft.commands.*;
import net.pretronic.dkbans.minecraft.commands.dkbans.DKBansCommand;
import net.pretronic.dkbans.minecraft.commands.history.HistoryCommand;
import net.pretronic.dkbans.minecraft.commands.history.MyHistoryPointsCommand;
import net.pretronic.dkbans.minecraft.commands.history.ResetHistoryCommand;
import net.pretronic.dkbans.minecraft.commands.punish.*;
import net.pretronic.dkbans.minecraft.commands.report.ReportCommand;
import net.pretronic.dkbans.minecraft.commands.unpunish.UnpunishCommand;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.joinme.MinecraftJoinMeManager;
import net.pretronic.dkbans.minecraft.listeners.InternalListener;
import net.pretronic.dkbans.minecraft.listeners.PlayerListener;
import net.pretronic.dkbans.minecraft.migration.DKBansLegacyMigration;
import net.pretronic.dkbans.minecraft.player.MinecraftPlayerManager;
import net.pretronic.libraries.command.command.Command;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriber;
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
        this.dkBans = new DefaultDKBans(getDescription().getVersion().getName()
                , getLogger()
                , McNative.getInstance().getExecutorService()
                ,McNative.getInstance().getLocal().getEventBus()
                ,getRuntime().getRegistry().getService(ConfigurationProvider.class).getDatabase(this, true)
                ,playerManager, new MinecraftJoinMeManager());

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

        dkBans.getMigrationManager().registerMigration(new DKBansLegacyMigration());

        getLogger().info("DKBans started successfully");
    }

    private void registerCommands(){
        getRuntime().getLocal().getCommandManager().registerCommand(new JumptoCommand(this, CommandConfig.COMMAND_JUMP_TO));
        getRuntime().getLocal().getCommandManager().registerCommand(new OnlineTimeCommand(this, CommandConfig.COMMAND_ONLINE_TIME));
        getRuntime().getLocal().getCommandManager().registerCommand(new PingCommand(this, CommandConfig.COMMAND_PING));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerInfoCommand(this, CommandConfig.COMMAND_PLAYER_INFO));
        getRuntime().getLocal().getCommandManager().registerCommand(new TeamChatCommand(this, CommandConfig.COMMAND_TEAMCHAT));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerNoteCommand(this, CommandConfig.COMMAND_PLAYER_NOTES));
        getRuntime().getLocal().getCommandManager().registerCommand(new PunishNotifyCommand(this, CommandConfig.COMMAND_PUNISH_NOTIFY));
        getRuntime().getLocal().getCommandManager().registerCommand(new NotifyCommand(this, CommandConfig.COMMAND_NOTIFY));
        getRuntime().getLocal().getCommandManager().registerCommand(new ChatLogCommand(this, CommandConfig.COMMAND_CHATLOG));

        getRuntime().getLocal().getCommandManager().registerCommand(new HistoryCommand(this, CommandConfig.COMMAND_HISTORY));
        getRuntime().getLocal().getCommandManager().registerCommand(new ResetHistoryCommand(this, CommandConfig.COMMAND_RESET_HISTORY));
        getRuntime().getLocal().getCommandManager().registerCommand(new MyHistoryPointsCommand(this, CommandConfig.COMMAND_MY_HISTORY_POINTS));

        getRuntime().getLocal().getCommandManager().registerCommand(new HelpCommand(this, CommandConfig.COMMAND_HELP));
        getRuntime().getLocal().getCommandManager().registerCommand(new FilterCommand(this, CommandConfig.COMMAND_FILTER));
        getRuntime().getLocal().getCommandManager().registerCommand(new DKBansCommand(this));
        getRuntime().getLocal().getCommandManager().registerCommand(new JoinMeCommand(this, CommandConfig.COMMAND_JOINME));

        for (CommandConfig.PunishmentTypeConfiguration configuration : CommandConfig.COMMAND_PUNISH_DIRECT) {
            Command command;

            if(configuration.getCommandType().equalsIgnoreCase("COMMAND_PERMANENTLY")){
                command = new PermanentlyPunishCommand(this,configuration,configuration.getPunishmentType()
                        ,configuration.getHistoryType(),configuration.getScope());
            }else if(configuration.getCommandType().equalsIgnoreCase("COMMAND_TEMPORARY")){
                command = new TemporaryPunishCommand(this,configuration,configuration.getPunishmentType()
                        ,configuration.getHistoryType(),configuration.getScope());
            }else  if(configuration.getCommandType().equalsIgnoreCase("COMMAND_ONE_TIME")){
                command = new OneTimePunishCommand(this,configuration,configuration.getPunishmentType()
                        ,configuration.getHistoryType(),configuration.getScope());
            }else  if(configuration.getCommandType().equalsIgnoreCase("COMMAND_LIST")){
                command = new PunishListCommand(this,configuration,configuration.getPunishmentType(),configuration.getScope());
            }else  if(configuration.getCommandType().equalsIgnoreCase("COMMAND_REVOKE")){
                command = new UnpunishCommand(this,configuration,configuration.getPunishmentType(),configuration.getScope());
            }else{
                throw new IllegalArgumentException("Invalid command type "+configuration.getCommandType());
            }

            getRuntime().getLocal().getCommandManager().registerCommand(command);
        }

        for (Map.Entry<String, CommandConfiguration> entry : CommandConfig.COMMAND_PUNISH_TEMPLATE.entrySet()) {
            TemplateGroup group = dkBans.getTemplateManager().getTemplateGroup(entry.getKey());
            if(group == null){
                getLogger().warn("Configured template group "+entry.getKey()+" does not exist");
            }else{
                getRuntime().getLocal().getCommandManager().registerCommand(new TemplatePunishCommand(this,entry.getValue(),group));
            }
        }

        if(CommandConfig.COMMAND_REPORT_MODE.equalsIgnoreCase("template") && CommandConfig.COMMAND_REPORT_TEMPLATE_NAME != null) {
            getRuntime().getLocal().getCommandManager().registerCommand(new ReportCommand(this, CommandConfig.COMMAND_REPORT_CONFIGURATION,
                    CommandConfig.COMMAND_REPORT_TEMPLATE_NAME));
        } else {
            getRuntime().getLocal().getCommandManager().registerCommand(new ReportCommand(this, CommandConfig.COMMAND_REPORT_CONFIGURATION, null));
        }
    }

    private void registerDescribers(){
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistory.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryEntrySnapshot.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryType.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerNote.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerChatLog.class);
        VariableDescriberRegistry.registerDescriber(DefaultChatLogEntry.class);
        VariableDescriberRegistry.registerDescriber(DefaultFilter.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerReport.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerReportEntry.class);

        VariableDescriberRegistry.registerDescriber(DefaultTemplate.class);
        VariableDescriberRegistry.registerDescriber(DefaultTemplateCategory.class);
        VariableDescriberRegistry.registerDescriber(DefaultTemplateGroup.class);
        VariableDescriberRegistry.registerDescriber(DefaultPunishmentTemplate.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerSession.class);
        VariableDescriberRegistry.registerDescriber(PunishmentType.class);

        VariableDescriber<DefaultDKBansPlayer> playerDescriber = VariableDescriberRegistry.registerDescriber(DefaultDKBansPlayer.class);
        playerDescriber.setForwardFunction(player -> McNative.getInstance().getPlayerManager().getPlayer(player.getUniqueId()));

        VariableDescriber<DefaultPlayerHistoryEntry> entryDescriber =  VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryEntry.class);
        entryDescriber.setForwardFunction(DefaultPlayerHistoryEntry::getCurrent);
    }
}
