package net.pretronic.dkbans.api.storage;

import net.pretronic.dkbans.api.player.PlayerSetting;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;

import java.util.Collection;
import java.util.UUID;

public interface DKBansStorage {

    /* Player */

    Collection<PlayerSetting> loadPlayerSettings(UUID uniqueId);

    int createPlayerSetting(UUID uniqueId, String key, String value);

    void updatePlayerSetting(int entryId, String value);


    Collection<PlayerNote> loadPlayerNotes(UUID uniqueId);

    int createPlayerNote(UUID playerId, UUID creatorId,PlayerNoteType type,String message);



}
