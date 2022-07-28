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
import org.mcnative.runtime.api.text.Text;
import org.mcnative.runtime.api.text.components.MessageComponent;

public class Messages {

    public static MessageComponent<?> PREFIX = Text.ofMessageKey("dkbans.prefix");
    public static MessageComponent<?> PREFIX_NETWORK  = Text.ofMessageKey("dkbans.prefix.network");
    public static MessageComponent<?> PREFIX_TEAMCHAT  = Text.ofMessageKey("dkbans.prefix.teamchat");
    public static MessageComponent<?> PREFIX_REPORT  = Text.ofMessageKey("dkbans.prefix.report");
    public static MessageComponent<?> PREFIX_CHAT  = Text.ofMessageKey("dkbans.prefix.chat");

    public static MessageComponent<?> ERROR_INTERNAL  = Text.ofMessageKey("dkbans.error.internal");
    public static MessageComponent<?> ERROR_INVALID_DURATION_FORMAT  = Text.ofMessageKey("dkbans.error.invalid.durationFormat");
    public static MessageComponent<?> ERROR_INVALID_NUMBER  = Text.ofMessageKey("dkbans.error.invalid.number");
    public static MessageComponent<?> ERROR_INVALID_BOOLEAN  = Text.ofMessageKey("dkbans.error.invalid.boolean");
    public static MessageComponent<?> ERROR_INVALID_ID = Text.ofMessageKey("dkbans.error.invalid.id");
    public static MessageComponent<?> ERROR_ONLY_PLAYER = Text.ofMessageKey("dkbans.error.onlyPlayer");
    public static MessageComponent<?> ERROR_ONLY_CONSOLE = Text.ofMessageKey("dkbans.error.onlyConsole");
    public static MessageComponent<?> ERROR_NO_PERMISSIONS = Text.ofMessageKey("dkbans.error.noPermissions");
    public static MessageComponent<?> ERROR_INVALID_IP_ADDRESS = Text.ofMessageKey("dkbans.error.invalidIpAddress");
    public static MessageComponent<?> ERROR_INVALID_TEMPLATE_SPECIFIER_FORMAT = Text.ofMessageKey("dkbans.error.invalid.templateSpecifierFormat");
    public static MessageComponent<?> ERROR_TEMPLATE_GROUP_NOT_EXISTS = Text.ofMessageKey("dkbans.error.templateGroup.notExists");
    public static MessageComponent<?> ERROR_TEMPLATE_NOT_EXISTS = Text.ofMessageKey("dkbans.error.template.notExists");
    public static MessageComponent<?> ERROR_INVALID_BROADCAST_ORDER = Text.ofMessageKey("dkbans.error.invalidBroadcastOrder");
    public static MessageComponent<?> ERROR_INVALID_SCOPE = Text.ofMessageKey("dkbans.error.invalidScope");
    public static MessageComponent<?> ERROR_PUNISHMENT_TO_LONG = Text.ofMessageKey("dkbans.error.punishment.toLong");

    public static MessageComponent<?> PLAYER_NOT_FOUND = Text.ofMessageKey("dkbans.player.notFound");
    public static MessageComponent<?> PLAYER_NOT_ONLINE = Text.ofMessageKey("dkbans.player.notOnline");
    public static MessageComponent<?> PLAYER_NOT_SELF = Text.ofMessageKey("dkbans.player.notSelf");
    public static MessageComponent<?> PLAYER_HAS_BYPASS = Text.ofMessageKey("dkbans.player.hasBypass");
    public static MessageComponent<?> PLAYER_HAS_BYPASS_NOTIFICATION = Text.ofMessageKey("dkbans.player.hasBypass.notification");

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
    public static MessageComponent<?> ONLINE_TIME_TOP = Text.ofMessageKey("dkbans.onlineTime.top");

    public static MessageComponent<?> PUNISH_TEMPLATE_NO_PERMISSION = Text.ofMessageKey("dkbans.punish.template.noPermission");
    public static MessageComponent<?> PUNISH_TEMPLATE_LIST = Text.ofMessageKey("dkbans.punish.template.list");

