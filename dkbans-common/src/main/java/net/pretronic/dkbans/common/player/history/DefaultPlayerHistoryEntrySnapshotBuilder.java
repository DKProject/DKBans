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

package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishEvent;
import net.pretronic.dkbans.api.event.punish.DKBansPlayerPunishUpdateEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.common.event.DefaultDKBansPlayerPunishEvent;
import net.pretronic.dkbans.common.event.DefaultDKBansPlayerPunishUpdateEvent;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.map.Pair;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class DefaultPlayerHistoryEntrySnapshotBuilder implements PlayerHistoryEntrySnapshotBuilder {

    private final DKBansPlayer player;
    private final PlayerHistory history;
    private final PlayerHistoryEntry entry;

    private PlayerHistoryType historyType;
    private PunishmentType punishmentType;
    private String reason = "None";
    private long timeout = -1;
    private Template template = null;
    private DKBansExecutor staff = null;
    private DKBansScope scope = null;
    private int points = 0;
    private boolean active = true;
    private Document properties;
    private String revokeReason = null;
    private Template revokeTemplate = null;
    private DKBansExecutor modifier;
    private long modifiedTime;

    public DefaultPlayerHistoryEntrySnapshotBuilder(DKBansPlayer player, PlayerHistoryEntry entry) {
        this.player = player;
        this.history = player.getHistory();
        this.entry = entry;

        modifiedTime = -1;

        if(entry != null){
            PlayerHistoryEntrySnapshot snapshot = entry.getCurrent();
            historyType = snapshot.getHistoryType();
            punishmentType = snapshot.getPunishmentType();
            reason = snapshot.getReason();
            timeout = snapshot.getTimeout();
            template = snapshot.getTemplate();
            staff = snapshot.getStaff();
            scope = snapshot.getScope();
            points = snapshot.getPoints();
            active = snapshot.isActive();
            properties = snapshot.getProperties();
            revokeReason = snapshot.getRevokeReason();
            revokeTemplate = snapshot.getRevokeTemplate();
        }
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder historyType(PlayerHistoryType historyType) {
        this.historyType = historyType;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder punishmentType(PunishmentType punishmentType) {
        this.punishmentType = punishmentType;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder reason(String reason) {
        this.reason = reason;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder timeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder duration(long time, TimeUnit unit) {
        if(time <= 0) return timeout(-1);
        return timeout(System.currentTimeMillis()+unit.toMillis(time));
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder duration(Duration duration) {
        return duration(duration.getSeconds(),TimeUnit.SECONDS);
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder template(Template template) {
        this.template = template;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder staff(DKBansExecutor staff) {
        this.staff = staff;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder scope(DKBansScope scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder points(int points) {
        this.points = points;
        return this;
    }

    @Override
    public DefaultPlayerHistoryEntrySnapshotBuilder active(boolean active) {
        this.active = active;
        return this;
    }

    public DefaultPlayerHistoryEntrySnapshotBuilder properties(Document properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder revokeReason(String reason) {
        this.revokeReason = reason;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder revokeTemplate(Template template) {
        this.revokeTemplate = template;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder modifiedBy(DKBansExecutor executor) {
        modifier = executor;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder modifiedTime(long timestamp) {
        this.modifiedTime = timestamp;
        return this;
    }

    @Override
    public PlayerHistoryEntrySnapshot execute() {
        Validate.notNull(staff,player,historyType,reason);

        if(punishmentType.isOneTime()) this.active = false;

        if(modifier == null) modifier = staff;
        if(modifiedTime <= 0) modifiedTime = System.currentTimeMillis();
        if(scope == null) scope = DKBansScope.GLOBAL;

        DefaultPlayerHistoryEntrySnapshot snapshot;
        if(entry == null){
            snapshot = new DefaultPlayerHistoryEntrySnapshot(-1, -1, historyType, punishmentType,
                    reason, timeout, template.getId(), staff.getUniqueId(), scope, points, active, properties
                    , revokeReason, revokeTemplate.getId(), true, modifiedTime, modifier.getUniqueId()
                    ,null,staff);

            Pair<PlayerHistoryEntry, Integer> result = DKBans.getInstance().getStorage().createHistoryEntry(player, snapshot);
            snapshot.setInsertResult(result);
            DKBans.getInstance().getEventBus().callEvent(DKBansPlayerPunishEvent.class,new DefaultDKBansPlayerPunishEvent(snapshot,player.getUniqueId(),player));
            ((DefaultPlayerHistory)history).push(result.getKey());
            if(player.getReport() != null){
                player.getReport().accept(this.staff);
            }
        }else{
            PlayerHistoryEntrySnapshot old = entry.getCurrent();
            snapshot = new DefaultPlayerHistoryEntrySnapshot(entry.getId(), -1, historyType
                    ,punishmentType, reason, timeout, template.getId(), staff.getUniqueId(), scope, points, active
                    ,properties, revokeReason, revokeTemplate.getId(), true, modifiedTime
                    ,modifier.getUniqueId(),entry,staff);
            int id = DKBans.getInstance().getStorage().insertHistoryEntrySnapshot(snapshot);
            snapshot.setId(id);
            ((DefaultPlayerHistoryEntry)entry).setCurrent(snapshot);
            DKBans.getInstance().getEventBus().callEvent(DKBansPlayerPunishUpdateEvent.class,new DefaultDKBansPlayerPunishUpdateEvent(old,snapshot,player.getUniqueId(),player));
        }

        return snapshot;
    }

}
