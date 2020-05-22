package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.OnlineMinecraftPlayer;

public class PingCommand extends BasicCommand {

    public PingCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1 || !sender.hasPermission("dkbans.ping.other")){
            if(!(sender instanceof MinecraftPlayer)){
                sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
                return;
            }

            int ping = ((OnlineMinecraftPlayer) sender).getPing();
            sender.sendMessage(Messages.PING_SELF, VariableSet.create().add("ping",ping));
            return;
        }

        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(arguments[0]);
        if(player == null){
            sender.sendMessage(Messages.PLAYER_NOT_FOUND,VariableSet.create().add("name",arguments[0]));
            return;
        }

        OnlineMinecraftPlayer onlinePlayer = player.getAsOnlinePlayer();
        if(onlinePlayer == null){
            sender.sendMessage(Messages.PLAYER_NOT_ONLINE,VariableSet.create().addDescribed("player",player));
            return;
        }

        int ping = onlinePlayer.getPing();
        sender.sendMessage(Messages.PING_OTHER, VariableSet.create()
                .addDescribed("player",player)
                .add("ping",ping));
    }
}
