/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.05.20, 20:56
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkbans.minecraft.integration;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.minecraft.PlayerSettingsKey;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.protocol.MinecraftEdition;
import org.mcnative.common.protocol.MinecraftProtocolVersion;
import org.mcnative.common.serviceprovider.placeholder.PlaceholderHook;

import java.util.concurrent.TimeUnit;

public class DKBansPlaceholders implements PlaceholderHook {

    @Override
    public Object onRequest(MinecraftPlayer player, String parameter) {
        DKBansPlayer dkbansPlayer = player.getAs(DKBansPlayer.class);
        if(parameter.equalsIgnoreCase("player_name")){
            return player.getName();
        }else if(parameter.equalsIgnoreCase("player_uniqueId")){
            return player.getUniqueId();
        }else if(parameter.equalsIgnoreCase("player_online-time-long")){
            return DurationProcessor.getStandard().format(TimeUnit.MILLISECONDS.toSeconds(dkbansPlayer.getOnlineTime()));
        }else if(parameter.equalsIgnoreCase("player_online-time-short")){
            return DurationProcessor.getStandard().formatShort(TimeUnit.MILLISECONDS.toSeconds(dkbansPlayer.getOnlineTime()));
        }else if(parameter.equalsIgnoreCase("player_online-time-h")){
            return TimeUnit.MICROSECONDS.toHours(dkbansPlayer.getOnlineTime());
        }else if(parameter.equalsIgnoreCase("player_online-time-d")){
            return TimeUnit.MICROSECONDS.toDays(dkbansPlayer.getOnlineTime());
        }else if(parameter.equalsIgnoreCase("player_country")){
            PlayerSession session = dkbansPlayer.getActiveSession();
            return session != null ? session.getCountry() : "Unknown";
        }else if(parameter.equalsIgnoreCase("player_ip")){
            PlayerSession session = dkbansPlayer.getActiveSession();
            return session != null ? session.getIpAddress().getHostAddress() : "Unknown";
        }else if(parameter.equalsIgnoreCase("player_region")){
            PlayerSession session = dkbansPlayer.getActiveSession();
            return session != null ? session.getRegion() : "Unknown";
        }else if(parameter.equalsIgnoreCase("player_version")){
            PlayerSession session = dkbansPlayer.getActiveSession();
            if(session == null) return "Unknown";
            MinecraftProtocolVersion version = MinecraftProtocolVersion.of(MinecraftEdition.JAVA,session.getClientProtocolVersion());
            return version.getName();
        }else if(parameter.equalsIgnoreCase("player_teamchatLogin")){
            return player.hasSetting("DKBans", PlayerSettingsKey.TEAM_CHAT_LOGIN,true);
        }else if(parameter.equalsIgnoreCase("player_notifyLogin")){
            return player.hasSetting("DKBans", PlayerSettingsKey.PUNISH_NOTIFY_LOGIN,true);
        }else if(parameter.equalsIgnoreCase("player_reportLogin")){
            return player.hasSetting("DKBans", PlayerSettingsKey.REPORT_CHAT_LOGIN,true);
        }else if(parameter.equalsIgnoreCase("openReports")){
            return DKBans.getInstance().getReportManager().getReportCount();
        }
        return null;
    }
}
