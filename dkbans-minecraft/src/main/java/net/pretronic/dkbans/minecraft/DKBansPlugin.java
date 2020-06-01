package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.minecraft.commands.JumptoCommand;
import net.pretronic.dkbans.minecraft.commands.OnlineTimeCommand;
import net.pretronic.dkbans.minecraft.commands.PingCommand;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.listeners.PlayerListener;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import org.mcnative.common.McNative;
import org.mcnative.common.plugin.MinecraftPlugin;
import org.mcnative.common.plugin.configuration.Configuration;
import org.mcnative.common.plugin.configuration.ConfigurationProvider;

public class DKBansPlugin extends MinecraftPlugin {

    private DefaultDKBans dkBans;

    @Lifecycle(state = LifecycleState.LOAD)
    public void onLoad(LifecycleState state){
        getLogger().info("DKBans is starting, please wait..");

        this.dkBans = new DefaultDKBans(getLogger(), McNative.getInstance().getRegistry().getService(ConfigurationProvider.class).getDatabase(this, true));

        Configuration config = getConfiguration();

        config.load(DKBansConfig.class);

        DKBansConfig.load(dkBans);


        registerCommands();

        getRuntime().getLocal().getEventBus().subscribe(this,new PlayerListener());

        getLogger().info("DKBans started successfully");
        DKBans.setInstance(dkBans);
    }

    private void registerCommands(){
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new JumptoCommand(this, DKBansConfig.COMMAND_JUMP_TO));
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new OnlineTimeCommand(this, DKBansConfig.COMMAND_ONLINE_TIME));
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new PingCommand(this, DKBansConfig.COMMAND_PING));
    }

}
