package net.pretronic.dkbans.minecraft.commands.punish;

import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class UnpunishCommand extends BasicCommand {

    public UnpunishCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {

    }
}
