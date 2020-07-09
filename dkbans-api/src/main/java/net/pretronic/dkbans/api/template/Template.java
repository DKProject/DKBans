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

package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.GeneralUtil;

import java.util.Collection;

public interface Template {

    int getId();

    int getInGroupId();

    String getName();

    TemplateGroup getGroup();

    String getDisplayName();

    String getPermission();

    Collection<String> getAliases();

    PlayerHistoryType getHistoryType();

    boolean isEnabled();

    boolean isHidden();

    //Collection<? extends DKBansScope> getScopes();

    TemplateCategory getCategory();

    Document getData();

    default boolean hasName(String name){
        if(getName().equalsIgnoreCase(name) || GeneralUtil.isNaturalNumber(name) && getId() == Integer.parseInt(name)) return true;
        for (String alias : getAliases()){
            if(alias.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

}
