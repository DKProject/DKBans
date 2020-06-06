package net.pretronic.dkbans.minecraft.commands.template;

import net.pretronic.libraries.command.command.MainCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TemplateCommand extends MainCommand {

    public TemplateCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
        registerCommand(new TemplateImportCommand(owner));
    }
}
