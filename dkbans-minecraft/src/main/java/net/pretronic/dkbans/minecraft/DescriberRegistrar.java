/*
 * (C) Copyright 2020 The DKPerms Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 05.05.20, 20:33
 * @website %web%
 *
 * %license%
 */

package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.common.broadcast.DefaultBroadcast;
import net.pretronic.dkbans.common.broadcast.DefaultBroadcastAssignment;
import net.pretronic.dkbans.common.broadcast.DefaultBroadcastGroup;
import net.pretronic.dkbans.common.filter.DefaultFilter;
import net.pretronic.dkbans.common.player.DefaultDKBansPlayer;
import net.pretronic.dkbans.common.player.chatlog.DefaultChatLogEntry;
import net.pretronic.dkbans.common.player.chatlog.DefaultPlayerChatLog;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistory;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntry;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryType;
import net.pretronic.dkbans.common.player.ipblacklist.DefaultIpAddressBlock;
import net.pretronic.dkbans.common.player.note.DefaultPlayerNote;
import net.pretronic.dkbans.common.player.report.DefaultPlayerReport;
import net.pretronic.dkbans.common.player.report.DefaultPlayerReportEntry;
import net.pretronic.dkbans.common.player.session.DefaultIpAddressInfo;
import net.pretronic.dkbans.common.player.session.DefaultPlayerSession;
import net.pretronic.dkbans.common.template.DefaultTemplate;
import net.pretronic.dkbans.common.template.DefaultTemplateCategory;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplate;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriber;
import net.pretronic.libraries.message.bml.variable.describer.VariableDescriberRegistry;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.map.Pair;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.MinecraftPlayer;
import org.mcnative.runtime.api.text.format.ColoredString;

import java.util.concurrent.TimeUnit;

public class DescriberRegistrar {

    public static void register(){
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistory.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryType.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerChatLog.class);
        VariableDescriberRegistry.registerDescriber(DefaultFilter.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerReport.class);
        VariableDescriberRegistry.registerDescriber(DefaultPlayerReportEntry.class);
        VariableDescriberRegistry.registerDescriber(DefaultTemplate.class);
        VariableDescriberRegistry.registerDescriber(DefaultTemplateCategory.class);
        VariableDescriberRegistry.registerDescriber(DefaultTemplateGroup.class);
        VariableDescriberRegistry.registerDescriber(DefaultPunishmentTemplate.class);
        VariableDescriber<?> punishmentDescriber = VariableDescriberRegistry.registerDescriber(PunishmentType.class);
        ColoredString.makeDescriberColored(punishmentDescriber);

        VariableDescriberRegistry.registerDescriber(DefaultIpAddressBlock.class);

        VariableDescriber<DKBansExecutor.SpecialExecutor> executorDescriber = VariableDescriberRegistry.registerDescriber(DKBansExecutor.SpecialExecutor.class);
        executorDescriber.registerFunction("displayName",  executor -> new ColoredString("&4"+executor.getName()));

        VariableDescriber<DefaultIpAddressInfo> addressDescriber = VariableDescriberRegistry.registerDescriber(DefaultIpAddressInfo.class);
        addressDescriber.registerFunction("blocked", DefaultIpAddressInfo::isBlocked);
        addressDescriber.registerFunction("address", entry -> entry.getAddress().getHostAddress());

        VariableDescriber<DefaultChatLogEntry> chatEntryDescriber = VariableDescriberRegistry.registerDescriber(DefaultChatLogEntry.class);
        chatEntryDescriber.registerFunction("timeFormatted", entry -> DKBansConfig.FORMAT_DATE.format(entry.getTime()));

        VariableDescriber<DefaultDKBansPlayer> playerDescriber = VariableDescriberRegistry.registerDescriber(DefaultDKBansPlayer.class);
        playerDescriber.setForwardFunction(player -> McNative.getInstance().getPlayerManager().getPlayer(player.getUniqueId()));
        playerDescriber.registerFunction("firstLoginFormatted", player -> {
            MinecraftPlayer mcPlayer = McNative.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            return DKBansConfig.FORMAT_DATE.format(mcPlayer.getFirstPlayed());
        });
        playerDescriber.registerFunction("lastLoginFormatted", player -> {
            MinecraftPlayer mcPlayer = McNative.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
            return DKBansConfig.FORMAT_DATE.format(mcPlayer.getLastPlayed());
        });

