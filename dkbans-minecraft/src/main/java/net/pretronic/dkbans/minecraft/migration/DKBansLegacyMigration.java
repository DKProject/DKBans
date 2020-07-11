/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 08.07.20, 17:45
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

package net.pretronic.dkbans.minecraft.migration;

import ch.dkrieger.bansystem.lib.BanSystem;
import ch.dkrieger.bansystem.lib.config.mode.BanMode;
import ch.dkrieger.bansystem.lib.config.mode.ReasonMode;
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import ch.dkrieger.bansystem.lib.player.history.BanType;
import ch.dkrieger.bansystem.lib.player.history.entry.*;
import ch.dkrieger.bansystem.lib.reason.*;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.migration.Migration;
import net.pretronic.dkbans.api.migration.MigrationResult;
import net.pretronic.dkbans.api.migration.MigrationResultBuilder;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.dkbans.common.template.punishment.DefaultPunishmentTemplate;
import net.pretronic.dkbans.common.template.punishment.types.DefaultBanPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.types.DefaultMutePunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.punishment.types.DefaultWarnPunishmentTemplateEntry;
import net.pretronic.dkbans.common.template.report.DefaultReportTemplate;
import net.pretronic.dkbans.common.template.unpunishment.DefaultUnPunishmentTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import org.mcnative.common.McNative;
import org.mcnative.common.player.data.PlayerDataProvider;

import java.io.File;
import java.time.Duration;
import java.util.*;

public class DKBansLegacyMigration extends Migration {

    public DKBansLegacyMigration() {
        super("DKBansLegacy");
    }

    @Override
    public MigrationResult migrate() {
        long start = System.currentTimeMillis();
        if(BanSystem.getInstance() == null) {
            new BanSystem();
        }
        MigrationResultBuilder resultBuilder = new MigrationResultBuilder();
        //migrateReasons(resultBuilder);
        migratePlayers(resultBuilder);

        long end = System.currentTimeMillis();
        return resultBuilder.time(end-start).success(true).create();
    }

    @Override
    public boolean isAvailable() {
        return new File("plugins/DKBans/legacy-config.yml").exists();
    }

    private void migrateReasons(MigrationResultBuilder resultBuilder) {
        migrateBanReasons(resultBuilder);
        migrateKickReasons(resultBuilder);
        migrateUnbanReasons(resultBuilder);
        migrateReportReasons(resultBuilder);
        migrateWarnReasons(resultBuilder);
    }

    private void migrateBanReasons(MigrationResultBuilder resultBuilder) {
        int amount = 0;
        BanMode banMode = BanSystem.getInstance().getConfig().banMode;
        if(banMode !=  BanMode.SELF) {
            DefaultTemplateGroup templateGroup = (DefaultTemplateGroup) getOrCreateGroup("ban", convertCalculationType(banMode));
            for (BanReason banReason : BanSystem.getInstance().getReasonProvider().getBanReasons()) {
                DefaultPunishmentTemplate template = (DefaultPunishmentTemplate) TemplateFactory.create(TemplateType.PUNISHMENT,
                        banReason.getID(),
                        banReason.getID(),
                        banReason.getName(),
                        templateGroup,
                        banReason.getDisplay(),
                        banReason.getPermission(),
                        banReason.getAliases(),
                        migrateHistoryType(banReason.getHistoryType()),
                        true,
                        banReason.isHidden(),
                        getCategory(),
                        Document.newDocument());
                template.setPointsDivider(banReason.getDivider());
                for (Map.Entry<Integer, BanReasonEntry> entry : banReason.getTemplateDurations().entrySet()) {
                    template.getDurations().put(entry.getKey(), migrateBanReasonEntry(entry.getValue()));
                }
                for (Map.Entry<Integer, BanReasonEntry> entry : banReason.getPointsDurations().entrySet()) {
                    template.getDurations().put(entry.getKey(), migrateBanReasonEntry(entry.getValue()));
                }
                templateGroup.addTemplateInternal(template);

                amount++;
            }
            DKBans.getInstance().getStorage().importTemplateGroup(templateGroup);
        }
        resultBuilder.addMigrated("BanReasons", amount);
    }