    public static MessageComponent<?> PUNISH_ALREADY_BANNED = Text.ofMessageKey("dkbans.punish.already.banned");
    public static MessageComponent<?> PUNISH_ALREADY_MUTED = Text.ofMessageKey("dkbans.punish.already.muted");

    public static MessageComponent<?> PUNISH_NOT_BANNED = Text.ofMessageKey("dkbans.punish.not.banned");
    public static MessageComponent<?> PUNISH_NOT_MUTED = Text.ofMessageKey("dkbans.punish.not.muted");

    public static MessageComponent<?> PUNISH_OVERRIDE = Text.ofMessageKey("dkbans.punish.override");

    public static MessageComponent<?> PUNISH_SUCCESS_BAN = Text.ofMessageKey("dkbans.punish.success.ban");
    public static MessageComponent<?> PUNISH_SUCCESS_MUTE = Text.ofMessageKey("dkbans.punish.success.mute");
    public static MessageComponent<?> PUNISH_SUCCESS_KICK = Text.ofMessageKey("dkbans.punish.success.kick");
    public static MessageComponent<?> PUNISH_SUCCESS_WARN = Text.ofMessageKey("dkbans.punish.success.warn");

    public static MessageComponent<?> UNPUNISH_SUCCESS_BAN = Text.ofMessageKey("dkbans.unpunish.success.ban");
    public static MessageComponent<?> UNPUNISH_SUCCESS_MUTE = Text.ofMessageKey("dkbans.unpunish.success.mute");
    public static MessageComponent<?> UNPUNISH_SUCCESS_KICK = Text.ofMessageKey("dkbans.unpunish.success.kick");
    public static MessageComponent<?> UNPUNISH_SUCCESS_WARN = Text.ofMessageKey("dkbans.unpunish.success.warn");

    public static MessageComponent<?> TEAMCHAT_MESSAGE_FORMAT = Text.ofMessageKey("dkbans.teamchat.messageFormat");
    public static MessageComponent<?> TEAMCHAT_LIST = Text.ofMessageKey("dkbans.teamchat.list");

    public static MessageComponent<?> CHAT_FILTER_SPAM_TOFAST = Text.ofMessageKey("dkbans.chat.filter.toFast");
    public static MessageComponent<?> CHAT_FILTER_SPAM_REPEAT = Text.ofMessageKey("dkbans.chat.filter.repeat");
    public static MessageComponent<?> CHAT_FILTER_SPAM_CAPSLOCK = Text.ofMessageKey("dkbans.chat.filter.capslock");

    public static MessageComponent<?> FILTER_BLOCKED_NAME = Text.ofMessageKey("dkbans.filter.blocked.name");
    public static MessageComponent<?> FILTER_BLOCKED_COMMAND = Text.ofMessageKey("dkbans.filter.blocked.command");
    public static MessageComponent<?> FILTER_BLOCKED_INSULTING = Text.ofMessageKey("dkbans.filter.blocked.insulting");
    public static MessageComponent<?> FILTER_BLOCKED_ADVERTISING = Text.ofMessageKey("dkbans.filter.blocked.advertising");
    public static MessageComponent<?> FILTER_BLOCKED_NOTIFICATION = Text.ofMessageKey("dkbans.filter.blocked.notification");

    public static MessageComponent<?> PUNISH_MESSAGE_BAN_PERMANENTLY = Text.ofMessageKey("dkbans.punish.message.ban.permanently");
    public static MessageComponent<?> PUNISH_MESSAGE_BAN_TEMPORARY = Text.ofMessageKey("dkbans.punish.message.ban.temporary");
    public static MessageComponent<?> PUNISH_MESSAGE_MUTE_PERMANENTLY = Text.ofMessageKey("dkbans.punish.message.mute.permanently");
    public static MessageComponent<?> PUNISH_MESSAGE_MUTE_TEMPORARY = Text.ofMessageKey("dkbans.punish.message.mute.temporary");
    public static MessageComponent<?> PUNISH_MESSAGE_KICK= Text.ofMessageKey("dkbans.punish.message.kick");
    public static MessageComponent<?> PUNISH_MESSAGE_WARN_CHAT = Text.ofMessageKey("dkbans.punish.message.warn.chat");

