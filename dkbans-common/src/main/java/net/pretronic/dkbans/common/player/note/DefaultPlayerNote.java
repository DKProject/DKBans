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
    public String getFormattedTime() {
        return null;
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