        VariableDescriber<DefaultPlayerHistoryEntry> entryDescriber =  VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryEntry.class);
        entryDescriber.setForwardFunction(DefaultPlayerHistoryEntry::getCurrent);
        entryDescriber.registerFunction("createdFormatted", snapshot -> DKBansConfig.FORMAT_DATE.format(snapshot.getCreated()));

        VariableDescriber<DefaultPlayerHistoryEntrySnapshot> snapshotDescriber = VariableDescriberRegistry.registerDescriber(DefaultPlayerHistoryEntrySnapshot.class);
        snapshotDescriber.registerFunction("revoked", snapshot -> !snapshot.isActive() || snapshot.getTimeout() <= System.currentTimeMillis());
        snapshotDescriber.registerFunction("active", DefaultPlayerHistoryEntrySnapshot::isActive);
        snapshotDescriber.registerFunction("revokedReason", snapshot -> snapshot.getRevokeReason() == null ? "" : snapshot.getRevokeReason());
        snapshotDescriber.registerFunction("modifiedTimeFormatted",snapshot -> DKBansConfig.FORMAT_DATE.format(snapshot.getModifiedTime()));
        snapshotDescriber.registerFunction("timeoutFormatted",snapshot -> DKBansConfig.FORMAT_DATE.format(snapshot.getTimeout()));
        snapshotDescriber.registerFunction("remainingFormatted", snapshot -> {
            long duration = snapshot.getTimeout()-System.currentTimeMillis();
            if(duration < 0 || !snapshot.getEntry().isActive()) return DKBansConfig.FORMAT_DATE_ENDLESSLY;
            else return DurationProcessor.getStandard().formatShort(TimeUnit.MILLISECONDS.toSeconds(duration));
        });
        snapshotDescriber.registerFunction("durationFormatted", snapshot -> {
            long duration = snapshot.getTimeout()-snapshot.getEntry().getCreated();
            if(duration < 0) return DKBansConfig.FORMAT_DATE_ENDLESSLY;
            else return DurationProcessor.getStandard().formatShort(TimeUnit.MILLISECONDS.toSeconds(duration));
        });

        VariableDescriber<DefaultPlayerNote> notesDescriber =  VariableDescriberRegistry.registerDescriber(DefaultPlayerNote.class);
        notesDescriber.registerFunction("timeFormatted", note -> DKBansConfig.FORMAT_DATE.format(note.getTime()));

        VariableDescriberRegistry.registerDescriber(DefaultBroadcast.class);
        VariableDescriberRegistry.registerDescriber(DefaultBroadcastAssignment.class);
        VariableDescriber<DefaultBroadcastGroup> groupDescriber = VariableDescriberRegistry.registerDescriber(DefaultBroadcastGroup.class);
        groupDescriber.registerFunction("enabled", DefaultBroadcastGroup::isEnabled);
        groupDescriber.registerFunction("scopeFormatted", group -> {
            if(group.getScope() != null) {
                StringBuilder builder = new StringBuilder().append("[")
                        .append(group.getScope().getType())
                        .append(":")
                        .append(group.getScope().getName());
                if(group.getScope().getId() != null) builder.append(":").append(group.getScope().getId());
                builder.append("]");
                return builder.toString();
            }
            return "none";
        });
        groupDescriber.registerFunction("intervalFormatted", group -> DurationProcessor.getStandard().format(group.getInterval(), true));

        VariableDescriber<DefaultPlayerSession> sessionDescriber = VariableDescriberRegistry.registerDescriber(DefaultPlayerSession.class);
        sessionDescriber.registerFunction("connectedFormatted", entry -> DKBansConfig.FORMAT_DATE.format(entry.getConnectTime()));
        sessionDescriber.registerFunction("disconnectedFormatted", entry -> DKBansConfig.FORMAT_DATE.format(entry.getDisconnectTime()));
        sessionDescriber.registerFunction("durationFormatted", entry -> DurationProcessor.getStandard().formatShort(entry.getDuration()));

        VariableDescriberRegistry.registerDescriber(Pair.class);
    }

}
