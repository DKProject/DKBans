/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.06.20, 17:19
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

    public static MessageComponent<?> ERROR_INTERNAL  = Text.ofMessageKey("dkbans.error.internal");
    public static MessageComponent<?> ERROR_INVALID_DURATION_FORMAT  = Text.ofMessageKey("dkbans.error.invalid.durationFormat");
    public static MessageComponent<?> ERROR_INVALID_NUMBER  = Text.ofMessageKey("dkbans.error.invalid.number");
    public static MessageComponent<?> ERROR_ONLY_PLAYER = Text.ofMessageKey("dkbans.error.onlyPlayer");

    public static MessageComponent<?> PLAYER_NOT_FOUND = Text.ofMessageKey("dkbans.player.notFound");
    public static MessageComponent<?> PLAYER_NOT_ONLINE = Text.ofMessageKey("dkbans.player.notOnline");
    public static MessageComponent<?> PLAYER_NOT_SELF = Text.ofMessageKey("dkbans.player.notSelf");
    public static MessageComponent<?> PLAYER_HAS_BYPASS = Text.ofMessageKey("dkbans.player.hasBypass");

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


    public static MessageComponent<?> PUNISH_OVERRIDE = Text.ofMessageKey("dkbans.punish.override");

    public static MessageComponent<?> PUNISH_BAN_MESSAGE_PERMANENTLY = Text.ofMessageKey("dkbans.punish.message.ban.permanently");
    public static MessageComponent<?> PUNISH_BAN_MESSAGE_TEMPORARY = Text.ofMessageKey("dkbans.punish.message.ban.temporary");

    public static MessageComponent<?> PUNISH_MUTE_MESSAGE_PERMANENTLY = Text.ofMessageKey("dkbans.punish.message.mute.permanently");
    public static MessageComponent<?> PUNISH_MUTE_MESSAGE_TEMPORARY = Text.ofMessageKey("dkbans.punish.message.mute.temporary");

    public static MessageComponent<?> COMMAND_PLAYER_INFO_OFFLINE = Text.ofMessageKey("dkbans.command.playerInfo.offline");
    public static MessageComponent<?> COMMAND_PLAYER_INFO_ONLINE = Text.ofMessageKey("dkbans.command.playerInfo.online");
    public static MessageComponent<?> COMMAND_PLAYER_INFO_SESSIONS = Text.ofMessageKey("dkbans.command.playerInfo.sessions");

    public static MessageComponent<?> COMMAND_HELP = Text.ofMessageKey("dkbans.command.help");

    public static MessageComponent<?> COMMAND_HELP_JUMPTO = Text.ofMessageKey("dkbans.command.help.jumpto");
    public static MessageComponent<?> COMMAND_HELP_KICK = Text.ofMessageKey("dkbans.command.help.kick");
    public static MessageComponent<?> COMMAND_HELP_PUNISH = Text.ofMessageKey("dkbans.command.help.punish");

    public static MessageComponent<?> COMMAND_FILTER_HELP = Text.ofMessageKey("dkbans.command.filter.help");
    public static MessageComponent<?> COMMAND_FILTER_AFFILIATION_AREA_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.affiliationArea.notFound");
    public static MessageComponent<?> COMMAND_FILTER_OPERATION_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.operation.notFound");
    public static MessageComponent<?> COMMAND_FILTER_NOT_FOUND = Text.ofMessageKey("dkbans.command.filter.notFound");
    public static MessageComponent<?> COMMAND_FILTER_LIST = Text.ofMessageKey("dkbans.command.filter.list");
    public static MessageComponent<?> COMMAND_FILTER_DELETED = Text.ofMessageKey("dkbans.command.filter.deleted");
    public static MessageComponent<?> COMMAND_FILTER_CREATED = Text.ofMessageKey("dkbans.command.filter.created");

    public static MessageComponent<?> FILTER_BLOCKED_NAME = Text.ofMessageKey("dkbans.filter.blocked.name");
    public static MessageComponent<?> FILTER_BLOCKED_COMMAND = Text.ofMessageKey("dkbans.filter.blocked.command");

    public static MessageComponent<?> COMMAND_TEMPLATE_IMPORT = Text.ofMessageKey("dkbans.command.template.import");


    public static MessageComponent<?> getPunishmentMessage(PlayerHistoryEntrySnapshot snapshot){
        if(snapshot.getPunishmentType() == PunishmentType.BAN){
            return snapshot.isPermanently() ? PUNISH_BAN_MESSAGE_PERMANENTLY : PUNISH_BAN_MESSAGE_TEMPORARY;
        }else if(snapshot.getPunishmentType() == PunishmentType.MUTE){
            return snapshot.isPermanently() ? PUNISH_MUTE_MESSAGE_PERMANENTLY : PUNISH_MUTE_MESSAGE_TEMPORARY;
        }
        throw new UnsupportedOperationException("Unknown punishment type");
    }
}
