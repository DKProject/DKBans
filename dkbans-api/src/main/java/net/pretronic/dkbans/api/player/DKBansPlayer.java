package net.pretronic.dkbans.api.player;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.util.Collection;
import java.util.List;

public interface DKBansPlayer extends DKBansExecutor {


    PlayerHistory getHistory();

    @Nullable
    PlayerSession getActiveSession();

    @Nullable
    PlayerSession getLastSession();


    DKBansScope getCurrentScope();//Ableitung von Active Session (Server)


    Collection<PlayerSetting> getSettings();

    PlayerSetting getSetting(String key);

    PlayerSetting setSetting(String key, String value);

    boolean hasSetting(String key);

    boolean hasSetting(String key,Object value);


    PlayerChatLog getChatLog();

    List<PlayerNote> getNotes();

    default PlayerNote createNote(DKBansExecutor creator, String message){
        return createNote(creator,message,PlayerNoteType.NORMAL);
    }

    PlayerNote createNote(DKBansExecutor creator, String message, PlayerNoteType type);



    boolean hasActivePunish(PunishmentType type);

    PlayerHistoryEntry punish(DKBansExecutor player, Template template);

    PunishmentBuilder punish();


    boolean hasReport();

    PlayerReport getReport();


    default PlayerReportEntry report(DKBansExecutor player, Template template){
        return report(player,template,getCurrentScope());
    }

    PlayerReportEntry report(DKBansExecutor player, Template template, DKBansScope scope);

    default PlayerReportEntry report(DKBansExecutor player,String reason){
        return report(player,reason,getCurrentScope());
    }

    PlayerReportEntry report(DKBansExecutor player,String reason, DKBansScope scope);






}
