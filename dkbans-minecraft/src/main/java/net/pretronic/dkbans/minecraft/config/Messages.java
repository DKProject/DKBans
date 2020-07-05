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

import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import org.mcnative.common.text.Text;
import org.mcnative.common.text.components.MessageComponent;

public class Messages {

    public static MessageComponent<?> PREFIX  = Text.ofMessageKey("dkbans.prefix");
    public static MessageComponent<?> PREFIX_TEAMCHAT  = Text.ofMessageKey("dkbans.prefix.teamchat");
    public static MessageComponent<?> PREFIX_REPORT  = Text.ofMessageKey("dkbans.prefix.report");

    public static MessageComponent<?> ERROR_INTERNAL  = Text.ofMessageKey("dkbans.error.internal");
    public static MessageComponent<?> ERROR_INVALID_DURATION_FORMAT  = Text.ofMessageKey("dkbans.error.invalid.durationFormat");
    public static MessageComponent<?> ERROR_INVALID_NUMBER  = Text.ofMessageKey("dkbans.error.invalid.number");
    public static MessageComponent<?> ERROR_ONLY_PLAYER = Text.ofMessageKey("dkbans.error.onlyPlayer");
    public static MessageComponent<?> ERROR_NO_PERMISSIONS = Text.ofMessageKey("dkbans.error.noPermissions");

    public static MessageComponent<?> PLAYER_NOT_FOUND = Text.ofMessageKey("dkbans.player.notFound");
    public static MessageComponent<?> PLAYER_NOT_ONLINE = Text.ofMessageKey("dkbans.player.notOnline");
    public static MessageComponent<?> PLAYER_NOT_SELF = Text.ofMessageKey("dkbans.player.notSelf");
    public static MessageComponent<?> PLAYER_HAS_BYPASS = Text.ofMessageKey("dkbans.player.hasBypass");

    public static MessageComponent<?> REPORT_NOT_FOUND = Text.ofMessageKey("dkbans.report.notFound");

    public static MessageComponent<?> STAFF_STATUS_NOW = Text.ofMessageKey("dkbans.player.staff.status.now");
    public static MessageComponent<?> STAFF_STATUS_ALREADY = Text.ofMessageKey("dkbans.player.staff.status.already");
    public static MessageComponent<?> STAFF_STATUS_CHANGE = Text.ofMessageKey("dkbans.player.staff.status.change");
    public static MessageComponent<?> STAFF_STATUS_NOT = Text.ofMessageKey("dkbans.player.staff.status.not");
    public static MessageComponent<?> STAFF_STATUS_LOGIN = Text.ofMessageKey("dkbans.player.staff.status.login");
    public static MessageComponent<?> STAFF_STATUS_LOGOUT = Text.ofMessageKey("dkbans.player.staff.status.logout");

    public static MessageComponent<?> SERVER_NOT_FOUND = Text.ofMessageKey("dkbans.server.notFound");
    public static MessageComponent<?> SERVER_ALREADY_CONNECTED = Text.ofMessageKey("dkbans.server.alreadyConnected");
    public static MessageComponent<?> SERVER_CONNECTING = Text.ofMessageKey("dkbans.server.connecting");

    public static MessageComponent<?> PING_SELF = Text.ofMessageKey("dkbans.ping.self");
    public static MessageComponent<?> PING_OTHER = Text.ofMessageKey("dkbans.ping.other");

    public static MessageComponent<?> ONLINE_TIME_SELF = Text.ofMessageKey("dkbans.onlineTime.self");
    public static MessageComponent<?> ONLINE_TIME_OTHER = Text.ofMessageKey("dkbans.onlineTime.other");

    public static MessageComponent<?> PUNISH_TEMPLATE_NO_PERMISSION = Text.ofMessageKey("dkbans.punish.template.noPermission");
    public static MessageComponent<?> PUNISH_TEMPLATE_LIST = Text.ofMessageKey("dkbans.punish.template.list");

    public static MessageComponent<?> PUNISH_ALREADY_BANNED = Text.ofMessageKey("dkbans.punish.already.banned");
    public static MessageComponent<?> PUNISH_ALREADY_MUTED = Text.ofMessageKey("dkbans.punish.already.muted");

