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
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.annonations.Internal;
import net.pretronic.libraries.utility.map.Pair;

import java.util.UUID;

public class DefaultPlayerHistoryEntrySnapshot implements PlayerHistoryEntrySnapshot {

    private int id;
    private int entryId;
    private final PlayerHistoryType historyType;
    private final PunishmentType punishmentType;

    private final String reason;
    private final long timeout;

    private final int templateId;

    private final UUID staffId;
    private final DKBansScope scope;

    private final int points;
    private final boolean active;

    private final Document properties;
    private final String revokeMessage;
    private final int revokeTemplateId;

    private final boolean modifiedActive;
    private final long modifiedTime;
    private final UUID modifierId;

    private transient PlayerHistoryEntry cachedEntry;
    private transient DKBansExecutor cachedStaff;

    public DefaultPlayerHistoryEntrySnapshot(int id, int entryId, PlayerHistoryType historyType, PunishmentType punishmentType
            , String reason, long timeout, int templateId, UUID staffId, DKBansScope scope, int points, boolean active
            , Document properties, String revokeMessage, int revokeTemplateId, boolean modifiedActive, long modifiedTime
            , UUID modifierId, PlayerHistoryEntry cachedEntry, DKBansExecutor cachedStaff) {
        this.id = id;
        this.entryId = entryId;
        this.historyType = historyType;
        this.punishmentType = punishmentType;
        this.reason = reason;
        this.timeout = timeout;
        this.templateId = templateId;
        this.staffId = staffId;
        this.scope = scope;
        this.points = points;
        this.active = active;
        this.properties = properties;
        this.revokeMessage = revokeMessage;
        this.revokeTemplateId = revokeTemplateId;
        this.modifiedActive = modifiedActive;
        this.modifiedTime = modifiedTime;
        this.modifierId = modifierId;
        this.cachedEntry = cachedEntry;
        this.cachedStaff = cachedStaff;
    }

    @Override
    public int getEntryId() {
        return entryId;
    }

    @Override
    public PlayerHistoryEntry getEntry() {
        if(cachedEntry == null) cachedEntry = DKBans.getInstance().getHistoryManager().getHistoryEntry(entryId);
        return cachedEntry;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PlayerHistoryType getHistoryType() {
        return historyType;
    }

    @Override
    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public int getTemplateId() {
        return templateId;
    }

    @Override
    public Template getTemplate() {
        return DKBans.getInstance().getTemplateManager().getTemplate(templateId);
    }

    @Override
    public UUID getStaffId() {
        return staffId;
    }

    @Override
    public DKBansExecutor getStaff() {
        if(cachedStaff == null) cachedStaff = DKBans.getInstance().getPlayerManager().getExecutor(this.staffId);
        return cachedStaff;
    }

    @Override
    public DKBansScope getScope() {
        return scope;
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Document getProperties() {
        return properties;
    }

    @Override
    public String getRevokeReason() {
        return revokeMessage;
    }

    @Override
    public int getRevokeTemplateId() {
        return revokeTemplateId;
    }

    @Override
    public Template getRevokeTemplate() {
        return DKBans.getInstance().getTemplateManager().getTemplate(revokeTemplateId);
    }

    @Override
    public boolean isModifiedActive() {
        return this.modifiedActive;
    }

    @Override
    public long getModifiedTime() {
        return this.modifiedTime;
    }

    @Override
    public UUID getModifiedById() {
        return modifierId;
    }

    @Override
    public DKBansExecutor getModifiedBy() {
        return DKBans.getInstance().getPlayerManager().getExecutor(modifierId);
    }

    @Internal
    public void setInsertResult(Pair<PlayerHistoryEntry, Integer> result) {
        this.id = result.getValue();
        this.entryId = result.getKey().getId();
        this.cachedEntry = result.getKey();
    }

    @Internal
    public void setId(int id) {
        this.id = id;
    }

    @Internal
    public void setEntry(PlayerHistoryEntry entry) {
        this.entryId = entry.getId();
        this.cachedEntry = entry;
    }
}
