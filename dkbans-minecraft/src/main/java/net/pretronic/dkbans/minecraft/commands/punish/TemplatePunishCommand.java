package net.pretronic.dkbans.minecraft.commands.punish;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

public class TemplatePunishCommand extends BasicCommand {

    public TemplatePunishCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length <= 1){
            sendTemplates();
        }
        MinecraftPlayer player = CommandUtil.getPlayer(sender, arguments[0]);
        if(player == null) return;
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);
        if(CommandUtil.checkBypass(sender,dkBansPlayer)) return;

        //@Todo get template
        Template template = null;
        if(template == null){
            sendTemplates();
            return;
        }
        if(!sender.hasPermission(template.getPermission())){
            sender.sendMessage(Messages.PUNISH_TEMPLATE_NO_PERMISSION, VariableSet.create().addDescribed("template",template));
            return;
        }

        PunishmentTemplateEntry entry = template.getNextEntry(dkBansPlayer);

        if(entry == null){
            //@Todo throw internal error
            return;
        }

        if(dkBansPlayer.hasActivePunish(entry.getType())){
            //@Todo send override message
        }

        //@Todo integrate internal command

        PlayerHistoryEntrySnapshot result = dkBansPlayer.punish(CommandUtil.getExecutor(sender),template);
    }

    private void sendTemplates(){

    }
}
