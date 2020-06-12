package net.pretronic.dkbans.api.player.history;

import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class PunishmentType {

    private static final Collection<PunishmentType> REGISTRY = new ArrayList<>();


    public static final PunishmentType ALL = registerPunishmentType("ALL");

    public static final PunishmentType BAN = registerPunishmentType("BAN");

    public static final PunishmentType MUTE = registerPunishmentType("MUTE");

    public static final PunishmentType WARN = registerPunishmentType("WARN");

    public static final PunishmentType REPORT = registerPunishmentType("REPORT");

    public static final PunishmentType UNBAN = registerPunishmentType("UNBAN");

    public static final PunishmentType KICK = registerPunishmentType("KICK");


    private final String name;

    private PunishmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PunishmentType &&
                ((PunishmentType)o).getName().equalsIgnoreCase(getName());
    }

    public static PunishmentType getPunishmentType(String name) {
        Validate.notNull(name);
        PunishmentType punishmentType = Iterators.findOne(REGISTRY, type -> type.getName().equalsIgnoreCase(name));
        if(punishmentType == null) throw new IllegalArgumentException("No punishment type for name " + name + " found");
        return punishmentType;
    }

    public static PunishmentType registerPunishmentType(String name) {
        PunishmentType punishmentType = new PunishmentType(name);
        REGISTRY.add(punishmentType);
        return punishmentType;
    }
}