    private void migrateKickReasons(MigrationResultBuilder resultBuilder) {
        int amount = 0;

        ReasonMode mode = BanSystem.getInstance().getConfig().kickMode;
        if(mode == ReasonMode.TEMPLATE) {
            DefaultTemplateGroup templateGroup = (DefaultTemplateGroup) getOrCreateGroup("kick", convertCalculationType(mode));
            for (KickReason reason : BanSystem.getInstance().getReasonProvider().getKickReasons()) {
                DefaultPunishmentTemplate template = (DefaultPunishmentTemplate) TemplateFactory.create(TemplateType.PUNISHMENT,
                        reason.getID(),
                        reason.getID(),
                        reason.getName(),
                        templateGroup,
                        reason.getDisplay(),
                        reason.getPermission(),
                        reason.getAliases(),
                        getDefaultHistoryType(),
                        true,
                        reason.isHidden(),
                        getCategory(),
                        Document.newDocument());
                amount++;
                templateGroup.addTemplateInternal(template);
            }
            DKBans.getInstance().getStorage().importTemplateGroup(templateGroup);
        }
        resultBuilder.addMigrated("KickReasons", amount);
    }

    private void migrateUnbanReasons(MigrationResultBuilder resultBuilder) {
        int amount = 0;

        ReasonMode mode = BanSystem.getInstance().getConfig().unbanMode;
        if(mode == ReasonMode.TEMPLATE) {
            DefaultTemplateGroup templateGroup = (DefaultTemplateGroup) getOrCreateGroup("unban", convertCalculationType(mode));
            for (UnbanReason reason : BanSystem.getInstance().getReasonProvider().getUnbanReasons()) {
                DefaultUnPunishmentTemplate template = (DefaultUnPunishmentTemplate) TemplateFactory.create(TemplateType.UNPUNISHMENT,
                        reason.getID(),
                        reason.getID(),
                        reason.getName(),
                        templateGroup,
                        reason.getDisplay(),
                        reason.getPermission(),
                        reason.getAliases(),
                        getDefaultHistoryType(),
                        true,
                        reason.isHidden(),
                        getCategory(),
                        Document.newDocument());

                for (Integer id : reason.getNotForBanID()) {
                    template.addBlacklistedTemplate(new DefaultUnPunishmentTemplate.BlacklistedTemplate("ban", null, id));
                }
                amount++;
                templateGroup.addTemplateInternal(template);
            }
            DKBans.getInstance().getStorage().importTemplateGroup(templateGroup);
        }
        resultBuilder.addMigrated("UnbanReasons", amount);
    }

    private void migrateReportReasons(MigrationResultBuilder resultBuilder) {
        int amount = 0;

        ReasonMode mode = BanSystem.getInstance().getConfig().reportMode;
        if(mode == ReasonMode.TEMPLATE) {
            DefaultTemplateGroup templateGroup = (DefaultTemplateGroup) getOrCreateGroup("report", convertCalculationType(mode));
            for (ReportReason reason : BanSystem.getInstance().getReasonProvider().getReportReasons()) {
                DefaultReportTemplate template = (DefaultReportTemplate) TemplateFactory.create(TemplateType.PUNISHMENT,
                        reason.getID(),
                        reason.getID(),
                        reason.getName(),
                        templateGroup,
                        reason.getDisplay(),
                        reason.getPermission(),
                        reason.getAliases(),
                        getDefaultHistoryType(),
                        true,
                        reason.isHidden(),
                        getCategory(),
                        Document.newDocument());
                template.setTargetTemplateGroupName("ban");
                template.setTargetTemplateId(reason.getForBan());
                amount++;
                templateGroup.addTemplateInternal(template);
            }
            DKBans.getInstance().getStorage().importTemplateGroup(templateGroup);
        }
        resultBuilder.addMigrated("ReportReasons", amount);
    }

    private void migrateWarnReasons(MigrationResultBuilder resultBuilder) {
        int amount = 0;

        ReasonMode mode = BanSystem.getInstance().getConfig().warnMode;
        if(mode == ReasonMode.TEMPLATE) {
            DefaultTemplateGroup templateGroup = (DefaultTemplateGroup) getOrCreateGroup("report", convertCalculationType(mode));
            for (WarnReason reason : BanSystem.getInstance().getReasonProvider().getWarnReasons()) {
                DefaultPunishmentTemplate template = (DefaultPunishmentTemplate) TemplateFactory.create(TemplateType.PUNISHMENT,
                        reason.getID(),
                        reason.getID(),
                        reason.getName(),
                        templateGroup,
                        reason.getDisplay(),
                        reason.getPermission(),
                        reason.getAliases(),
                        getDefaultHistoryType(),
                        true,
                        reason.isHidden(),
                        getCategory(),
                        Document.newDocument());
                DefaultWarnPunishmentTemplateEntry entry = new DefaultWarnPunishmentTemplateEntry(DKBansScope.GLOBAL, true,
                        "ban", null, reason.getForBan());
                templateGroup.addTemplateInternal(template);
                amount++;
            }
            DKBans.getInstance().getStorage().importTemplateGroup(templateGroup);
        }
        resultBuilder.addMigrated("WarnReasons", amount);
    }

