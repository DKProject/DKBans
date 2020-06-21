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

package net.pretronic.dkbans.api.support;

import net.pretronic.dkbans.api.player.DKBansPlayer;

import java.util.Collection;
import java.util.List;

public interface SupportTicket extends Iterable<SupportMessage> {


    String getTopic();

    void setTopic(String topic);


    TicketState getState();

    void setState(TicketState state);


    Collection<SupportParticipant> getParticipants();

    SupportParticipant getParticipant(DKBansPlayer player);

    default SupportParticipant addParticipant(DKBansPlayer player){
        return addParticipant(player,false);
    }

    SupportParticipant addParticipant(DKBansPlayer player, boolean hidden);

    void removeParticipant(DKBansPlayer player);

    boolean isParticipant(DKBansPlayer player);

    boolean isHidden(DKBansPlayer player);


    SupportMessage getLastMessage();

    List<SupportMessage> getMessages();


    SupportMessage send(DKBansPlayer player,String message);


}
