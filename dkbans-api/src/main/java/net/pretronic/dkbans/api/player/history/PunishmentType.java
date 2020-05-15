package net.pretronic.dkbans.api.player.history;

public class PunishmentType {

    public static final PunishmentType BAN = new PunishmentType(1,"Ban");

    public static final PunishmentType MUTE = new PunishmentType(2,"Mute");

    public static final PunishmentType WARN = new PunishmentType(3,"Warn");


    private final int id;
    private final String name;

    public PunishmentType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
