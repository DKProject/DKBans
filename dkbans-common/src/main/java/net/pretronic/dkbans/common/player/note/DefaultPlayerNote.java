package net.pretronic.dkbans.common.player.note;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;

public class DefaultPlayerNote implements PlayerNote {

    private final int id;
    private final PlayerNoteType type;
    private final long time;
    private final String message;
    private final DKBansExecutor creator;

    public DefaultPlayerNote(int id, PlayerNoteType type, long time, String message, DKBansExecutor creator) {
        this.id = id;
        this.type = type;
        this.time = time;
        this.message = message;
        this.creator = creator;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PlayerNoteType getType() {
        return type;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public DKBansExecutor getCreator() {
        return creator;
    }
}
