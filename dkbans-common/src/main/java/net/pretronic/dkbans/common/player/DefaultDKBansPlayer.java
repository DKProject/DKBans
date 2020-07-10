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

package net.pretronic.dkbans.common.player;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.api.player.session.PlayerSessionList;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistory;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.common.player.note.DefaultPlayerNoteList;
import net.pretronic.dkbans.common.player.session.DefaultPlayerSession;
import net.pretronic.dkbans.common.player.session.DefaultPlayerSessionList;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.net.InetAddress;
import java.util.Collection;
import java.util.UUID;

public  class DefaultDKBansPlayer implements DKBansPlayer {

    private final UUID uniqueId;

    private final String name;

    private final PlayerHistory history;
    private final DefaultPlayerSessionList sessionList;

    private final DefaultPlayerNoteList noteList;

    public DefaultDKBansPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.history = new DefaultPlayerHistory(this);

        this.sessionList = new DefaultPlayerSessionList(this);
        this.noteList = new DefaultPlayerNoteList(this);
    }

    @Override
    public PlayerHistory getHistory() {
        return history;
    }

    @Override
    public PlayerSessionList getSessions() {
        return this.sessionList;
    }

    @Override
    public PlayerSession getActiveSession() {
         return getSessions().getActive();
    }

    @Override
    public PlayerSession getLastSession() {
         return getSessions().getLast();
    }

    @Override
    public long getOnlineTime() {
        return 0;//@Todo calculate
    }

    @Override
    public DKBansScope getCurrentScope() {
         throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasBypass() {//@Todo event
        return false;
    }

    @Override
    public PlayerChatLog getChatLog() {
         throw new UnsupportedOperationException();
    }

    @Override
    public PlayerNoteList getNotes() {
         return this.noteList;
    }

    @Override
    public PlayerNote createNote(DKBansExecutor creator, String message, PlayerNoteType type) {
        return this.noteList.createNote(creator, message, type);
    }

    @Override
    public boolean hasActivePunish(PunishmentType type) {
        Validate.notNull(type);
        return history.hasActivePunish(type);
    }

    @Override
    public boolean hasActivePunish(PunishmentType type, DKBansScope scope) {
        Validate.notNull(type,scope);
        return history.hasActivePunish(type,scope);
    }

    @Override
    public boolean hasActivePunish(PunishmentType type, Collection<DKBansScope> scopes) {
        Validate.notNull(type,scopes);
        return history.hasActivePunish(type,scopes);
    }

    @Override
    public PlayerHistoryEntrySnapshot punish(DKBansExecutor executor, PunishmentTemplate template) {
        Validate.notNull(executor,template);
        PlayerHistoryEntrySnapshotBuilder builder = punish();
        template.build(this,executor,builder);
        return builder.execute();
    }

    @Override
    public PlayerHistoryEntrySnapshotBuilder punish() {
         return new DefaultPlayerHistoryEntrySnapshotBuilder(this,null);
    }

    @Override
    public boolean hasReport() {
        return getReport() != null;
    }

    @Override
    public PlayerReport getReport() {
        return Iterators.findOne(DKBans.getInstance().getReportManager().getOpenReports(), report -> report.getPlayer().getUniqueId().equals(getUniqueId()));
    }

    @Override
    public PlayerReportEntry report(DKBansPlayer executor, ReportTemplate template) {
        return DKBans.getInstance().getReportManager().report(executor, this, template, "none", UUID.randomUUID());//@Todo set current server/id
    }

    @Override
    public PlayerReportEntry report(DKBansPlayer executor, String reason) {
        return DKBans.getInstance().getReportManager().report(executor, this, reason, "none", UUID.randomUUID());//@Todo set current server/id
    }

    @Override
    public void startSession(String currentName, InetAddress address, String country, String region, String proxyName, UUID proxyId, String clientEdition, int clientProtocolVersion) {
        DefaultPlayerSession session = new DefaultPlayerSession(-1,
                this, currentName, address, country,
                region, proxyName, UUID.randomUUID(),
                null, null,
                clientEdition, clientProtocolVersion,
                System.currentTimeMillis(), -1);
        int sessionId = DKBans.getInstance().getStorage().startPlayerSession(session);
        session.setId(sessionId);
        this.sessionList.setActive(session);
    }

    @Override
    public void finishSession(String lastServerName, UUID lastServerId) {
        DefaultPlayerSession session = (DefaultPlayerSession) getActiveSession();
        if(session == null) throw new IllegalArgumentException("No active session found");

        session.setDisconnectTime();
        session.setLastServerName(lastServerName);
        session.setLastServerId(lastServerId);

        DKBans.getInstance().getStorage().completePlayerSession(session);

        this.sessionList.setActive(null);
    }

    @Override
    public String getName() {
         return this.name;
    }

    @Override
    public UUID getUniqueId() {
         return this.uniqueId;
    }

    @Override
    public DKBansPlayer getPlayer() {
         return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DKBansPlayer && ((DKBansPlayer)o).getUniqueId().equals(getUniqueId());
    }
}
