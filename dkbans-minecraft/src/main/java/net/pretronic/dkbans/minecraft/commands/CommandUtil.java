package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;

import java.time.Duration;

public class CommandUtil {

    public static DKBansExecutor getExecutor(CommandSender sender){
        if(sender instanceof MinecraftPlayer){
            return ((MinecraftPlayer) sender).getAs(DKBansPlayer.class);
        }else{
            throw new UnsupportedOperationException();//Todo implement console sender
        }
    }

    public static MinecraftPlayer getPlayer(CommandSender sender,String name){
        if(sender.getName().equalsIgnoreCase(name)){
            sender.sendMessage(Messages.PLAYER_NOT_SELF, VariableSet.newEmptySet());
            return null;
        }
        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(name);
        if(player == null){
            sender.sendMessage(Messages.PLAYER_NOT_FOUND, VariableSet.create().add("name",name));
            return null;
        }
        return player;
    }

    public static boolean checkBypass(CommandSender sender,DKBansPlayer player){
        if(player.hasBypass() && !sender.hasPermission(Permissions.BYPASS_IGNORE)){
            sender.sendMessage(Messages.PLAYER_HAS_BYPASS,VariableSet.create().addDescribed("player",player));
            return true;
        }
        return false;
    }

    public static String readStringFromArguments(String[] arguments, int start){
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arguments.length; i++){
            builder.append(' ').append(arguments[i]);
        }
        return builder.substring(1);
    }

    public static Duration parseDuration(CommandSender sender, String duration){
        try {
            return DurationProcessor.getStandard().parse(duration);
        }catch (IllegalArgumentException exception){
            sender.sendMessage(Messages.ERROR_INVALID_DURATION_FORMAT,VariableSet.create().add("input",duration));
            return null;
        }
    }

    public static boolean canOverridePunish(CommandSender sender,DKBansPlayer player, PunishmentType type){
        if(sender.hasPermission(Permissions.PUNISH_OVERRIDE_ALL)) return true;
        if(sender.hasPermission(Permissions.PUNISH_OVERRIDE_OWN)){
            DKBansExecutor stuff = player.getHistory().getActiveEntry(type).getCurrent().getStuff();
            return stuff != null && stuff.equals(sender);
        }
        return false;
    }

    public static void sendAlreadyPunished(CommandSender sender,DKBansPlayer player, PunishmentType type, String override){
        if(type == PunishmentType.BAN){
            sender.sendMessage(Messages.PUNISH_ALREADY_BANNED,VariableSet.create().addDescribed("player",player));
        }else if(type == PunishmentType.MUTE){
            sender.sendMessage(Messages.PUNISH_ALREADY_MUTED,VariableSet.create().addDescribed("player",player));
        }

        if(override != null && canOverridePunish(sender, player, type)){
            sender.sendMessage(Messages.PUNISH_OVERRIDE, VariableSet.create()
                    .add("command",override));
        }
    }

    public static void sendPunishResultMessage(CommandSender sender,PlayerHistoryEntrySnapshot snapshot){

    }

}
