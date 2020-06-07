package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.minecraft.commands.JumptoCommand;
import net.pretronic.dkbans.minecraft.commands.OnlineTimeCommand;
import net.pretronic.dkbans.minecraft.commands.PingCommand;
import net.pretronic.dkbans.minecraft.commands.PlayerInfoCommand;
import net.pretronic.dkbans.minecraft.commands.punish.KickCommand;
import net.pretronic.dkbans.minecraft.commands.punish.PermaPunishCommand;
import net.pretronic.dkbans.minecraft.commands.punish.TempPunishCommand;
import net.pretronic.dkbans.minecraft.commands.punish.TemplatePunishCommand;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.listeners.PlayerListener;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import org.mcnative.common.plugin.MinecraftPlugin;
import org.mcnative.common.plugin.configuration.ConfigurationProvider;

import java.util.Map;

public class DKBansPlugin extends MinecraftPlugin {

    private DefaultDKBans dkBans;

    @Lifecycle(state = LifecycleState.LOAD)
    public void onLoad(LifecycleState state){
        getLogger().info("DKBans is starting, please wait..");

        this.dkBans = new DefaultDKBans(getLogger(), getRuntime().getRegistry().getService(ConfigurationProvider.class).getDatabase(this, true));
        DKBans.setInstance(dkBans);

        getConfiguration().load(DKBansConfig.class);
        getConfiguration("commands").load(CommandConfig.class);

        DKBansConfig.load(dkBans);

        registerCommands();

        getRuntime().getLocal().getEventBus().subscribe(this,new PlayerListener());

        MinecraftPlayerManager playerManager = new MinecraftPlayerManager();
        getRuntime().getPlayerManager().registerPlayerAdapter(DKBansPlayer.class, playerManager::getPlayer);

        getLogger().info("DKBans started successfully");
    }

    private void registerCommands(){
        getRuntime().getLocal().getCommandManager().registerCommand(new JumptoCommand(this, CommandConfig.COMMAND_JUMP_TO));
        getRuntime().getLocal().getCommandManager().registerCommand(new OnlineTimeCommand(this, CommandConfig.COMMAND_ONLINE_TIME));
        getRuntime().getLocal().getCommandManager().registerCommand(new PingCommand(this, CommandConfig.COMMAND_PING));
        getRuntime().getLocal().getCommandManager().registerCommand(new PlayerInfoCommand(this, CommandConfig.COMMAND_PLAYER_INFO));

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
    }
}