    public static MessageComponent<?> PUNISH_LIST_BAN = Text.ofMessageKey("dkbans.punish.list.ban");
    public static MessageComponent<?> PUNISH_LIST_MUTE = Text.ofMessageKey("dkbans.punish.list.mute");

    public static MessageComponent<?> PUNISH_NOTIFY_KICK = Text.ofMessageKey("dkbans.punish.notify.kick");
    public static MessageComponent<?> PUNISH_NOTIFY_MUTE = Text.ofMessageKey("dkbans.punish.notify.mute");
    public static MessageComponent<?> PUNISH_NOTIFY_BAN = Text.ofMessageKey("dkbans.punish.notify.ban");
    public static MessageComponent<?> PUNISH_NOTIFY_WARN= Text.ofMessageKey("dkbans.punish.notify.warn");
    public static MessageComponent<?> PUNISH_NOTIFY_EDIT = Text.ofMessageKey("dkbans.punish.notify.edit");

    public static MessageComponent<?> PUNISH_NOT_FOUND = Text.ofMessageKey("dkbans.punish.notFound");
    public static MessageComponent<?> PUNISH_EMPTY = Text.ofMessageKey("dkbans.punish.empty");

    public static MessageComponent<?> PUNISH_ADDRESS_BLOCK = Text.ofMessageKey("dkbans.punish.addressBlock");

    public static MessageComponent<?> REPORT_COUNT_INFO = Text.ofMessageKey("dkbans.report.countInfo");
    public static MessageComponent<?> REPORT_ACCEPTED = Text.ofMessageKey("dkbans.report.accepted");
    public static MessageComponent<?> REPORT_DECLINED = Text.ofMessageKey("dkbans.report.declined");
    public static MessageComponent<?> REPORT_NOTIFY = Text.ofMessageKey("dkbans.report.notify");

    public static MessageComponent<?> COMMAND_HISTORY_HELP = Text.ofMessageKey("dkbans.command.history.help");
    public static MessageComponent<?> COMMAND_HISTORY_INFO = Text.ofMessageKey("dkbans.command.history.info");
    public static MessageComponent<?> COMMAND_HISTORY_VERSION_LIST = Text.ofMessageKey("dkbans.command.history.version.list");
    public static MessageComponent<?> COMMAND_HISTORY_VERSION_INFO = Text.ofMessageKey("dkbans.command.history.version.info");
    public static MessageComponent<?> COMMAND_HISTORY_ENTRY_NOT_FOUND = Text.ofMessageKey("dkbans.command.history.notFound");

    public static MessageComponent<?> COMMAND_HISTORY_LIST = Text.ofMessageKey("dkbans.command.history.list");

    public static MessageComponent<?> COMMAND_RESET_HISTORY_HELP = Text.ofMessageKey("dkbans.command.resetHistory.help");
    public static MessageComponent<?> COMMAND_RESET_HISTORY_MANY = Text.ofMessageKey("dkbans.command.resetHistory.many");
    public static MessageComponent<?> COMMAND_RESET_HISTORY_ONE = Text.ofMessageKey("dkbans.command.resetHistory.one");

    public static MessageComponent<?> COMMAND_PLAYER_INFO_HELP = Text.ofMessageKey("dkbans.command.playerInfo.help");
    public static MessageComponent<?> COMMAND_PLAYER_INFO_OFFLINE = Text.ofMessageKey("dkbans.command.playerInfo.offline");
    public static MessageComponent<?> COMMAND_PLAYER_INFO_ONLINE = Text.ofMessageKey("dkbans.command.playerInfo.online");

    public static MessageComponent<?> COMMAND_PLAYER_SESSIONS_HELP = Text.ofMessageKey("dkbans.command.playerSessions.help");
    public static MessageComponent<?> COMMAND_PLAYER_SESSIONS_INFO = Text.ofMessageKey("dkbans.command.playerSessions.info");

    public static MessageComponent<?> COMMAND_HELP = Text.ofMessageKey("dkbans.command.help");

