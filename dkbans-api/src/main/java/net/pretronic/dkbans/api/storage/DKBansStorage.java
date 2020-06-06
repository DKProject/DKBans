package net.pretronic.dkbans.api.storage;

import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateGroup;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface DKBansStorage {

    /* Player */

    Collection<PlayerNote> loadPlayerNotes(UUID uniqueId);

    int createPlayerNote(UUID playerId, UUID creatorId,PlayerNoteType type,String message);


    Collection<TemplateCategory> loadTemplateCategories();

    Collection<TemplateGroup> loadTemplateGroups();




    /* History */

    PlayerHistoryEntrySnapshot createHistoryEntry(UUID playerId,int sessionId);

    int insertHistoryEntrySnapshot(PlayerHistoryEntrySnapshot snapshot);


    List<PlayerHistoryEntry> loadActiveEntries(UUID uniqueId);

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
