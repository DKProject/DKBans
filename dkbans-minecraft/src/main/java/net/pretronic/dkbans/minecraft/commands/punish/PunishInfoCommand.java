package net.pretronic.dkbans.minecraft.commands.punish;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

public class PunishInfoCommand extends BasicCommand {

    public PunishInfoCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            //@Todo usage info
            return;
        }
        if(GeneralUtil.isNaturalNumber(arguments[0])){
            int id = Integer.parseInt(arguments[0]);
            PlayerHistoryEntry entry = DKBans.getInstance().getHistoryManager().getHistoryEntry(id);
            //Get from manager -> Create global history manager
        }else{
            MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
            if(player == null) return;
            //@Todo wie machen wir das?
        }
    }
}
