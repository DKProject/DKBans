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

package net.pretronic.dkbans.api.broadcast;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.libraries.document.Document;

import java.util.Collection;
import java.util.Map;

public interface BroadcastManager {

    Collection<Broadcast> getBroadcasts();

    Broadcast getBroadcast(int id);

    Broadcast getBroadcast(String name);

    Broadcast searchBroadcast(Object value);

    Broadcast createBroadcast(String name, BroadcastVisibility visibility, Document properties);

    void deleteBroadcast(int id);

    void deleteBroadcast(Broadcast broadcast);


    Collection<BroadcastGroup> getGroups();

    BroadcastGroup searchGroup(Object value);

    BroadcastGroup getGroup(int id);

    BroadcastGroup getGroup(String name);

    BroadcastGroup createGroup(String name, int interval);

    void deleteGroup(int id);

    void deleteGroup(BroadcastGroup group);

    Collection<DKBansPlayer> sendBroadcast(Broadcast broadcast);

    Collection<DKBansPlayer> sendBroadcast(BroadcastAssignment broadcast);
}