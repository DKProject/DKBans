package net.pretronic.dkbans.common.player;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.api.player.PlayerSetting;
import net.pretronic.dkbans.api.player.PunishmentBuilder;
import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.common.player.note.DefaultPlayerNote;
import net.pretronic.libraries.utility.Iterators;

import java.util.Collection;
import java.util.UUID;

public class DefaultDKBansPlayer implements DKBansPlayer {

    private final UUID uniqueId;
    private final String name;
    private final Collection<PlayerSetting> settings;
    private final PlayerSession lastSession;

    public DefaultDKBansPlayer() {
        this.uniqueId = null;
        this.name = null;
        this.settings = null;
        this.lastSession = null;
    }

    @Override
    public PlayerHistory getHistory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PlayerSession getActiveSession() {
         return lastSession.isActive() ? lastSession : null;
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
    public Collection<PlayerSetting> getSettings() {
         return settings;
    }

    @Override
    public PlayerSetting getSetting(String key) {
         return Iterators.findOne(this.settings, setting -> setting.getKey().equalsIgnoreCase(key));
    }

    @Override
    public PlayerSetting setSetting(String key, String value) {
         Iterators.removeOne(this.settings, setting -> setting.getKey().equalsIgnoreCase(key));
         PlayerSetting setting = new DefaultPlayerSetting(key, value);
         settings.add(setting);
         return setting;
    }

    @Override
    public boolean hasSetting(String key) {
        return Iterators.findOne(this.settings, setting -> setting.getKey().equalsIgnoreCase(key)) != null;
    }

    @Override
    public boolean hasSetting(String key, Object value) {
        PlayerSetting settings = getSetting(key);
        return settings != null && settings.equalsValue(value);
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
        //@Todo create to storage
        int id = -1;
        PlayerNote note = new DefaultPlayerNote(id,type,System.currentTimeMillis(),message,creator);
         throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasActivePunish(PunishmentType type) {
        return false;
    }

    @Override
    public PlayerHistoryEntry punish(DKBansExecutor player, Template template) {
         throw new UnsupportedOperationException();
    }

    @Override
    public PunishmentBuilder punish() {
         throw new UnsupportedOperationException();
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
