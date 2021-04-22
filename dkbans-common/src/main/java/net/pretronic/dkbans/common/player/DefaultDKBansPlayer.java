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

import net.pretronic.databasequery.api.query.Aggregation;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.event.DKBansBypassCheckEvent;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressInfo;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.session.PlayerSession;
import net.pretronic.dkbans.api.player.session.PlayerSessionList;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.event.DefaultDKBansBypassCheckEvent;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistory;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.common.player.note.DefaultPlayerNoteList;
import net.pretronic.dkbans.common.player.session.DefaultIpAddressInfo;
import net.pretronic.dkbans.common.player.session.DefaultPlayerSession;
import net.pretronic.dkbans.common.player.session.DefaultPlayerSessionList;
import net.pretronic.libraries.utility.Validate;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public  class DefaultDKBansPlayer implements DKBansPlayer {

    private final UUID uniqueId;

    private final String name;

    private final PlayerHistory history;
    private final DefaultPlayerSessionList sessionList;

    private final DefaultPlayerNoteList noteList;

    private long onlineTime;

    public DefaultDKBansPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.history = new DefaultPlayerHistory(this);

        this.sessionList = new DefaultPlayerSessionList(this);
        this.noteList = new DefaultPlayerNoteList(this);

        this.onlineTime = -1;
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
    public Collection<IpAddressInfo> getIpAddresses() {
        QueryResult result = DefaultDKBans.getInstance().getStorage().getPlayerSessions().find()
                .get("IpAddress")
                .getAs(Aggregation.MIN,"Country","Country")
                .getAs(Aggregation.MIN,"Region","Region")
                .where("PlayerId",this.uniqueId)
                .groupBy("IpAddress").execute();

        Collection<IpAddressInfo> addresses = new ArrayList<>();
        for (QueryResultEntry entry : result) {
            addresses.add(new DefaultIpAddressInfo(entry.getObject("IpAddress",InetAddress.class)
                    ,entry.getString("Country"),entry.getString("Region")));
        }
        return addresses;
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
        if(onlineTime == -1){
            this.onlineTime = DKBans.getInstance().getStorage().getOnlineTime(uniqueId);
        }

        if(getActiveSession() != null){
            PlayerSession session = getActiveSession();
            long newOnlineTime = System.currentTimeMillis()-session.getConnectTime();
            return this.onlineTime + newOnlineTime;
        }

        return this.onlineTime;
    }

    @Override
    public boolean hasBypass() {
        DefaultDKBansBypassCheckEvent event = new DefaultDKBansBypassCheckEvent(this,false);
        DKBans.getInstance().getEventBus().callEvent(DKBansBypassCheckEvent.class,event);
        return event.hasBypass();
    }

    @Override
    public PlayerChatLog getChatLog() {
         return DKBans.getInstance().getChatLogManager().getPlayerChatLog(uniqueId);
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
    public void clearNotes() {
        DefaultDKBans.getInstance().getStorage().getPlayerNotes().delete().where("PlayerId",uniqueId).execute();
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
    public PlayerHistoryEntrySnapshot unpunish(DKBansExecutor executor, PunishmentType type, String message) {
        Validate.notNull(executor,type,message);
        PlayerHistoryEntry entry = getHistory().getActiveEntry(type);
        if(entry == null) throw new IllegalArgumentException("Player is not punished for "+type.getName());
        return entry.unpunish(executor,message);
    }

    @Override
    public boolean hasReport() {
        return getReport() != null;
    }

    @Override
    public PlayerReport getReport() {
        return DKBans.getInstance().getReportManager().getOpenReport(this.uniqueId);
    }

    @Override
    public PlayerReport getWatchingReport() {
        return DKBans.getInstance().getReportManager().getReportByWatcher(this.uniqueId);
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
        if(session == null) return;

        session.setDisconnectTime();
        session.setLastServerName(lastServerName);
        session.setLastServerId(lastServerId);

        DKBans.getInstance().getStorage().completePlayerSession(session);

        long newOnlineTime = session.getDisconnectTime()-session.getConnectTime();
        DKBans.getInstance().getStorage().addOnlineTime(getUniqueId(), newOnlineTime);

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
    public DKBansPlayer getAsPlayer() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DKBansPlayer && ((DKBansPlayer)o).getUniqueId().equals(getUniqueId());
    }
}