    public static MessageComponent<?> PUNISH_NOT_BANNED = Text.ofMessageKey("dkbans.punish.not.banned");
    public static MessageComponent<?> PUNISH_NOT_MUTED = Text.ofMessageKey("dkbans.punish.not.muted");

    public static MessageComponent<?> PUNISH_OVERRIDE = Text.ofMessageKey("dkbans.punish.override");

    public static MessageComponent<?> TEAMCHAT_MESSAGE_FORMAT = Text.ofMessageKey("dkbans.teamchat.messageFormat");

    public static MessageComponent<?> CHAT_FILTER_SPAM_TOFAST = Text.ofMessageKey("dkbans.chat.filter.toFast");
    public static MessageComponent<?> CHAT_FILTER_SPAM_REPEAT = Text.ofMessageKey("dkbans.chat.filter.repeat");

    public static MessageComponent<?> FILTER_BLOCKED_NAME = Text.ofMessageKey("dkbans.filter.blocked.name");
    public static MessageComponent<?> FILTER_BLOCKED_COMMAND = Text.ofMessageKey("dkbans.filter.blocked.command");
    public static MessageComponent<?> FILTER_BLOCKED_INSULTING = Text.ofMessageKey("dkbans.filter.blocked.insulting");
    public static MessageComponent<?> FILTER_BLOCKED_ADVERTISING = Text.ofMessageKey("dkbans.filter.blocked.advertising");

    public static MessageComponent<?> PUNISH_BAN_MESSAGE_PERMANENTLY = Text.ofMessageKey("dkbans.punish.ban.message.permanently");
    public static MessageComponent<?> PUNISH_BAN_MESSAGE_TEMPORARY = Text.ofMessageKey("dkbans.punish.ban.message.temporary");
    public static MessageComponent<?> PUNISH_BAN_NOTIFY = Text.ofMessageKey("dkbans.punish.ban.notify");

    public static MessageComponent<?> PUNISH_MUTE_MESSAGE_PERMANENTLY = Text.ofMessageKey("dkbans.punish.mute.message.permanently");
    public static MessageComponent<?> PUNISH_MUTE_MESSAGE_TEMPORARY = Text.ofMessageKey("dkbans.punish..mute.message.temporary");
    public static MessageComponent<?> PUNISH_MUTE_NOTIFY = Text.ofMessageKey("dkbans.punish.mute.notify");

    public static MessageComponent<?> COMMAND_PLAYER_INFO_OFFLINE = Text.ofMessageKey("dkbans.command.playerInfo.offline");
    public static MessageComponent<?> COMMAND_PLAYER_INFO_ONLINE = Text.ofMessageKey("dkbans.command.playerInfo.online");
    public static MessageComponent<?> COMMAND_PLAYER_INFO_SESSIONS = Text.ofMessageKey("dkbans.command.playerInfo.sessions");

    public static MessageComponent<?> COMMAND_HELP = Text.ofMessageKey("dkbans.command.help");

    public static MessageComponent<?> COMMAND_JUMPTO_HELP = Text.ofMessageKey("dkbans.command.jumpto.help");
    public static MessageComponent<?> COMMAND_KICK_HELP = Text.ofMessageKey("dkbans.command.kick.help");
    public static MessageComponent<?> COMMAND_NOTIFY_HELP = Text.ofMessageKey("dkbans.command.notify.help");
    public static MessageComponent<?> COMMAND_TEAM_CHAT_HELP = Text.ofMessageKey("dkbans.command.teamChat.help");


    public static MessageComponent<?> COMMAND_PUNISH_HELP = Text.ofMessageKey("dkbans.command.punish.help");

    public static MessageComponent<?> COMMAND_UNPUNISH_HELP = Text.ofMessageKey("dkbans.command.unpunish.help");

    public static MessageComponent<?> COMMAND_FILTER_HELP = Text.ofMessageKey("dkbans.command.filter.help");
    public static MessageComponent<?> COMMAND_FILTER_AFFILIATION_AREA_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.affiliationArea.notFound");
    public static MessageComponent<?> COMMAND_FILTER_OPERATION_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.operation.notFound");
    public static MessageComponent<?> COMMAND_FILTER_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.notFound");
    public static MessageComponent<?> COMMAND_FILTER_LIST = Text.ofMessageKey("dkbans.command.filter.list");
    public static MessageComponent<?> COMMAND_FILTER_DELETED = Text.ofMessageKey("dkbans.command.filter.deleted");
    public static MessageComponent<?> COMMAND_FILTER_CREATED = Text.ofMessageKey("dkbans.command.filter.created");