    public static MessageComponent<?> COMMAND_JUMPTO_HELP = Text.ofMessageKey("dkbans.command.jumpto.help");
    public static MessageComponent<?> COMMAND_NOTIFY_HELP = Text.ofMessageKey("dkbans.command.notify.help");
    public static MessageComponent<?> COMMAND_TEAM_CHAT_HELP = Text.ofMessageKey("dkbans.command.teamChat.help");
    public static MessageComponent<?> COMMAND_ALERT_HELP = Text.ofMessageKey("dkbans.command.alert.help");


    public static MessageComponent<?> COMMAND_PUNISH_HELP_TEMPORARY = Text.ofMessageKey("dkbans.command.punish.help.temporary");
    public static MessageComponent<?> COMMAND_PUNISH_HELP_PERMANENTLY = Text.ofMessageKey("dkbans.command.punish.help.permanently");
    public static MessageComponent<?> COMMAND_PUNISH_HELP_ONE_TIME = Text.ofMessageKey("dkbans.command.punish.help.oneTime");

    public static MessageComponent<?> COMMAND_UNPUNISH_HELP = Text.ofMessageKey("dkbans.command.unpunish.help");

    public static MessageComponent<?> COMMAND_PUNISH_INFO_HELP = Text.ofMessageKey("dkbans.command.punishinfo.help");
    public static MessageComponent<?> COMMAND_PUNISH_INFO_MULTIPLE = Text.ofMessageKey("dkbans.command.punishinfo.multiple");

    public static MessageComponent<?> COMMAND_PUNISH_EDIT_HELP = Text.ofMessageKey("dkbans.command.punishedit.help");
    public static MessageComponent<?> COMMAND_PUNISH_EDIT_MULTIPLE = Text.ofMessageKey("dkbans.command.punishedit.multiple");
    public static MessageComponent<?> COMMAND_PUNISH_EDIT_DONE = Text.ofMessageKey("dkbans.command.punishedit.done");

    public static MessageComponent<?> COMMAND_PUNISH_NOTES_HELP = Text.ofMessageKey("dkbans.command.punishNotes.help");
    public static MessageComponent<?> COMMAND_PUNISH_NOTES_LIST = Text.ofMessageKey("dkbans.command.punishNotes.list");
    public static MessageComponent<?> COMMAND_PUNISH_NOTES_ADDED = Text.ofMessageKey("dkbans.command.punishNotes.added");
    public static MessageComponent<?> COMMAND_PUNISH_NOTES_CLEARED = Text.ofMessageKey("dkbans.command.punishNotes.cleared");

    public static MessageComponent<?> COMMAND_FILTER_HELP = Text.ofMessageKey("dkbans.command.filter.help");
    public static MessageComponent<?> COMMAND_FILTER_RELOADED = Text.ofMessageKey("dkbans.command.filter.reloaded");
    public static MessageComponent<?> COMMAND_FILTER_AFFILIATION_AREA_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.affiliationArea.notFound");
    public static MessageComponent<?> COMMAND_FILTER_OPERATION_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.operation.notFound");
    public static MessageComponent<?> COMMAND_FILTER_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.notFound");
    public static MessageComponent<?> COMMAND_FILTER_LIST = Text.ofMessageKey("dkbans.command.filter.list");
    public static MessageComponent<?> COMMAND_FILTER_DELETED = Text.ofMessageKey("dkbans.command.filter.deleted");
    public static MessageComponent<?> COMMAND_FILTER_CREATED = Text.ofMessageKey("dkbans.command.filter.created");

    public static MessageComponent<?> COMMAND_TEMPLATE_IMPORT = Text.ofMessageKey("dkbans.command.dkbans.template.import");
    public static MessageComponent<?> COMMAND_TEMPLATE_EXPORT = Text.ofMessageKey("dkbans.command.dkbans.template.export");

    public static MessageComponent<?> COMMAND_REPORT_HELP = Text.ofMessageKey("dkbans.command.report.help");
    public static MessageComponent<?> COMMAND_REPORT_TEMPLATE_NOT_EXIST = Text.ofMessageKey("dkbans.command.report.template.notExist");
    public static MessageComponent<?> COMMAND_REPORT_ALREADY_REPORTED = Text.ofMessageKey("dkbans.command.report.already.reported");
    public static MessageComponent<?> COMMAND_REPORT_REPORTED = Text.ofMessageKey("dkbans.command.report.reported");
    public static MessageComponent<?> COMMAND_REPORT_LIST_TEMPLATE = Text.ofMessageKey("dkbans.command.report.listTemplates");

