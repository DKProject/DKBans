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
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.broadcast.BroadcastTask;
import net.pretronic.dkbans.minecraft.commands.*;
import net.pretronic.dkbans.minecraft.commands.broadcast.BroadcastCommand;
import net.pretronic.dkbans.minecraft.commands.broadcastgroup.BroadcastGroupCommand;
import net.pretronic.dkbans.minecraft.commands.dkbans.DKBansCommand;
import net.pretronic.dkbans.minecraft.commands.history.HistoryCommand;
import net.pretronic.dkbans.minecraft.commands.history.MyHistoryPointsCommand;
import net.pretronic.dkbans.minecraft.commands.history.ResetHistoryCommand;
import net.pretronic.dkbans.minecraft.commands.ip.IpBlockCommand;
import net.pretronic.dkbans.minecraft.commands.ip.IpInfoCommand;
import net.pretronic.dkbans.minecraft.commands.ip.IpUnblockCommand;
import net.pretronic.dkbans.minecraft.commands.punish.*;
import net.pretronic.dkbans.minecraft.commands.report.ReportCommand;
import net.pretronic.dkbans.minecraft.commands.unpunish.UnpunishCommand;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.integration.DKBansPlaceholders;
import net.pretronic.dkbans.minecraft.joinme.MinecraftJoinMeManager;
import net.pretronic.dkbans.minecraft.listeners.InternalListener;
import net.pretronic.dkbans.minecraft.listeners.PlayerListener;
import net.pretronic.dkbans.minecraft.migration.DKBansLegacyMigration;
import net.pretronic.dkbans.minecraft.player.MinecraftPlayerManager;
import net.pretronic.libraries.command.command.Command;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import net.pretronic.libraries.utility.io.FileUtil;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.plugin.MinecraftPlugin;
import org.mcnative.runtime.api.plugin.configuration.ConfigurationProvider;
import org.mcnative.runtime.api.serviceprovider.placeholder.PlaceholderProvider;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DKBansPlugin extends MinecraftPlugin {

    private DefaultDKBans dkBans;

    @Lifecycle(state = LifecycleState.LOAD)
    public void onLoad(LifecycleState state){
        getLogger().info("DKBans is starting, please wait..");

        MinecraftPlayerManager playerManager = new MinecraftPlayerManager();
        this.dkBans = new DefaultDKBans(getDescription().getVersion().getName(),getLogger()
                ,McNative.getInstance().getExecutorService()
                ,McNative.getInstance().getLocal().getEventBus()
                ,getRuntime().getRegistry().getService(ConfigurationProvider.class).getDatabase(this, true)
                ,playerManager, new MinecraftJoinMeManager(),
                new MinecraftBroadcastSender());

        DKBans.setInstance(dkBans);
        dkBans.getBroadcastManager().init();
        dkBans.getTemplateManager().initialize();

        loadConfigs();
        registerCommands();

        dkBans.getFilterManager().initialize();
        dkBans.getMigrationManager().registerMigration(new DKBansLegacyMigration());
        initBroadcast();

        getRuntime().getLocal().getEventBus().subscribe(this,new PlayerListener());
        getRuntime().getLocal().getEventBus().subscribe(this,new InternalListener());
        getRuntime().getNetwork().getEventBus().subscribe(this,new InternalListener());
        getRuntime().getPlayerManager().registerPlayerAdapter(DKBansPlayer.class, player -> playerManager.getPlayer(player.getUniqueId()));
        getRuntime().getRegistry().getService(PlaceholderProvider.class).registerPlaceHolders(this,"dkbans",new DKBansPlaceholders());
        DescriberRegistrar.register();

        getLogger().info("DKBans started successfully");
    }

    private void registerCommands(){
        getRuntime().getLocal().getCommandManager().registerCommand(new JumptoCommand(this, CommandConfig.COMMAND_JUMP_TO));
        getRuntime().getLocal().getCommandManager().registerCommand(new OnlineTimeCommand(this, CommandConfig.COMMAND_ONLINE_TIME));
        getRuntime().getLocal().getCommandManager().registerCommand(new PingCommand(this, CommandConfig.COMMAND_PING));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerInfoCommand(this, CommandConfig.COMMAND_PLAYER_INFO));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerSessionsCommand(this, CommandConfig.COMMAND_PLAYER_SESSIONS));
        getRuntime().getLocal().getCommandManager().registerCommand(new TeamChatCommand(this, CommandConfig.COMMAND_TEAMCHAT));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerNoteCommand(this, CommandConfig.COMMAND_PLAYER_NOTES));
        getRuntime().getLocal().getCommandManager().registerCommand(new PunishNotifyCommand(this, CommandConfig.COMMAND_PUNISH_NOTIFY));
        getRuntime().getLocal().getCommandManager().registerCommand(new NotifyCommand(this, CommandConfig.COMMAND_NOTIFY));
        getRuntime().getLocal().getCommandManager().registerCommand(new ChatLogCommand(this, CommandConfig.COMMAND_CHATLOG));

        getRuntime().getLocal().getCommandManager().registerCommand(new HistoryCommand(this, CommandConfig.COMMAND_HISTORY));
        getRuntime().getLocal().getCommandManager().registerCommand(new ResetHistoryCommand(this, CommandConfig.COMMAND_RESET_HISTORY));
        getRuntime().getLocal().getCommandManager().registerCommand(new MyHistoryPointsCommand(this, CommandConfig.COMMAND_MY_HISTORY_POINTS));
        getRuntime().getLocal().getCommandManager().registerCommand(new PunishInfoCommand(this, CommandConfig.COMMAND_PUNISH_INFO_CONFIGURATION));
        getRuntime().getLocal().getCommandManager().registerCommand(new PunishEditCommand(this, CommandConfig.COMMAND_PUNISH_EDIT_CONFIGURATION));

        getRuntime().getLocal().getCommandManager().registerCommand(new HelpCommand(this, CommandConfig.COMMAND_HELP));
        getRuntime().getLocal().getCommandManager().registerCommand(new FilterCommand(this, CommandConfig.COMMAND_FILTER));
        getRuntime().getLocal().getCommandManager().registerCommand(new DKBansCommand(this));
        getRuntime().getLocal().getCommandManager().registerCommand(new JoinMeCommand(this, CommandConfig.COMMAND_JOINME));
        getRuntime().getLocal().getCommandManager().registerCommand(new ChatClearCommand(this, CommandConfig.COMMAND_CHAT_CLEAR));

        getRuntime().getLocal().getCommandManager().registerCommand(new IpInfoCommand(this, CommandConfig.COMMAND_IP_INFO));
        getRuntime().getLocal().getCommandManager().registerCommand(new IpBlockCommand(this, CommandConfig.COMMAND_IP_BLOCK));
        getRuntime().getLocal().getCommandManager().registerCommand(new IpUnblockCommand(this, CommandConfig.COMMAND_IP_UNBLOCK));

        getRuntime().getLocal().getCommandManager().registerCommand(new BroadcastCommand(this, CommandConfig.COMMAND_BROADCAST));
        getRuntime().getLocal().getCommandManager().registerCommand(new BroadcastGroupCommand(this, CommandConfig.COMMAND_BROADCAST_GROUP));

        for (CommandConfig.PunishmentTypeConfiguration configuration : CommandConfig.COMMAND_PUNISH_DIRECT) {
            Command command;
            String type = configuration.getCommandType();
            if(type.equalsIgnoreCase("COMMAND_PERMANENTLY")){
                command = new PermanentlyPunishCommand(this,configuration,configuration.getPunishmentType(),configuration.getHistoryType(),configuration.getScope());
            }else if(type.equalsIgnoreCase("COMMAND_TEMPORARY")){
                command = new TemporaryPunishCommand(this,configuration,configuration.getPunishmentType(),configuration.getHistoryType(),configuration.getScope());
            }else  if(type.equalsIgnoreCase("COMMAND_ONE_TIME")){
                command = new OneTimePunishCommand(this,configuration,configuration.getPunishmentType(),configuration.getHistoryType(),configuration.getScope());
            }else  if(type.equalsIgnoreCase("COMMAND_LIST")){
                command = new PunishListCommand(this,configuration,configuration.getPunishmentType(),configuration.getScope());
            }else  if(type.equalsIgnoreCase("COMMAND_REVOKE")){
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
            getRuntime().getLocal().getCommandManager().registerCommand(new ReportCommand(this, CommandConfig.COMMAND_REPORT, CommandConfig.COMMAND_REPORT_TEMPLATE_NAME));
        } else {
            getRuntime().getLocal().getCommandManager().registerCommand(new ReportCommand(this, CommandConfig.COMMAND_REPORT, null));
        }
    }

    private void loadConfigs() {
        File configLocation = new File("plugins/DKBans/config.yml");

        if(configLocation.exists()) {
            Document oldConfig = DocumentFileType.YAML.getReader().read(configLocation);

            if(oldConfig.contains("storage.type")) {
                getLogger().info("DKBans Legacy detected");

                File legacyConfigLocation = new File("plugins/DKBans/legacy-config.yml");

                FileUtil.copyFile(configLocation, legacyConfigLocation);

                boolean success = configLocation.delete();
                if(success) {
                    getLogger().info("DKBans Legacy config successful copied to legacy-config.yml");
                } else {
                    getLogger().error("DKBans Legacy config can't be copied to legacy-config.yml");
                }
            }
        }

        getConfiguration().load(DKBansConfig.class);
        getConfiguration("commands").load(CommandConfig.class);

        DKBansConfig.load();
        getLogger().info("DKBans config loaded");
    }

    private void initBroadcast() {
        McNative.getInstance().getScheduler().createTask(this)
                .delay(5, TimeUnit.SECONDS)
                .interval(1, TimeUnit.SECONDS)
                .execute(new BroadcastTask().start());
    }
}
