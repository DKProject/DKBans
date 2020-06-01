package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

public class PlayerInfoCommand extends BasicCommand {

    public PlayerInfoCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            return;
        }

        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(arguments[0]);
        if(player == null){
             return;
        }


    }
}