    private CalculationType convertCalculationType(BanMode banMode) {
        switch (banMode) {
            case POINT: return CalculationType.POINTS;
            case TEMPLATE: return CalculationType.AMOUNT;
        }
        throw new IllegalArgumentException("Migration failed on ban mode " + banMode.name());
    }

    private CalculationType convertCalculationType(ReasonMode mode) {
        if (mode == ReasonMode.TEMPLATE) {
            return CalculationType.AMOUNT;
        }
        throw new IllegalArgumentException("Migration failed on reason mode " + mode.name());
    }

    private void migratePlayers(MigrationResultBuilder resultBuilder) {
        PlayerDataProvider playerDataProvider = McNative.getInstance().getRegistry().getService(PlayerDataProvider.class);

        int mcNativeCount = 0;
        int players = 0;
        for (NetworkPlayer player : BanSystem.getInstance().getStorage().getPlayers()) {
            if(McNative.getInstance().getPlayerManager().getPlayer(player.getUUID()) == null
                    && McNative.getInstance().getPlayerManager().getPlayer(player.getName()) == null) {

                playerDataProvider.createPlayerData(player.getName(), player.getUUID(), -1, player.getFirstLogin()
                        , player.getLastLogin(), null);
                mcNativeCount++;
            }
            Map<BanType, PlayerHistoryEntry> tempHistory = new HashMap<>();

            DKBansPlayer dkBansPlayer = DKBans.getInstance().getPlayerManager().getPlayer(player.getUUID());

            for (HistoryEntry entry : player.getHistory().getEntries()) {
                PlayerHistoryEntrySnapshotBuilder builder = new DefaultPlayerHistoryEntrySnapshotBuilder(dkBansPlayer, null);
                builder.stuff(getStuff(entry));
                builder.reason(entry.getReason());
                builder.points(entry.getPoints().getPoints());
                PlayerHistoryType historyType = DKBans.getInstance().getHistoryManager().getHistoryType(entry.getPoints().getHistoryType().name());
                if(historyType == null) historyType = DKBans.getInstance().getHistoryManager().createHistoryType(entry.getPoints().getHistoryType().name());
                builder.historyType(historyType);
                builder.properties(DocumentFileType.JSON.getReader().read(entry.getProperties().toJson()));
                builder.modifiedTime(entry.getTimeStamp());

                if(entry instanceof Ban) {
                    PlayerHistoryEntry historyEntry = migrateBanEntry(builder, ((Ban) entry));
                    tempHistory.put(((Ban) entry).getBanType(), historyEntry);
                } else if(entry instanceof Kick) {
                    migrateKickEntry(builder, ((Kick) entry));
                } else if(entry instanceof Unban) {
                    Unban unban = ((Unban) entry);
                    migrateUnbanEntry(builder, tempHistory, unban);
                } else if(entry instanceof Warn) {
                    migrateWarnEntry(builder, ((Warn) entry));
                } else if(entry instanceof Unwarn) {
                    //Not included in new dkbans
                }
            }
            players++;
        }
        resultBuilder.addMigrated("McNativePlayers", mcNativeCount);
        resultBuilder.addMigrated("Players", players);
    }

