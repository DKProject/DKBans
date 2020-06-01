package net.pretronic.dkbans.api.storage;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.PlayerSession;
import net.pretronic.dkbans.api.player.PlayerSetting;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;

import java.util.Collection;
import java.util.UUID;

//Todo definieren mit angaben, objekt oder id?
public interface DKBansStorage {

    /* Player */

    Collection<PlayerSetting> loadPlayerSettings(UUID uniqueId);

    int createPlayerSetting(UUID uniqueId, String key, String value);

    void updatePlayerSetting(int entryId, String value);


    Collection<PlayerNote> loadPlayerNotes(UUID uniqueId);

    int createPlayerNote(UUID playerId, UUID creatorId,PlayerNoteType type,String message);


    Collection<TemplateCategory> loadTemplateCategories();

    Collection<Template> loadTemplates();



    /* History */

    int createHistoryEntry(int playerId,int sessionId);//Braucht player, session kann null sein

    int insertHistoryEntrySnapshot(PlayerHistoryEntrySnapshot snapshot);


      /*
    private final PlayerHistoryEntry entry;
    private final int id;
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

    private final long modifyTime;
    private final DKBansExecutor modifier;
     */
}
