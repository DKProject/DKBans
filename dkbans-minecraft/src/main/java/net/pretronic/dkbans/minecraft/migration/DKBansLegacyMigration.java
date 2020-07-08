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
import ch.dkrieger.bansystem.lib.player.NetworkPlayer;
import ch.dkrieger.bansystem.lib.player.history.BanType;
import ch.dkrieger.bansystem.lib.player.history.entry.*;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.migration.Migration;
import net.pretronic.dkbans.api.migration.MigrationResult;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntry;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshotBuilder;
import net.pretronic.libraries.document.type.DocumentFileType;
import org.mcnative.common.McNative;
import org.mcnative.common.player.data.PlayerDataProvider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DKBansLegacyMigration extends Migration {

    public DKBansLegacyMigration() {
        super("DKBansLegacy");
    }

    @Override
    public MigrationResult migrate() {
        if(BanSystem.getInstance() == null) {
            new BanSystem();
        }

        PlayerDataProvider playerDataProvider = McNative.getInstance().getRegistry().getService(PlayerDataProvider.class);

        int mcNativeCount = 0;
        for (NetworkPlayer player : BanSystem.getInstance().getStorage().getPlayers()) {
            if(McNative.getInstance().getPlayerManager().getPlayer(player.getUUID()) == null
                    && McNative.getInstance().getPlayerManager().getPlayer(player.getName()) == null) {

                playerDataProvider.createPlayerData(player.getName(), player.getUUID(), -1, player.getFirstLogin()
                        , player.getLastLogin(), null);
                mcNativeCount++;

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
            }
        }
        return null;
    }

    @Override
    public boolean isAvailable() {
        return true;
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
}
