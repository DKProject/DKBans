/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.11.20, 17:13
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

package net.pretronic.dkbans.common.broadcast;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.broadcast.Broadcast;
import net.pretronic.dkbans.api.broadcast.BroadcastAssignment;
import net.pretronic.dkbans.api.broadcast.BroadcastGroup;
import net.pretronic.dkbans.common.DefaultDKBans;

public class DefaultBroadcastAssignment implements BroadcastAssignment {

    private final int id;
    private final int broadcastId;
    private final int groupId;
    private int position;

    public DefaultBroadcastAssignment(int id, int broadcastId, int groupId, int position) {
        this.id = id;
        this.broadcastId = broadcastId;
        this.groupId = groupId;
        this.position = position;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public BroadcastGroup getGroup() {
        return DKBans.getInstance().getBroadcastManager().getGroup(this.groupId);
    }

    @Override
    public Broadcast getBroadcast() {
        return DKBans.getInstance().getBroadcastManager().getBroadcast(this.broadcastId);
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
        DefaultDKBans.getInstance().getStorage().getBroadcastGroupAssignment().update()
                .set("Position", position)
                .where("Id", getId())
                .execute();
    }
}
