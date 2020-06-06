package net.pretronic.dkbans.minecraft.commands.punish;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

import java.time.Duration;

public class PermaPunishCommand extends BasicCommand {

    private final PunishmentType type;

    public PermaPunishCommand(ObjectOwner owner, CommandConfiguration configuration, PunishmentType type) {
        super(owner, configuration);
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length <= 1){
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0]);
        if(player == null) return;

        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);
        if(CommandUtil.checkBypass(sender,dkBansPlayer)) return;


        boolean override = arguments[1].equalsIgnoreCase("--override");
        String reason = CommandUtil.readStringFromArguments(arguments,override ? 2 : 1);

        if(dkBansPlayer.hasActivePunish(type)){
            if(override && CommandUtil.canOverridePunish(sender,dkBansPlayer,type)){
                dkBansPlayer.unpunish(CommandUtil.getExecutor(sender),type,"Overriding punishment with a new one");
            }else{
                String command = getConfiguration().getName()+arguments[0]+" --override "+reason;
                CommandUtil.sendAlreadyPunished(sender,dkBansPlayer,type,command);
            }
            return;
        }

        PlayerHistoryEntrySnapshot result = dkBansPlayer.punish()//@Todo configurable default history type
                .stuff(CommandUtil.getExecutor(sender))
                .punishmentType(type)
                .timeout(-1)
                .reason(reason)
                .execute();
        CommandUtil.sendPunishResultMessage(sender,result);
    }
}
