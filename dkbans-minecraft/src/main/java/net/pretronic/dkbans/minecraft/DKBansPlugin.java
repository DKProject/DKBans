package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import org.mcnative.common.plugin.MinecraftPlugin;
import org.mcnative.common.plugin.configuration.Configuration;

public class DKBansPlugin extends MinecraftPlugin {

    @Lifecycle(state = LifecycleState.LOAD)
    public void onLoad(LifecycleState state){
        getLogger().info("DKBans is starting, please wait..");
        Configuration config = getConfiguration();

        config.load(DKBansConfig.class);

        getLogger().info("DKBans started successfully");
    }

    private void registerCommands(){

    }

}