    public static MessageComponent<?> COMMAND_REPORT_TAKE_USAGE = Text.ofMessageKey("dkbans.command.report.take.usage");
    public static MessageComponent<?> COMMAND_REPORT_TAKE = Text.ofMessageKey("dkbans.command.report.take");
    public static MessageComponent<?> COMMAND_REPORT_TAKE_NO_PERMISSION = Text.ofMessageKey("dkbans.command.report.take.noPermission");
    public static MessageComponent<?> COMMAND_REPORT_NOT_REPORTED = Text.ofMessageKey("dkbans.command.report.take.notReported");
    public static MessageComponent<?> COMMAND_REPORT_TAKE_ALREADY = Text.ofMessageKey("dkbans.command.report.take.already");

    public static MessageComponent<?> COMMAND_REPORT_NOT_WATCHING = Text.ofMessageKey("dkbans.command.report.notWatching");

    public static MessageComponent<?> COMMAND_REPORT_DECLINE = Text.ofMessageKey("dkbans.command.report.decline");
    public static MessageComponent<?> COMMAND_REPORT_ACCEPTED = Text.ofMessageKey("dkbans.command.report.accepted");

    public static MessageComponent<?> COMMAND_REPORT_LIST = Text.ofMessageKey("dkbans.command.report.list");

    public static MessageComponent<?> COMMAND_PLAYER_NOTES_HELP = Text.ofMessageKey("dkbans.command.playerNotes.help");
    public static MessageComponent<?> COMMAND_PLAYER_NOTES_LIST = Text.ofMessageKey("dkbans.command.playerNotes.list");
    public static MessageComponent<?> COMMAND_PLAYER_NOTES_ADDED = Text.ofMessageKey("dkbans.command.playerNotes.added");
    public static MessageComponent<?> COMMAND_PLAYER_NOTES_CLEARED = Text.ofMessageKey("dkbans.command.playerNotes.cleared");

    public static MessageComponent<?> COMMAND_MY_HISTORY_POINTS = Text.ofMessageKey("dkbans.command.myHistoryPoints");


    public static MessageComponent<?> COMMAND_CHATLOG_USAGE = Text.ofMessageKey("dkbans.command.chatlog.usage");
    public static MessageComponent<?> COMMAND_CHATLOG_ENTRY_NOT_FOUND = Text.ofMessageKey("dkbans.command.chatlogEntry.notFound");
    public static MessageComponent<?> COMMAND_CHATLOG_PLAYER_LIST = Text.ofMessageKey("dkbans.command.chatlog.player.list");
    public static MessageComponent<?> COMMAND_CHATLOG_SERVER_LIST = Text.ofMessageKey("dkbans.command.chatlog.server.list");
    public static MessageComponent<?> COMMAND_CHATLOG_ENTRY = Text.ofMessageKey("dkbans.command.chatlog.entry");


    public static MessageComponent<?> COMMAND_DKBANS_HELP = Text.ofMessageKey("dkbans.command.dkbans.help");
    public static MessageComponent<?> COMMAND_DKBANS_MIGRATE_HELP = Text.ofMessageKey("dkbans.command.dkbans.migrate.help");


    public static MessageComponent<?> COMMAND_JOINME_NOT_ENOUGH_AMOUNT = Text.ofMessageKey("dkbans.command.joinme.notEnoughAmount");
    public static MessageComponent<?> COMMAND_JOINME_USAGE = Text.ofMessageKey("dkbans.command.joinme.usage");
    public static MessageComponent<?> COMMAND_JOINME_NOT_EXIST = Text.ofMessageKey("dkbans.command.joinme.notExists");
    public static MessageComponent<?> COMMAND_JOINME_DISABLED = Text.ofMessageKey("dkbans.command.joinme.disabled");

