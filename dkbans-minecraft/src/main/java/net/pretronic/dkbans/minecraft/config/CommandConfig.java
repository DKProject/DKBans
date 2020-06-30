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

package net.pretronic.dkbans.minecraft.config;

import net.pretronic.libraries.command.command.configuration.CommandConfiguration;

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

    public static CommandConfiguration COMMAND_TEAMCHAT = CommandConfiguration.newBuilder()
            .name("teamchat")
            .aliases("team","tc","sc","staffchat")
            .permission("dkbans.command.teamchat")
            .create();

    public static CommandConfiguration COMMAND_HELP = CommandConfiguration.newBuilder()
            .name("help")
            .aliases("?")
            .permission("dkbans.command.help")
            .create();

    public static CommandConfiguration COMMAND_FILTER = CommandConfiguration.newBuilder()
            .name("filter")
            .permission("dkbans.command.filter")
            .create();


    public static CommandConfiguration COMMAND_KICK = CommandConfiguration.newBuilder()
            .name("kick")
            .permission("dkbans.command.kick")
            .create();

    public static CommandConfiguration COMMAND_PLAYER_NOTES = CommandConfiguration.newBuilder()
            .name("playerNotes")
            .aliases("playerNote","notes","note")
            .permission("dkbans.command.playerNotes")
            .create();

    public static CommandConfiguration COMMAND_PUNISH_NOTIFY  = CommandConfiguration.newBuilder()
            .name("punishNotify")
            .aliases("banNotify","pn")
            .permission("dkbans.command.punishNotify")
            .create();

    public static CommandConfiguration COMMAND_NOTIFY  = CommandConfiguration.newBuilder()
            .name("notify")
            .permission("dkbans.command.notify")
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
    public static Map<String,CommandConfiguration> COMMAND_TEMPLATE_REPORT = new HashMap<>();


    static {
        COMMAND_TEMPLATE_PUNISHMENT.put("ban",CommandConfiguration.newBuilder()
                .name("ban")
                .aliases("mute")
                .permission("dkbans.ban")
                .create());
        COMMAND_TEMPLATE_REPORT.put("report", CommandConfiguration.newBuilder()
                .name("report")
                .permission("dkbans.report")
                .create());
    }
}
