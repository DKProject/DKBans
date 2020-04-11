package net.pretronic.dkbans.api.player.history;

public enum PunishmentType {

    BAN(0),
    MUTE(1),
    WARN(2);

    private final int id;

    PunishmentType(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

}
