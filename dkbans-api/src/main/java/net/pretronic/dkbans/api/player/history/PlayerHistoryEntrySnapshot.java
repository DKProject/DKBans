/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 13.06.20, 17:19
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

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.libraries.document.Document;

public interface PlayerHistoryEntrySnapshot {

    PlayerHistoryEntry getEntry();

    int getId();


    PlayerHistoryType getHistoryType();

    PunishmentType getPunishmentType();



    String getReason();

    long getTimeout();

    default boolean isPermanently(){
        return getTimeout() <= 0;
    }


    Template getTemplate();


    DKBansExecutor getStuff();

    DKBansScope getScope();

    int getPoints();


    boolean isActive();

    Document getProperties();


    String getRevokeReason();

    Template getRevokeTemplate();


    boolean isModifiedActive();

    long getModifiedTime();

    DKBansExecutor getModifiedBy();


}
