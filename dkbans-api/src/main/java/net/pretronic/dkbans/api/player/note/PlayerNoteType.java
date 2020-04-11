package net.pretronic.dkbans.api.player.note;

public enum PlayerNoteType {

    NORMAL(0),
    WARNING(1),
    IMPORTANT(2);

    private final int id;

    PlayerNoteType(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
