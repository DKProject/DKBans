package net.pretronic.dkbans.api.player.history;

public class PunishmentType {

    public static final PunishmentType ALL = new PunishmentType(0,"ALL");

    public static final PunishmentType BAN = new PunishmentType(1,"BAN");

    public static final PunishmentType MUTE = new PunishmentType(2,"MUTE");

    public static final PunishmentType WARN = new PunishmentType(3,"WARN");

    public static final PunishmentType REPORT = new PunishmentType(4,"REPORT");

    public static final PunishmentType UNBAN = new PunishmentType(5,"UNBAN");

    public static final PunishmentType KICK = new PunishmentType(5,"KICK");



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

    @Override
    public boolean equals(Object o) {
        return o instanceof PunishmentType &&
                ((PunishmentType)o).getId() == getId();
    }
}
