package net.pretronic.dkbans.common.player;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.api.player.history.*;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplate;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistory;
import net.pretronic.dkbans.common.player.history.DefaultPlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.common.player.note.DefaultPlayerNote;
import net.pretronic.libraries.utility.Validate;

import java.net.InetAddress;
import java.util.UUID;

public class DefaultDKBansPlayer implements DKBansPlayer {

    private final UUID uniqueId;
    private final String name;
    private final PlayerSession lastSession;

    private final PlayerHistory history;

    public DefaultDKBansPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.history = new DefaultPlayerHistory(this);

        this.lastSession = null;
    }

    @Override
    public PlayerHistory getHistory() {
        return history;
    }

    @Override
    public PlayerSession getActiveSession() {
         return lastSession != null && lastSession.isActive() ? lastSession : null;
    }

    @Override
    public PlayerSession getLastSession() {
         return lastSession;
    }

    @Override
    public long getOnlineTime() {
        throw new UnsupportedOperationException();
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
         throw new UnsupportedOperationException();
    }

    @Override
    public PlayerNote createNote(DKBansExecutor creator, String message, PlayerNoteType type) {
        Validate.notNull(creator,message,type);
        //@Todo create to storage
        int id = -1;
        PlayerNote note = new DefaultPlayerNote(id,type,System.currentTimeMillis(),message,creator);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasActivePunish(PunishmentType type) {
        Validate.notNull(type);
        return history.hasActivePunish(type);
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
    public void unpunish(DKBansExecutor executor, UnPunishmentTemplate template) {
        Validate.notNull(executor,template);
        throw new UnsupportedOperationException();
    }

    @Override
    public void unpunish(DKBansExecutor executor, PunishmentType type, String reason) {
        Validate.notNull(executor,type,reason);
        PlayerHistoryEntry entry = getHistory().getActiveEntry(type);
        if(entry != null){
            entry.newSnapshot(executor)
                    .active(false)
                    .revokeReason(reason);
        }
    }

    @Override
    public PlayerHistoryEntrySnapshot kick(DKBansExecutor executor, String reason) {
        Validate.notNull(executor,reason);
        return punish()
                .stuff(executor)
                .punishmentType(PunishmentType.KICK)
                .reason(reason)
                .execute();
    }

    @Override
    public boolean hasReport() {
        return false;
    }

    @Override
    public PlayerReport getReport() {
         throw new UnsupportedOperationException();
    }

    @Override
    public PlayerReportEntry report(DKBansExecutor player, Template template, DKBansScope scope) {
         throw new UnsupportedOperationException();
    }

    @Override
    public PlayerReportEntry report(DKBansExecutor player, String reason, DKBansScope scope) {
         throw new UnsupportedOperationException();
    }

    @Override
    public void startSession(String currentName, InetAddress address) {

    }

    @Override
    public void finishSession() {
        PlayerSession session = getActiveSession();
        //if(session == null) throw new IllegalArgumentException("No active session found");

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
}
