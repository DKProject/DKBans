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

package net.pretronic.dkbans.api.broadcast;

import net.pretronic.dkbans.api.DKBansScope;

import java.util.Collection;
import java.util.List;

public interface BroadcastGroup extends Iterable<BroadcastAssignment>{

    int getId();

    String getName();

    void setName(String name);


    boolean isEnabled();

    void setEnabled(boolean enabled);


    String getPermission();

    void setPermission(String permission);


    BroadcastOrder getOrder();

    void setOrder(BroadcastOrder order);


    long getInterval();

    void setInterval(long interval);


    DKBansScope getScope();

    void setScope(DKBansScope scope);


    Collection<BroadcastAssignment> getAssignments();

    BroadcastAssignment getAssignment(Broadcast broadcast);


    List<Broadcast> getBroadcasts();

    default BroadcastAssignment addBroadcast(Broadcast broadcast){
        return addBroadcast(broadcast,getBroadcasts().size()+1);
    }

    BroadcastAssignment addBroadcast(Broadcast broadcast, int position);

    void removeBroadcast(Broadcast broadcast);



    BroadcastAssignment getNext(int position);

    BroadcastAssignment getNext(BroadcastAssignment current);


    void delete();


}
