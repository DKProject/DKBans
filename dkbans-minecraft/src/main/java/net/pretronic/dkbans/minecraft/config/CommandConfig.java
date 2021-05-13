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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.command.configuration.DefaultCommandConfiguration;
import net.pretronic.libraries.document.annotations.DocumentKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandConfig {

    @DocumentKey("command.jumpTo")
    public static CommandConfiguration COMMAND_JUMP_TO = CommandConfiguration.newBuilder()
            .name("jumpto")
            .aliases("goto")
            .permission("dkbans.command.jumpto")
            .create();

    @DocumentKey("command.onlineTime")
    public static CommandConfiguration COMMAND_ONLINE_TIME = CommandConfiguration.newBuilder()
            .name("onlinetime")
            .permission("dkbans.command.onlinetime")
            .create();

    public static CommandConfiguration COMMAND_PING = CommandConfiguration.newBuilder()
            .name("ping")
            .permission("dkbans.command.ping")
            .create();

    @DocumentKey("command.playerInfo")
    public static CommandConfiguration COMMAND_PLAYER_INFO = CommandConfiguration.newBuilder()
            .name("playerinfo")
            .aliases("pinfo")
            .permission("dkbans.command.playerInfo")
            .create();

    @DocumentKey("command.playerSessions")
    public static CommandConfiguration COMMAND_PLAYER_SESSIONS = CommandConfiguration.newBuilder()
            .name("playerSessions")
            .aliases("playerSession", "psession","psessions")
            .permission("dkbans.command.playerSessions")
            .create();

    public static CommandConfiguration COMMAND_TEAMCHAT = CommandConfiguration.newBuilder()
            .name("teamchat")
            .aliases("team","tc","sc","staffchat")
            .permission("dkbans.teamchat.receive")
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

    @DocumentKey("command.playerNotes")
    public static CommandConfiguration COMMAND_PLAYER_NOTES = CommandConfiguration.newBuilder()
            .name("playerNotes")
            .aliases("playerNote","pnotes","pnote")
            .permission("dkbans.command.playerNotes")
            .create();

    @DocumentKey("command.punishNotify")
    public static CommandConfiguration COMMAND_PUNISH_NOTIFY  = CommandConfiguration.newBuilder()
            .name("punishNotify")
            .aliases("banNotify","pn")
            .permission("dkbans.punish.notify")
            .create();

    public static CommandConfiguration COMMAND_NOTIFY  = CommandConfiguration.newBuilder()
            .name("notify")
            .permission("dkbans.command.notify")
            .create();

    public static CommandConfiguration COMMAND_CHATLOG = CommandConfiguration.newBuilder()
            .name("chatlog")
            .permission("dkbans.command.chatlog")
            .create();

    public static CommandConfiguration COMMAND_HISTORY = CommandConfiguration.newBuilder()
            .name("history")
            .permission("dkbans.command.history")
            .create();

    @DocumentKey("command.resetHistory")
    public static CommandConfiguration COMMAND_RESET_HISTORY = CommandConfiguration.newBuilder()
            .name("resethistory")
            .permission("dkbans.command.resethistory")
            .create();

    @DocumentKey("command.myHistoryPoints")
    public static CommandConfiguration COMMAND_MY_HISTORY_POINTS = CommandConfiguration.newBuilder()
            .name("myhistorypoints")
            .permission("dkbans.command.myhistorypoints")
            .create();


    public static CommandConfiguration COMMAND_JOINME = CommandConfiguration.newBuilder()
            .name("joinme")
            .create();

    @DocumentKey("command.ipInfo")
    public static CommandConfiguration COMMAND_IP_INFO = CommandConfiguration.newBuilder()
            .name("ipInfo")
            .permission("dkbans.command.ipInfo")
            .create();

    @DocumentKey("command.ipBlock")
    public static CommandConfiguration COMMAND_IP_BLOCK = CommandConfiguration.newBuilder()
            .name("ipBlock")
            .permission("dkbans.command.ipBlock")
            .create();

    @DocumentKey("command.ipUnblock")
    public static CommandConfiguration COMMAND_IP_UNBLOCK = CommandConfiguration.newBuilder()
            .name("ipUnblock")
            .permission("dkbans.command.ipUnblock")
            .create();

    @DocumentKey("command.chatClear")
    public static CommandConfiguration COMMAND_CHAT_CLEAR = CommandConfiguration.newBuilder()
            .name("chatClear")
            .aliases("cc","ccheat")
            .permission("dkbans.command.chatclear")
            .create();

    public static CommandConfiguration COMMAND_BROADCAST = CommandConfiguration.newBuilder()
            .name("broadcast")
            .aliases("bc")
            .permission("dkbans.command.broadcast")
            .create();

    public static CommandConfiguration COMMAND_BROADCAST_GROUP = CommandConfiguration.newBuilder()
            .name("broadcastgroup")
            .aliases("bcg")
            .permission("dkbans.command.broadcast.group")
            .create();

    public static Collection<PunishmentTypeConfiguration> COMMAND_PUNISH_DIRECT = new ArrayList<>();
    public static Map<String,CommandConfiguration> COMMAND_PUNISH_TEMPLATE = new HashMap<>();

    public static CommandConfiguration COMMAND_REPORT = CommandConfiguration.newBuilder()
            .name("report")
            .permission("dkbans.command.report")
            .create();

    @DocumentKey("command.punishInfo")
    public static CommandConfiguration COMMAND_PUNISH_INFO_CONFIGURATION = CommandConfiguration.newBuilder()
            .name("punishInfo")
            .aliases("baninfo","muteinfo","binfo","minfo")
            .permission("dkbans.command.punishInfo")
            .create();

    @DocumentKey("command.punishEdit")
    public static CommandConfiguration COMMAND_PUNISH_EDIT_CONFIGURATION = CommandConfiguration.newBuilder()
            .name("punishedit")
            .aliases("banedit","muteedit")
            .permission("dkbans.command.punishEdit")
            .create();

    public static String COMMAND_REPORT_TEMPLATE_NAME = "report";

    public static String COMMAND_REPORT_MODE = "TEMPLATE";

    static {

        //Ban
        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"permanentban"
                ,"dkbans.command.punish.ban.permanently"
                ,new String[]{"permaban","pban"}
                ,"BAN","NETWORK","COMMAND_PERMANENTLY"));

        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"temporaryban"
                ,"dkbans.command.punish.ban.temporary"
                ,new String[]{"tempban","tban"}
                ,"BAN","NETWORK","COMMAND_TEMPORARY"));

        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"unban"
                ,"dkbans.command.punish.ban.unban"
                ,new String[]{}
                ,"BAN",null,"COMMAND_REVOKE"));

        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"banlist"
                ,"dkbans.command.punish.ban.list"
                ,new String[]{"blist","bans"}
                ,"BAN",null,"COMMAND_LIST"));


        //Mute
        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"permanentmute"
                ,"dkbans.command.punish.mute.permanently"
                ,new String[]{"permamute","pmute"}
                ,"MUTE","NETWORK","COMMAND_PERMANENTLY"));

        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"temporarymute"
                ,"dkbans.command.punish.mute.temporary"
                ,new String[]{"tempmute","tmute"}
                ,"MUTE","NETWORK","COMMAND_TEMPORARY"));

        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"unmute"
                ,"dkbans.command.punish.mute.unmute"
                ,new String[]{}
                ,"MUTE",null,"COMMAND_REVOKE"));

        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"mutelist"
                ,"dkbans.command.punish.mute.list"
                ,new String[]{"mlist","mutes"}
                ,"MUTE",null,"COMMAND_LIST"));


        //Kick
        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"kick"
                ,"dkbans.command.punish.kick"
                ,new String[]{}
                ,"KICK","NETWORK","COMMAND_ONE_TIME"));

        //Warn
        COMMAND_PUNISH_DIRECT.add(new PunishmentTypeConfiguration(true,"warn"
                ,"dkbans.command.punish.warn"
                ,new String[]{}
                ,"WARN","NETWORK","COMMAND_ONE_TIME"));

        COMMAND_PUNISH_TEMPLATE.put("ban",CommandConfiguration.newBuilder()
                .name("ban")
                .aliases("mute")
                .permission("dkbans.ban")
                .create());
    }

    public static final String PERMISSION_ADMIN = "dkbans.admin";

    public static final String PERMISSION_BYPASS = "dkbans.bypass";
    @DocumentKey("permission.bypassIgnore")
    public static final String PERMISSION_BYPASS_IGNORE = "dkbans.bypass.ignore";

    public static final String PERMISSION_CHAT_BYPASS = "dkbans.chat.bypass";
    @DocumentKey("permission.chat.bypassTabComplete")
    public static final String PERMISSION_CHAT_BYPASS_TAB_COMPLETION = "dkbans.chat.bypass.tabComplete";
    public static final String PERMISSION_CHAT_NOTIFICATION = "dkbans.chat.notification";


    public static final String PERMISSION_PUNISH_OVERRIDE_OWN = "dkbans.punish.override";
    public static final String PERMISSION_PUNISH_OVERRIDE_ALL = "dkbans.bypass.override.all";
    public static final String PERMISSION_PUNISH_NOTIFY = "dkbans.punish.notify";

    public static final String PERMISSION_COMMAND_TEAMCHAT_RECEIVE = "dkbans.teamchat.receive";
    public static final String PERMISSION_COMMAND_TEAMCHAT_SEND = "dkbans.teamchat.send";

    public static final String PERMISSION_COMMAND_REPORT_STAFF = "dkbans.report.staff";

    public static final String PERMISSION_COMMAND_JOINME = "dkbans.joinme";
    @DocumentKey("permission.command.chatClear.all")
    public static final String PERMISSION_COMMAND_CHAT_CLEAR_ALL = "dkbans.chatClear.all";


    public final static class PunishmentTypeConfiguration extends DefaultCommandConfiguration {

        private final String punishmentType;
        private final String historyType;
        private final String commandType;
        private final String scope;

        public PunishmentTypeConfiguration(boolean enabled, String name, String permission
                , String[] aliases, String punishmentType, String historyType, String commandType) {
            super(enabled, name, permission, null, aliases);
            this.punishmentType = punishmentType;
            this.historyType = historyType;
            this.commandType = commandType;
            this.scope = "GLOBAL:GLOBAL";
        }

        public PunishmentType getPunishmentType() {
            return PunishmentType.getPunishmentType(punishmentType);
        }

        public PlayerHistoryType getHistoryType() {
            PlayerHistoryType type = DKBans.getInstance().getHistoryManager().getHistoryType(historyType);
            if(type == null) return DKBans.getInstance().getHistoryManager().createHistoryType(historyType);
            return type;
        }

        public String getCommandType() {
            return commandType;
        }

        public DKBansScope getScope() {
            String[] parts = scope.split(":");
            if(parts.length == 2){
                return DKBansScope.of(parts[0],parts[1]);
            }
            throw new IllegalArgumentException("Invalid scope format (key:name)");
        }
    }
}
