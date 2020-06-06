package net.pretronic.dkbans.minecraft.commands.punish;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

import java.util.Collection;

public class HistoryCommand extends BasicCommand {

    public HistoryCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length < 1){
            //@Todo usage
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0],true);
        if(player == null) return;
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

        PlayerHistory history = dkBansPlayer.getHistory();

        int page = 1;
        if(arguments.length > 1 && GeneralUtil.isNaturalNumber(arguments[1])){
            page = Integer.parseInt(arguments[1]);
        }

        Collection<PlayerHistoryEntry> entries = history.getEntries(page,25);
        sender.sendMessage(null, VariableSet.create().addDescribed("entries",entries));
        //@Todo find solution for formatting
    }
}
