package net.pretronic.dkbans.api.player.history;

import net.pretronic.libraries.utility.Iterators;

import java.util.ArrayList;
import java.util.Collection;

public class PunishmentType {

    private static final Collection<PunishmentType> REGISTRY = new ArrayList<>();


    public static final PunishmentType ALL = registerPunishmentType(0,"ALL");

    public static final PunishmentType BAN = registerPunishmentType(1,"BAN");

    public static final PunishmentType MUTE = registerPunishmentType(2,"MUTE");

    public static final PunishmentType WARN = registerPunishmentType(3,"WARN");

    public static final PunishmentType REPORT = registerPunishmentType(4,"REPORT");

    public static final PunishmentType UNBAN = registerPunishmentType(5,"UNBAN");

    public static final PunishmentType KICK = registerPunishmentType(5,"KICK");



    private final int id;
    private final String name;

    private PunishmentType(int id, String name) {
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


    public static PunishmentType getPunishmentType(int id) {
        return Iterators.findOne(REGISTRY, type -> type.getId() == id);
    }

    public static PunishmentType getPunishmentType(String name) {
        return Iterators.findOne(REGISTRY, type -> type.getName().equalsIgnoreCase(name));
    }

    public static PunishmentType registerPunishmentType(int id, String name) {
        PunishmentType punishmentType = new PunishmentType(id, name);
        REGISTRY.add(punishmentType);
        return punishmentType;
    }
}
