package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateFactory;
import net.pretronic.dkbans.api.template.TemplateType;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.minecraft.commands.JumptoCommand;
import net.pretronic.dkbans.minecraft.commands.OnlineTimeCommand;
import net.pretronic.dkbans.minecraft.commands.PingCommand;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.plugin.lifecycle.Lifecycle;
import net.pretronic.libraries.plugin.lifecycle.LifecycleState;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.common.McNative;
import org.mcnative.common.plugin.MinecraftPlugin;
import org.mcnative.common.plugin.configuration.Configuration;
import org.mcnative.common.plugin.configuration.ConfigurationProvider;

import java.util.ArrayList;
import java.util.Collection;

public class DKBansPlugin extends MinecraftPlugin {

    private DefaultDKBans dkBans;

    @Lifecycle(state = LifecycleState.LOAD)
    public void onLoad(LifecycleState state){
        System.out.println("TEST");
        getLogger().info("DKBans is starting, please wait..");

        this.dkBans = new DefaultDKBans(getLogger(), McNative.getInstance().getRegistry().getService(ConfigurationProvider.class).getDatabase(this, true));

        Configuration config = getConfiguration();

        config.load(DKBansConfig.class);

        DKBansConfig.load(dkBans);


        registerCommands();

        getLogger().info("DKBans started successfully");
        DKBans.setInstance(dkBans);
    }

    private void registerCommands(){
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new JumptoCommand(this, DKBansConfig.COMMAND_JUMP_TO));
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new OnlineTimeCommand(this, DKBansConfig.COMMAND_ONLINE_TIME));
        McNative.getInstance().getLocal().getCommandManager().registerCommand(new PingCommand(this, DKBansConfig.COMMAND_PING));
    }
}
