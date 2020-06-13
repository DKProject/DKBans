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

package net.pretronic.dkbans.common.player.history;

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

public class DefaultPlayerHistoryEntrySnapshot implements PlayerHistoryEntrySnapshot {

    private PlayerHistoryEntry entry;
    private int id;
    private final PlayerHistoryType historyType;
    private final PunishmentType punishmentType;

    private final String reason;
    private final long timeout;

    private final Template template;

    private final DKBansExecutor stuff;
    private final DKBansScope scope;

    private final int points;
    private final boolean active;

    private final Document properties;
    private final String revokeMessage;
    private final Template revokeTemplate;

    private final boolean modifiedActive;
    private final long modifiedTime;
    private final DKBansExecutor modifier;

    public DefaultPlayerHistoryEntrySnapshot(PlayerHistoryEntry entry, int id, PlayerHistoryType historyType, PunishmentType punishmentType
            , String reason, long timeout, Template template, DKBansExecutor stuff, DKBansScope scope, int points
            , boolean active, Document properties, String revokeMessage, Template revokeTemplate, boolean modifiedActive, long modifiedTime, DKBansExecutor modifier) {
        this.entry = entry;
        this.id = id;
        this.historyType = historyType;
        this.punishmentType = punishmentType;
        this.reason = reason;
        this.timeout = timeout;
        this.template = template;
        this.stuff = stuff;
        this.scope = scope;
        this.points = points;
        this.active = active;
        this.properties = properties;
        this.revokeMessage = revokeMessage;
        this.revokeTemplate = revokeTemplate;
        this.modifiedActive = modifiedActive;
        this.modifiedTime = modifiedTime;
        this.modifier = modifier;
    }

    @Override
    public PlayerHistoryEntry getEntry() {
        return entry;
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
    public Template getTemplate() {
        return template;
    }

    @Override
    public DKBansExecutor getStuff() {
        return stuff;
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
    public Template getRevokeTemplate() {
        return revokeTemplate;
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
    public DKBansExecutor getModifiedBy() {
        return modifier;
    }

    @Internal
    public void setInsertResult(Pair<PlayerHistoryEntry, Integer> result) {
        this.id = result.getValue();
        this.entry = result.getKey();
    }

    @Internal
    public void setId(int id) {
        this.id = id;
    }

    @Internal
    public void setEntry(PlayerHistoryEntry entry) {
        this.entry = entry;
    }
}
