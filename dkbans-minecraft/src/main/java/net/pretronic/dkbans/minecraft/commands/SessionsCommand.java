package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.List;

public class SessionsCommand extends BasicCommand {

    public SessionsCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
        if(player == null) return;
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);


        int page = 1;
        if(arguments.length > 1 && GeneralUtil.isNaturalNumber(arguments[1])){
            page = Integer.parseInt(arguments[1]);
        }

        List<PlayerSession> sessions = null;//@Todo get sessions dkBansPlayer.gets
        sender.sendMessage(Messages.COMMAND_PLAYER_INFO_SESSIONS,
                VariableSet.create().addDescribed("sessions",arguments[0]));
    }
}