    public static MessageComponent<?> COMMAND_TEMPLATE_IMPORT = Text.ofMessageKey("dkbans.command.template.import");

    public static MessageComponent<?> COMMAND_REPORT_LOGIN_ALREADY = Text.ofMessageKey("dkbans.command.report.login.already");
    public static MessageComponent<?> COMMAND_REPORT_LOGIN_SUCCESS = Text.ofMessageKey("dkbans.command.report.login.success");

    public static MessageComponent<?> COMMAND_REPORT_LOGOUT_ALREADY = Text.ofMessageKey("dkbans.command.report.logout.already");
    public static MessageComponent<?> COMMAND_REPORT_LOGOUT_SUCCESS = Text.ofMessageKey("dkbans.command.report.logout.success");

    public static MessageComponent<?> COMMAND_REPORT_TOGGLE_LOGIN = Text.ofMessageKey("dkbans.command.report.toggle.login");
    public static MessageComponent<?> COMMAND_REPORT_TOGGLE_LOGOUT = Text.ofMessageKey("dkbans.command.report.toggle.logout");

    public static MessageComponent<?> COMMAND_REPORT_TEMPLATE_NOT_EXIST = Text.ofMessageKey("dkbans.command.report.template.notExist");
    public static MessageComponent<?> COMMAND_REPORT_ALREADY_REPORTED = Text.ofMessageKey("dkbans.command.report.already.reported");
    public static MessageComponent<?> COMMAND_REPORT_REPORTED = Text.ofMessageKey("dkbans.command.report.reported");
    public static MessageComponent<?> COMMAND_REPORT_NO_REASON = Text.ofMessageKey("dkbans.command.report.noReason");
    public static MessageComponent<?> COMMAND_REPORT_LIST_TEMPLATE = Text.ofMessageKey("dkbans.command.report.listTemplates");

    public static MessageComponent<?> COMMAND_REPORT_TAKE_USAGE = Text.ofMessageKey("dkbans.command.report.take.usage");
    public static MessageComponent<?> COMMAND_REPORT_TAKE = Text.ofMessageKey("dkbans.command.report.take");
    public static MessageComponent<?> COMMAND_REPORT_TAKE_ALREADY = Text.ofMessageKey("dkbans.command.report.take.already");

    public static MessageComponent<?> COMMAND_REPORT_ACCEPT_USAGE = Text.ofMessageKey("dkbans.command.report.accept.usage");
    public static MessageComponent<?> COMMAND_REPORT_ACCEPT_NOT_WATCHING = Text.ofMessageKey("dkbans.command.report.accept.notWatching");


    public static MessageComponent<?> COMMAND_PLAYER_NOTES_HELP = Text.ofMessageKey("dkbans.command.playerNotes.help");
    public static MessageComponent<?> COMMAND_PLAYER_NOTES_LIST = Text.ofMessageKey("dkbans.command.playerNotes.list");
    public static MessageComponent<?> COMMAND_PLAYER_NOTES_ADDED = Text.ofMessageKey("dkbans.command.playerNotes.added");


    public static MessageComponent<?> COMMAND_MY_HISTORY_POINTS = Text.ofMessageKey("dkbans.command.myHistoryPoints");


    public static MessageComponent<?> COMMAND_CHATLOG_USAGE = Text.ofMessageKey("dkbans.command.chatLog.usage");
    public static MessageComponent<?> COMMAND_CHATLOG_LIST = Text.ofMessageKey("dkbans.command.chatLog.list");

    public static MessageComponent<?> getPunishmentMessage(PlayerHistoryEntrySnapshot snapshot){
        if(snapshot.getPunishmentType() == PunishmentType.BAN){
            return snapshot.isPermanently() ? PUNISH_BAN_MESSAGE_PERMANENTLY : PUNISH_BAN_MESSAGE_TEMPORARY;
        }else if(snapshot.getPunishmentType() == PunishmentType.MUTE){
            return snapshot.isPermanently() ? PUNISH_MUTE_MESSAGE_PERMANENTLY : PUNISH_MUTE_MESSAGE_TEMPORARY;
        }
        throw new UnsupportedOperationException("Unknown punishment type");
    }
}
