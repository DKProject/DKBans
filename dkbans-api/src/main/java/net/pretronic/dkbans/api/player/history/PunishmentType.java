/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 17:26
 * @web %web%
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.pretronic.dkbans.api.player.history;

import net.pretronic.libraries.message.bml.variable.describer.VariableObjectToString;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public class PunishmentType implements VariableObjectToString {

    private static final Collection<PunishmentType> REGISTRY = new ArrayList<>();


    public static final PunishmentType BAN = registerPunishmentType("BAN","&c");

    public static final PunishmentType MUTE = registerPunishmentType("MUTE","&9");

    public static final PunishmentType WARN = registerPunishmentType("WARN","&6");

    public static final PunishmentType KICK = registerPunishmentType("KICK","&e");


    private final String name;
    private final String color;

    private PunishmentType(String name,String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
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

    public static PunishmentType registerPunishmentType(String name,String color) {
        PunishmentType punishmentType = new PunishmentType(name,color);
        REGISTRY.add(punishmentType);
        return punishmentType;
    }

    @Override
    public String toStringVariable() {
        return name;
    }
}
