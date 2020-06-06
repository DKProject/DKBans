package net.pretronic.dkbans.minecraft.config;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.document.annotations.DocumentKey;
import net.pretronic.libraries.document.annotations.OnDocumentConfigurationLoad;

import java.util.HashMap;
import java.util.Map;

public class CommandConfig {

    public static CommandConfiguration COMMAND_JUMP_TO = CommandConfiguration.newBuilder()
            .name("jumpto")
            .aliases("goto")
            .permission("dkbans.command.jumpto")
            .create();

    public static CommandConfiguration COMMAND_ONLINE_TIME = CommandConfiguration.newBuilder()
            .name("onlinetime")
            .permission("dkbans.command.onlinetime")
            .create();

    public static CommandConfiguration COMMAND_PING = CommandConfiguration.newBuilder()
            .name("ping")
            .permission("dkbans.command.ping")
            .create();

    public static CommandConfiguration COMMAND_PLAYER_INFO = CommandConfiguration.newBuilder()
            .name("playerinfo")
            .aliases("pinfo")
            .permission("dkbans.command.playerInfo")
            .create();

    public static CommandConfiguration COMMAND_KICK = CommandConfiguration.newBuilder()
            .name("kick")
            .permission("dkbans.command.kick")
            .create();

    //Punishment Commands

    public static CommandConfiguration COMMAND_PUNISH_KICK = CommandConfiguration.newBuilder()
            .name("kick")
            .aliases("tempmute","tmute")
            .permission("dkbans.command.punish.tempmute")
            .create();


    public static CommandConfiguration COMMAND_PUNISH_BAN_PERMANENT = CommandConfiguration.newBuilder()
            .name("permanentban")
            .aliases("permaban","pban")
            .permission("dkbans.command.punish.permaban")
            .create();

    public static CommandConfiguration COMMAND_PUNISH_BAN_TEMPORARY = CommandConfiguration.newBuilder()
            .name("temporaryban")
            .aliases("tempban","tban")
            .permission("dkbans.command.punish.tempban")
            .create();


    public static CommandConfiguration COMMAND_PUNISH_MUTE_PERMANENT = CommandConfiguration.newBuilder()
            .name("permanentmute")
            .aliases("permamute","pmute")
            .permission("dkbans.command.punish.permamute")
            .create();

    public static CommandConfiguration COMMAND_PUNISH_MUTE_TEMPORARY = CommandConfiguration.newBuilder()
            .name("temporarymute")
            .aliases("tempmute","tmute")
            .permission("dkbans.command.punish.tempmute")
            .create();

    public static Map<String,CommandConfiguration> COMMAND_TEMPLATE_PUNISHMENT = new HashMap<>();

    static {
        COMMAND_TEMPLATE_PUNISHMENT.put("ban",CommandConfiguration.newBuilder()
                .name("ban")
                .aliases("mute")
                .permission("dkbans.ban")
                .create());
    }
}
