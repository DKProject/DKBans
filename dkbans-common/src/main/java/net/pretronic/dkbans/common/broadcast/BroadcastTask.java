/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 18.12.20, 17:31
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

import net.pretronic.dkbans.api.broadcast.BroadcastAssignment;
import net.pretronic.dkbans.api.broadcast.BroadcastGroup;
import net.pretronic.dkbans.common.DefaultDKBans;

import java.util.concurrent.atomic.AtomicBoolean;

public class BroadcastTask implements Runnable {

    private final AtomicBoolean running;

    public BroadcastTask() {
        this.running = new AtomicBoolean(false);
    }

    public Runnable start() {
        this.running.set(true);
        return this;
    }

    public void stop() {
        this.running.set(false);
    }

    @Override
    public void run() {
        for (BroadcastGroup group0 : DefaultDKBans.getInstance().getBroadcastManager().getGroups()) {
            if(!group0.isEnabled()) continue;
            DefaultBroadcastGroup group = ((DefaultBroadcastGroup) group0);
            long interval = group.getInterval()* 1000L;
            if((System.currentTimeMillis()-group.getLastBroadcastTime()) > interval) {

                group.setLastBroadcastTime(System.currentTimeMillis());
                BroadcastAssignment next = group.getNext(group.getCurrentBroadcast());
                if(next != null) {
                    group.setCurrentBroadcast(next.getPosition());
                    DefaultDKBans.getInstance().getBroadcastManager().sendBroadcast(next);
                }
            }
        }
    }
}
