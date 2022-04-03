/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.03.21, 20:11
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

package net.pretronic.dkbans.minecraft.commands.util;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.text.components.MessageComponent;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class CommandUtil {

    public static final Pattern IPADDRESS_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final boolean DKPERMS = McNative.getInstance().getPluginManager().getPlugin("DKPerms") != null;

    public static DKBansExecutor getExecutor(CommandSender sender) {
        if (sender instanceof MinecraftPlayer) {
            return ((MinecraftPlayer) sender).getAs(DKBansPlayer.class);
        } else {
            return DKBansExecutor.CONSOLE;
        }
    }

    public static MinecraftPlayer getPlayer(CommandSender sender, String name) {
        return getPlayer(sender, name, false);
    }

    public static MinecraftPlayer getPlayer(CommandSender sender, String name, boolean notSelf) {
        if (notSelf && sender.getName().equalsIgnoreCase(name)) {
            sender.sendMessage(Messages.PLAYER_NOT_SELF, VariableSet
                    .create().addDescribed("prefix", Messages.PREFIX));
            return null;
        }
        MinecraftPlayer player = McNative.getInstance().getPlayerManager().getPlayer(name);
        if (player == null) {
            sender.sendMessage(Messages.PLAYER_NOT_FOUND, VariableSet.create()
                    .add("prefix", Messages.PREFIX)
                    .add("name", name));
            return null;
        }
        return player;
    }

    public static boolean checkBypass(CommandSender sender, DKBansPlayer player) {
        if (player.hasBypass() && !sender.hasPermission(CommandConfig.PERMISSION_BYPASS_IGNORE)) {
            sender.sendMessage(Messages.PLAYER_HAS_BYPASS, VariableSet.create().addDescribed("player", player));
            McNative.getInstance().getNetwork().broadcast(CommandConfig.PERMISSION_BYPASS_IGNORE, Messages.PLAYER_HAS_BYPASS_NOTIFICATION
                    , VariableSet.create()
                            .addDescribed("sender", sender)
                            .add("target", player)
                            .add("prefix", Messages.PREFIX));
            return true;
        }
        return false;
    }

    public static String readStringFromArguments(String[] arguments, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arguments.length; i++) {
            builder.append(' ').append(arguments[i]);
        }
        return builder.substring(1);
    }

    public static Duration parseDuration(CommandSender sender, String duration) {
        try {
            return DurationProcessor.getStandard().parse(duration);
        } catch (IllegalArgumentException exception) {
            sender.sendMessage(Messages.ERROR_INVALID_DURATION_FORMAT, VariableSet.create()
                    .addDescribed("prefix", Messages.PREFIX)
                    .add("input", duration));
            return null;
        }
    }

    public static boolean canOverridePunish(CommandSender sender, DKBansPlayer player, PunishmentType type) {
        if (sender.hasPermission(CommandConfig.PERMISSION_PUNISH_OVERRIDE_ALL)) return true;
        if (sender.hasPermission(CommandConfig.PERMISSION_PUNISH_OVERRIDE_OWN)) {
            DKBansExecutor stuff = player.getHistory().getActiveEntry(type).getCurrent().getStaff();
            return stuff != null && stuff.equals(sender);
        }
        return false;
    }

    public static void sendAlreadyPunished(CommandSender sender, DKBansPlayer player, PunishmentType type, String override) {
        if (type == PunishmentType.BAN) {
            sender.sendMessage(Messages.PUNISH_ALREADY_BANNED, VariableSet.create().addDescribed("player", player));
        } else if (type == PunishmentType.MUTE) {
            sender.sendMessage(Messages.PUNISH_ALREADY_MUTED, VariableSet.create().addDescribed("player", player));
        }

        if (override != null && canOverridePunish(sender, player, type)) {
            sender.sendMessage(Messages.PUNISH_OVERRIDE, VariableSet.create()
                    .add("command", override));
        }
    }

    public static void sendNotPunished(CommandSender sender, DKBansPlayer player, PunishmentType type) {
        VariableSet variables = VariableSet.create()
                .addDescribed("player", player);
        if (type == PunishmentType.BAN) {
            sender.sendMessage(Messages.PUNISH_NOT_BANNED, variables);
        } else {
            sender.sendMessage(Messages.PUNISH_NOT_MUTED, variables);
        }
    }

    public static void sendPunishResultMessage(CommandSender sender, DKBansPlayer player, PlayerHistoryEntrySnapshot snapshot) {
        VariableSet variables = VariableSet.create()
                .addDescribed("snapshot", snapshot)
                .addDescribed("entry", snapshot.getEntry())
                .addDescribed("player", player);

        if (snapshot.getPunishmentType() == PunishmentType.BAN) {
            sender.sendMessage(Messages.PUNISH_SUCCESS_BAN, variables);
        } else if (snapshot.getPunishmentType() == PunishmentType.MUTE) {
            sender.sendMessage(Messages.PUNISH_SUCCESS_MUTE, variables);
        } else if (snapshot.getPunishmentType() == PunishmentType.KICK) {
            sender.sendMessage(Messages.PUNISH_SUCCESS_KICK, variables);
        } else if (snapshot.getPunishmentType() == PunishmentType.WARN) {
            sender.sendMessage(Messages.PUNISH_SUCCESS_WARN, variables);
        }
    }

    public static void sendUnpunishResultMessage(CommandSender sender, DKBansPlayer player, PlayerHistoryEntrySnapshot snapshot) {
        if (snapshot.getPunishmentType() == PunishmentType.BAN) {
            sender.sendMessage(Messages.UNPUNISH_SUCCESS_BAN, VariableSet.create()
                    .addDescribed("player", player));
        } else if (snapshot.getPunishmentType() == PunishmentType.MUTE) {
            sender.sendMessage(Messages.UNPUNISH_SUCCESS_MUTE, VariableSet.create()
                    .addDescribed("player", player));
        }
    }

    public static void changeLogin(MessageComponent<?> prefix, String settingKey, OnlineMinecraftPlayer player, boolean current, boolean action) {
        if (current == action) {
            player.sendMessage(Messages.STAFF_STATUS_ALREADY, VariableSet.create()
                    .add("prefix", prefix)
                    .add("status", action)
                    .add("statusFormatted", action ? Messages.STAFF_STATUS_LOGIN : Messages.STAFF_STATUS_LOGOUT));
        } else {
            player.sendMessage(Messages.STAFF_STATUS_CHANGE, VariableSet.create()
                    .add("prefix", prefix)
                    .add("status", action)
                    .add("statusFormatted", action ? Messages.STAFF_STATUS_LOGIN : Messages.STAFF_STATUS_LOGOUT));
            player.setSetting("DKBans", settingKey, action);
        }
    }

    public static boolean checkMaximumAllowedDuration(OnlineMinecraftPlayer sender, PunishmentType type, Duration duration) {
        if (DKPERMS) return DKPermsUtil.checkMaximumAllowedDuration(sender, type, duration);
        return false;
    }

    public static List<String> completeSimple(List<String> commands, String[] args) {
        if (args.length == 0) return commands;
        else if (args.length == 1)
            return Iterators.filter(commands, command -> command.startsWith(args[0].toLowerCase()));
        return Collections.emptyList();
    }

    public static List<String> completePlayer(String[] args) {
        if (args.length == 0) {
            return Iterators.map(McNative.getInstance().getLocal().getConnectedPlayers()
                    , ConnectedMinecraftPlayer::getName);
        } else if (args.length == 1) {
            return Iterators.map(McNative.getInstance().getLocal().getConnectedPlayers()
                    , ConnectedMinecraftPlayer::getName
                    , player -> player.getName().toLowerCase().startsWith(args[0].toLowerCase(Locale.ROOT)));
        } else return Collections.emptyList();
    }

}
