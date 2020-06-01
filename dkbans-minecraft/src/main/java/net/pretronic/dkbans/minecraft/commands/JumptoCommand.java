package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.McNative;
import org.mcnative.common.network.component.server.MinecraftServer;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.OnlineMinecraftPlayer;

public class JumptoCommand extends BasicCommand {

    public JumptoCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(!(sender instanceof OnlineMinecraftPlayer)){
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        if(arguments.length < 1){
            sender.sendMessage(Messages.COMMAND_HELP_JUMPTO);
            return;
        }
        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(arguments[0]);
        if(player == null){
            sender.sendMessage(Messages.PLAYER_NOT_FOUND, VariableSet.create().add("name",arguments[0]));
            return;
        }

        OnlineMinecraftPlayer onlinePlayer = player.getAsOnlinePlayer();
        if(onlinePlayer == null){
            sender.sendMessage(Messages.PLAYER_NOT_ONLINE, VariableSet.create().add("player",player));
            return;
        }

        MinecraftServer server = onlinePlayer.getServer();
        if(server == null){
            sender.sendMessage(Messages.SERVER_NOT_FOUND, VariableSet.create());
            return;
        }

        if(server.getName().equals(((OnlineMinecraftPlayer) sender).getServer().getName())){
            sender.sendMessage(Messages.SERVER_ALREADY_CONNECTED, VariableSet.create()
                    .add("player",player)
                    .add("server",server));
            return;
        }

        sender.sendMessage(Messages.SERVER_CONNECTING, VariableSet.create()
                .add("player",player)
                .add("server",server));
        ((OnlineMinecraftPlayer) sender).connect(server);
    }
}