    public static MessageComponent<?> COMMAND_JOINME_LINE_1 = Text.ofMessageKey("dkbans.command.joinme.line1");
    public static MessageComponent<?> COMMAND_JOINME_LINE_2 = Text.ofMessageKey("dkbans.command.joinme.line2");
    public static MessageComponent<?> COMMAND_JOINME_LINE_3 = Text.ofMessageKey("dkbans.command.joinme.line3");
    public static MessageComponent<?> COMMAND_JOINME_LINE_4 = Text.ofMessageKey("dkbans.command.joinme.line4");
    public static MessageComponent<?> COMMAND_JOINME_LINE_5 = Text.ofMessageKey("dkbans.command.joinme.line5");
    public static MessageComponent<?> COMMAND_JOINME_LINE_6 = Text.ofMessageKey("dkbans.command.joinme.line6");
    public static MessageComponent<?> COMMAND_JOINME_LINE_7 = Text.ofMessageKey("dkbans.command.joinme.line7");
    public static MessageComponent<?> COMMAND_JOINME_LINE_8 = Text.ofMessageKey("dkbans.command.joinme.line8");
    public static MessageComponent<?> COMMAND_JOINME_LINE_9 = Text.ofMessageKey("dkbans.command.joinme.line9");
    public static MessageComponent<?> COMMAND_JOINME_LINE_10 = Text.ofMessageKey("dkbans.command.joinme.line10");


    public static MessageComponent<?> COMMAND_IP_INFO_HELP = Text.ofMessageKey("dkbans.command.ip.info.help");
    public static MessageComponent<?> COMMAND_IP_INFO_ADDRESS = Text.ofMessageKey("dkbans.command.ip.info.address");
    public static MessageComponent<?> COMMAND_IP_INFO_ADDRESS_DETAILS = Text.ofMessageKey("dkbans.command.ip.info.address.details");
    public static MessageComponent<?> COMMAND_IP_INFO_PLAYER = Text.ofMessageKey("dkbans.command.ip.info.player");
    public static MessageComponent<?> COMMAND_IP_INFO_NOT_BLOCKED = Text.ofMessageKey("dkbans.command.ip.info.notBlocked");

    public static MessageComponent<?> COMMAND_IP_BLOCK_HELP = Text.ofMessageKey("dkbans.command.ip.block.help");
    public static MessageComponent<?> COMMAND_IP_BlOCK_INVALID_TYPE = Text.ofMessageKey("dkbans.command.ip.block.invalid.type");
    public static MessageComponent<?> COMMAND_IP_BLOCK = Text.ofMessageKey("dkbans.command.ip.block");

    public static MessageComponent<?> COMMAND_IP_UNBLOCK_HELP = Text.ofMessageKey("dkbans.command.ip.unblock.help");
    public static MessageComponent<?> COMMAND_IP_UNBLOCK = Text.ofMessageKey("dkbans.command.ip.unblock");

    public static MessageComponent<?> COMMAND_CHAT_CLEAR_HELP = Text.ofMessageKey("dkbans.command.chatClear.help");
    public static MessageComponent<?> COMMAND_CHAT_CLEAR_MY = Text.ofMessageKey("dkbans.command.chatClear.my");
    public static MessageComponent<?> COMMAND_CHAT_CLEAR_ALL = Text.ofMessageKey("dkbans.command.chatClear.all");


    public static MessageComponent<?> COMMAND_BROADCAST_NOT_FOUND = Text.ofMessageKey("dkbans.command.broadcast.notFound");
    public static MessageComponent<?> COMMAND_BROADCAST_ALREADY_EXISTS = Text.ofMessageKey("dkbans.command.broadcast.alreadyExists");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_ALREADY_EXISTS = Text.ofMessageKey("dkbans.command.broadcast.group.alreadyExists");

    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_HELP = Text.ofMessageKey("dkbans.command.broadcast.group.help");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_INFO = Text.ofMessageKey("dkbans.command.broadcast.group.info");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_LIST = Text.ofMessageKey("dkbans.command.broadcast.group.list");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_CREATE_HELP = Text.ofMessageKey("dkbans.command.broadcast.group.create.help");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_CREATED = Text.ofMessageKey("dkbans.command.broadcast.group.created");

    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_ASSIGNMENT_HELP = Text.ofMessageKey("dkbans.command.broadcast.group.assignment.help");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_ASSIGNMENT_NOT_FOUND = Text.ofMessageKey("dkbans.command.broadcast.group.assignment.notFound");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_ASSIGNMENT_LIST = Text.ofMessageKey("dkbans.command.broadcast.group.assignment.list");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_ASSIGNMENT_INFO = Text.ofMessageKey("dkbans.command.broadcast.group.assignment.info");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_ASSIGNMENT_REMOVE = Text.ofMessageKey("dkbans.command.broadcast.group.assignment.remove");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_ASSIGNMENT_ADD = Text.ofMessageKey("dkbans.command.broadcast.group.assignment.add");

