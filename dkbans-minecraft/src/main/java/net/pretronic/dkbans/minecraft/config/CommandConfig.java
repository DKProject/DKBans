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

    public static CommandConfiguration COMMAND_PLAYER_NOTES = CommandConfiguration.newBuilder()
            .name("playerNotes")
            .aliases("playerNote","pnotes","pnote")
            .permission("dkbans.command.playerNotes")
            .create();

    @DocumentKey("command.punishNotify")
    public static CommandConfiguration COMMAND_PUNISH_NOTIFY  = CommandConfiguration.newBuilder()
            .name("punishNotify")
            .aliases("banNotify","pn")
            .permission("dkbans.command.punishNotify")
            .create();

    public static CommandConfiguration COMMAND_NOTIFY  = CommandConfiguration.newBuilder()
            .name("notify")
            .permission("dkbans.command.notify")
            .create();

    public static CommandConfiguration COMMAND_CHATLOG = CommandConfiguration.newBuilder()
            .name("chatlog")
            .permission("dkbans.command.chatlog")
            .create();

    public static CommandConfiguration COMMAND_TEMPLATE = CommandConfiguration.newBuilder()
            .name("template")
            .permission("dkbans.command.template")
            .create();

    public static CommandConfiguration COMMAND_MY_HISTORY_POINTS = CommandConfiguration.newBuilder()
            .name("myhistorypoints")
            .permission("dkbans.command.myhistorypoints")
            .create();

    public static Collection<PunishmentTypeConfiguration> COMMAND_PUNISH_DIRECT = new ArrayList<>();
    public static Map<String,CommandConfiguration> COMMAND_PUNISH_TEMPLATE = new HashMap<>();


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
                ,new String[]{"blist"}
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
                ,new String[]{"mlist"}
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

    public final static class PunishmentTypeConfiguration extends DefaultCommandConfiguration {

        private String punishmentType;
        private String historyType;
        private String commandType;
        private String scope;

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
                return new DKBansScope(parts[0],parts[1]);
            }
            throw new IllegalArgumentException("Invalid scope format (key:name)");
        }
    }
}
