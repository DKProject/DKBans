package net.pretronic.dkbans.minecraft.commands.template;

import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;

public class TemplateImportCommand extends BasicCommand {

    public TemplateImportCommand(ObjectOwner owner) {
        super(owner, CommandConfiguration.name("import"));
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        int count = DKBansConfig.importTemplates();
        commandSender.sendMessage(Messages.COMMAND_TEMPLATE_IMPORT, VariableSet.create().add("count", count));
    }
}