    private PlayerHistoryEntry migrateBanEntry(PlayerHistoryEntrySnapshotBuilder builder, Ban ban) {
        builder.timeout(ban.getTimeOut());
        builder.punishmentType(convertBanType(ban.getBanType()));
        PlayerHistoryEntry historyEntry = builder.execute().getEntry();

        ban.getVersions().sort(Comparator.comparingLong(Ban.BanEditVersion::getTime));
        for (Ban.BanEditVersion version : ban.getVersions()) {
            builder = historyEntry.newSnapshot(getStuff(version.getStaff()));
            if(version instanceof Ban.ReasonBanEdit) {
                Ban.ReasonBanEdit edit = ((Ban.ReasonBanEdit) version);
                builder.reason(edit.getReason());
            } else if(version instanceof Ban.MessageBanEdit) {
                Ban.MessageBanEdit edit = ((Ban.MessageBanEdit) version);
                //@Todo add note
            } else if(version instanceof Ban.TimeOutBanEdit) {
                Ban.TimeOutBanEdit edit = ((Ban.TimeOutBanEdit) version);
                builder.timeout(edit.getTimeOut());
            } else if(version instanceof Ban.PointsBanEdit) {
                Ban.PointsBanEdit edit = ((Ban.PointsBanEdit) version);
                builder.points(edit.getPoints().getPoints());
            }
            builder.execute();
        }
        return historyEntry;
    }

    private void migrateKickEntry(PlayerHistoryEntrySnapshotBuilder builder, Kick kick) {
        builder.scope(new DKBansScope(DKBansScope.DEFAULT_SERVER, kick.getServer(), null));
        builder.execute();
    }

    private void migrateUnbanEntry(PlayerHistoryEntrySnapshotBuilder builder, Map<BanType, PlayerHistoryEntry> tempHistory, Unban unban) {
        PlayerHistoryEntry historyEntry = tempHistory.get(unban.getBanType());
        historyEntry.newSnapshot(getStuff(unban))
                .active(false)
                .execute();
    }

    private void migrateWarnEntry(PlayerHistoryEntrySnapshotBuilder builder, Warn warn) {
        builder.punishmentType(PunishmentType.WARN).execute(); //@Todo map warn kick
    }

    private DKBansExecutor getStuff(HistoryEntry entry) {
        if(entry.getStaffAsPlayer() != null) {
            return McNative.getInstance().getPlayerManager().getPlayer(entry.getStaffAsPlayer().getUUID()).getAs(DKBansPlayer.class);
        }
        return DKBansExecutor.CONSOLE;
    }

    private DKBansExecutor getStuff(String stuff) {
        try{
            return McNative.getInstance().getPlayerManager().getPlayer(UUID.fromString(stuff)).getAs(DKBansPlayer.class);
        }catch (Exception exception){
            return DKBansExecutor.CONSOLE;
        }
    }

    private PunishmentType convertBanType(BanType banType) {
        switch (banType) {
            case NETWORK: return PunishmentType.BAN;
            case CHAT: return PunishmentType.MUTE;
        }
        throw new UnsupportedOperationException("Migration failed on ban type " + banType);
    }

    private PlayerHistoryType migrateHistoryType(BanType type) {
        PlayerHistoryType historyType = DKBans.getInstance().getHistoryManager().getHistoryType(type.name());
        if(historyType == null) historyType = DKBans.getInstance().getHistoryManager().createHistoryType(type.name());
        return historyType;
    }

    private Duration convertDuration(ch.dkrieger.bansystem.lib.utils.Duration duration) {
        return Duration.ofMillis(duration.getMillisTime());
    }

    private PunishmentTemplateEntry migrateBanReasonEntry(BanReasonEntry entry) {
        if(entry.getType() == BanType.NETWORK) {
            return new DefaultBanPunishmentTemplateEntry(DKBansScope.GLOBAL, convertDuration(entry.getDuration()));
        } else {
            return new DefaultMutePunishmentTemplateEntry(DKBansScope.GLOBAL, convertDuration(entry.getDuration()));
        }
    }

    private TemplateGroup getOrCreateGroup(String name, CalculationType type) {
        TemplateGroup templateGroup = DKBans.getInstance().getTemplateManager().getTemplateGroup(name);
        if(templateGroup == null) {
            templateGroup = DKBans.getInstance().getTemplateManager().createTemplateGroup(name, TemplateType.PUNISHMENT, type);
        }
        return templateGroup;
    }

    private TemplateCategory getCategory() {
        TemplateCategory category = DKBans.getInstance().getTemplateManager().getTemplateCategory("General");
        if(category == null) category = DKBans.getInstance().getTemplateManager().createTemplateCategory("General", "General");
        return category;
    }

    private PlayerHistoryType getDefaultHistoryType() {
        PlayerHistoryType historyType = DKBans.getInstance().getHistoryManager().getHistoryType("Network");
        if(historyType == null) {
            historyType = DKBans.getInstance().getHistoryManager().createHistoryType("Network");
        }
        return historyType;
    }
}
