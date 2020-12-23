/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 17:26
 * @web %web%
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.pretronic.dkbans.minecraft.commands;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkbans.minecraft.config.Permissions;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.common.McNative;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.OnlineMinecraftPlayer;
import org.mcnative.common.text.components.MessageComponent;

import java.time.Duration;

public class CommandUtil {

    public static DKBansExecutor getExecutor(CommandSender sender){
        if(sender instanceof MinecraftPlayer){
            return ((MinecraftPlayer) sender).getAs(DKBansPlayer.class);
        }else{
            return DKBansExecutor.CONSOLE;
        }
    }

    public static MinecraftPlayer getPlayer(CommandSender sender,String name){
        return getPlayer(sender,name,false);
    }

    public static MinecraftPlayer getPlayer(CommandSender sender,String name, boolean notSelf){
        if(notSelf && sender.getName().equalsIgnoreCase(name)){
            sender.sendMessage(Messages.PLAYER_NOT_SELF, VariableSet
                    .create().addDescribed("prefix",Messages.PREFIX));
            return null;
        }
        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(name);
        if(player == null){
            sender.sendMessage(Messages.PLAYER_NOT_FOUND, VariableSet.create()
                    .add("prefix",Messages.PREFIX)
                    .add("name",name));
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
            DKBansExecutor stuff = player.getHistory().getActiveEntry(type).getCurrent().getStaff();
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

    public static void sendNotPunished(CommandSender sender,DKBansPlayer player, PunishmentType type){
        VariableSet variables = VariableSet.create()
                .addDescribed("player",player);
        if(type == PunishmentType.BAN){
            sender.sendMessage(Messages.PUNISH_NOT_BANNED,variables);
        }else{
            sender.sendMessage(Messages.PUNISH_NOT_MUTED,variables);
        }
    }

    public static void sendPunishResultMessage(CommandSender sender,DKBansPlayer player,PlayerHistoryEntrySnapshot snapshot){
        VariableSet variables = VariableSet.create()
                .addDescribed("snapshot",snapshot)
                .addDescribed("entry",snapshot.getEntry())
                .addDescribed("player",player);

        if(snapshot.getPunishmentType() == PunishmentType.BAN){
            sender.sendMessage(Messages.PUNISH_SUCCESS_BAN,variables);
        }else if(snapshot.getPunishmentType() == PunishmentType.MUTE){
            sender.sendMessage(Messages.PUNISH_SUCCESS_MUTE,variables);
        }else if(snapshot.getPunishmentType() == PunishmentType.KICK){
            sender.sendMessage(Messages.PUNISH_SUCCESS_KICK,variables);
        }else if(snapshot.getPunishmentType() == PunishmentType.WARN){
            sender.sendMessage(Messages.PUNISH_SUCCESS_WARN,variables);
        }
    }

    public static void sendUnpunishResultMessage(CommandSender sender,DKBansPlayer player,PlayerHistoryEntrySnapshot snapshot){
        if(snapshot.getPunishmentType() == PunishmentType.BAN){
            sender.sendMessage(Messages.UNPUNISH_SUCCESS_BAN,VariableSet.create()
                    .addDescribed("player",player));
        }else if(snapshot.getPunishmentType() == PunishmentType.MUTE){
            sender.sendMessage(Messages.UNPUNISH_SUCCESS_MUTE,VariableSet.create()
                    .addDescribed("player",player));
        }
    }

    public static void changeLogin(MessageComponent<?> prefix,String settingKey,OnlineMinecraftPlayer player, boolean current, boolean action){
        if(current == action){
            player.sendMessage(Messages.STAFF_STATUS_ALREADY, VariableSet.create()
                    .add("prefix",prefix)
                    .add("status",action)
                    .add("statusFormatted", action ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
        }else{
            player.sendMessage(Messages.STAFF_STATUS_CHANGE, VariableSet.create()
                    .add("prefix",prefix)
                    .add("status",action)
                    .add("statusFormatted", action ? Messages.STAFF_STATUS_LOGIN :  Messages.STAFF_STATUS_LOGOUT));
            player.setSetting("DKBans",settingKey,action);
        }
    }

    public static Pair<OnlineMinecraftPlayer, PlayerReport> checkAndGetTargetReport(CommandSender sender, String target0) {
        if(!(sender instanceof OnlineMinecraftPlayer)) {
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return null;
        }
        OnlineMinecraftPlayer target = McNative.getInstance().getLocal().getOnlinePlayer(target0);
        if(target == null) {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND,VariableSet.create()
                    .add("prefix",Messages.PREFIX_REPORT)
                    .add("player",target0));
            return null;
        }

        PlayerReport report = DKBans.getInstance().getReportManager().getReport(target.getUniqueId());
        if(report == null) {
            sender.sendMessage(Messages.REPORT_NOT_FOUND, VariableSet.create().addDescribed("player", target));
            return null;
        }
        return new Pair<>(target, report);
    }
}
