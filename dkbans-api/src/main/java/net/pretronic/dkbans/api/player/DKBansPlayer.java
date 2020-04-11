package net.pretronic.dkbans.api.player;

import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.util.Collection;
import java.util.List;

public interface DKBansPlayer {


    @Nullable
    PlayerSession getActiveSession();

    @Nullable
    PlayerSession getLastSession();


    Collection<PlayerSetting> getSettings();

    PlayerSetting getSetting(String key);

    PlayerSetting setSetting(String key, Object value);

    boolean hasSetting(String key);

    boolean hasSetting(String key,Object value);


    PlayerChatLog getChatLog();

    List<PlayerNote> getNotes();

    default PlayerNote createNote(DKBansPlayer creator, String message){
        return createNote(creator,message,PlayerNoteType.NORMAL);
    }

    PlayerNote createNote(DKBansPlayer creator, String message, PlayerNoteType type);






}
