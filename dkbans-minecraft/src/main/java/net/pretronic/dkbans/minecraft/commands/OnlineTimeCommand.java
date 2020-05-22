package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

public class OnlineTimeCommand extends BasicCommand {

    public OnlineTimeCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length == 1){
            if(sender instanceof MinecraftPlayer){
                DKBansPlayer player = ((MinecraftPlayer) sender).getAs(DKBansPlayer.class);
                sender.sendMessage(Messages.ONLINE_TIME_SELF, VariableSet.create()
                        .add("online-time-formatted-short", DurationProcessor.getStandard().formatShort(player.getOnlineTime()))
                        .add("online-time-formatted", DurationProcessor.getStandard().format(player.getOnlineTime()))
                        .addDescribed("player",player));
            }else{
                sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            }
        }else{
            MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(arguments[0]);
            if(player == null){
                sender.sendMessage(Messages.PLAYER_NOT_FOUND, VariableSet.create().add("name",arguments[0]));
                return;
            }
            DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

            sender.sendMessage(Messages.ONLINE_TIME_OTHER, VariableSet.create()
                    .add("online-time-formatted-short", DurationProcessor.getStandard().formatShort(dkBansPlayer.getOnlineTime()))
                    .add("online-time-formatted", DurationProcessor.getStandard().format(dkBansPlayer.getOnlineTime()))
                    .addDescribed("player",player));
        }
    }
}