    public static MessageComponent<?> COMMAND_BROADCAST_VISIBILITY_NOT_EXISTS = Text.ofMessageKey("dkbans.command.broadcast.visibility.notExists");
    public static MessageComponent<?> COMMAND_BROADCAST_CREATE_HELP = Text.ofMessageKey("dkbans.command.broadcast.create.help");
    public static MessageComponent<?> COMMAND_BROADCAST_CREATED = Text.ofMessageKey("dkbans.command.broadcast.created");

    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_NOT_FOUND = Text.ofMessageKey("dkbans.command.broadcast.group.notFound");

    public static MessageComponent<?> COMMAND_BROADCAST_LIST = Text.ofMessageKey("dkbans.command.broadcast.list");

    public static MessageComponent<?> COMMAND_BROADCAST_DIRECT = Text.ofMessageKey("dkbans.command.broadcast.direct");

    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_EDIT_HELP = Text.ofMessageKey("dkbans.command.broadcast.group.edit.help");
    public static MessageComponent<?> COMMAND_BROADCAST_GROUP_EDIT_EDITED = Text.ofMessageKey("dkbans.command.broadcast.group.edit.edited");

    public static MessageComponent<?> COMMAND_BROADCAST_HELP = Text.ofMessageKey("dkbans.command.broadcast.help");

    public static MessageComponent<?> COMMAND_BROADCAST_DELETED = Text.ofMessageKey("dkbans.command.broadcast.deleted");

    public static MessageComponent<?> BROADCAST_CHAT = Text.ofMessageKey("dkbans.broadcast.chat");
    public static MessageComponent<?> BROADCAST_ACTIONBAR = Text.ofMessageKey("dkbans.broadcast.actionbar");
    public static MessageComponent<?> BROADCAST_BOSSBAR = Text.ofMessageKey("dkbans.broadcast.bossbar");
    public static MessageComponent<?> BROADCAST_TITLE = Text.ofMessageKey("dkbans.broadcast.title");
    public static MessageComponent<?> BROADCAST_TITLE_SUB = Text.ofMessageKey("dkbans.broadcast.title.sub");

    public static MessageComponent<?> COMMAND_BROADCAST_EDIT_HELP = Text.ofMessageKey("dkbans.command.broadcast.edit.help");
    public static MessageComponent<?> COMMAND_BROADCAST_EDIT_EDITED = Text.ofMessageKey("dkbans.command.broadcast.edit.edited");

    public static MessageComponent<?> COMMAND_BROADCAST_EDIT_PROPERTY_LIST = Text.ofMessageKey("dkbans.command.broadcast.edit.property.list");
    public static MessageComponent<?> COMMAND_BROADCAST_EDIT_PROPERTY_ADD = Text.ofMessageKey("dkbans.command.broadcast.edit.property.add");

    public static MessageComponent<?> COMMAND_BROADCAST_EDIT_PROPERTY_REMOVE = Text.ofMessageKey("dkbans.command.broadcast.edit.property.remove");
    public static MessageComponent<?> COMMAND_BROADCAST_EDIT_PROPERTY_NOT_EXIST = Text.ofMessageKey("dkbans.command.broadcast.edit.property.notExists");

    public static MessageComponent<?> COMMAND_BROADCAST_EDIT_PROPERTY_HELP = Text.ofMessageKey("dkbans.command.broadcast.edit.property.help");

    public static MessageComponent<?> COMMAND_BROADCAST_INFO = Text.ofMessageKey("dkbans.command.broadcast.info");
}
