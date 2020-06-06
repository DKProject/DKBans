package net.pretronic.dkbans.minecraft.config;

import net.pretronic.dkbans.api.template.Template;
import org.mcnative.common.text.Text;
import org.mcnative.common.text.components.MessageComponent;
import org.mcnative.common.text.format.TextColor;

public class Messages {

    public static MessageComponent<?> ERROR_INTERNAL  = Text.ofMessageKey("dkbans.error.internal");
    public static MessageComponent<?> ERROR_INVALID_DURATION_FORMAT  = Text.ofMessageKey("dkbans.error.invalidDurationFormat");
    public static MessageComponent<?> ERROR_ONLY_PLAYER = Text.ofMessageKey("dkbans.error.onlyPlayer");

    public static MessageComponent<?> PLAYER_NOT_FOUND = Text.ofMessageKey("dkbans.player.notFound");
    public static MessageComponent<?> PLAYER_NOT_ONLINE = Text.ofMessageKey("dkbans.player.notOnline");
    public static MessageComponent<?> PLAYER_NOT_SELF = Text.ofMessageKey("dkbans.player.notSelf");
    public static MessageComponent<?> PLAYER_HAS_BYPASS = Text.ofMessageKey("dkbans.player.hasBypass");

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

    public static MessageComponent<?> PUNISH_BAN_SCREEN_PERMANENTLY = Text.ofMessageKey("dkbans.banScreen.permanently");
    public static MessageComponent<?> PUNISH_BAN_SCREEN_TEMPORARY = Text.ofMessageKey("dkbans.banScreen.temporary");

    public static MessageComponent<?> COMMAND_PLAYER_INFO_OFFLINE = Text.ofMessageKey("dkbans.command.playerInfo.offline");
    public static MessageComponent<?> COMMAND_PLAYER_INFO_ONLINE = Text.ofMessageKey("dkbans.command.playerInfo.online");
    public static MessageComponent<?> COMMAND_PLAYER_INFO_SESSIONS = Text.ofMessageKey("dkbans.command.playerInfo.sessions");

    public static MessageComponent<?> COMMAND_HELP_JUMPTO = Text.ofMessageKey("dkbans.command.help.jumpto");
    public static MessageComponent<?> COMMAND_HELP_KICK = Text.ofMessageKey("dkbans.command.help.kick");
    public static MessageComponent<?> COMMAND_HELP_PUNISH = Text.ofMessageKey("dkbans.command.help.punish");

    public static MessageComponent<?> FILTER_BLOCKED_NAME = Text.ofMessageKey("dkbans.filter.blockedName");

    public static MessageComponent<?> COMMAND_TEMPLATE_IMPORT = Text.ofMessageKey("dkbans.command.template.import");
}
