/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.11.20, 17:15
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

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.broadcast.Broadcast;
import net.pretronic.dkbans.api.broadcast.BroadcastAssignment;
import net.pretronic.dkbans.api.broadcast.BroadcastGroup;
import net.pretronic.dkbans.api.broadcast.BroadcastOrder;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultBroadcastGroup implements BroadcastGroup {

    private final int id;
    private String name;
    private boolean enabled;
    private String permission;
    private BroadcastOrder order;
    private int interval;
    private DKBansScope scope;
    private final Collection<BroadcastAssignment> assignments;

    private long lastBroadcastTime;
    private int currentBroadcast;

    public DefaultBroadcastGroup(int id, String name, int interval) {
        this(id, name, true, null, BroadcastOrder.SORTED, interval, null, new ArrayList<>());
    }

    public DefaultBroadcastGroup(int id, String name, boolean enabled, String permission, BroadcastOrder order, int interval,
                                 DKBansScope scope, Collection<BroadcastAssignment> assignments) {
        Validate.notNull(name, order, assignments);
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.permission = permission;
        this.order = order;
        this.interval = interval;
        this.scope = scope;
        this.assignments = assignments;

        this.lastBroadcastTime = -1;
        this.currentBroadcast = 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        DefaultDKBans.getInstance().getStorage().getBroadcastGroup().update()
                .set("Name", name)
                .where("Id", getId())
                .execute();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        DefaultDKBans.getInstance().getStorage().getBroadcastGroup().update()
                .set("Enabled", enabled)
                .where("Id", getId())
                .execute();
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public void setPermission(String permission) {
        this.permission = permission;
        DefaultDKBans.getInstance().getStorage().getBroadcastGroup().update()
                .set("Permission", permission)
                .where("Id", getId())
                .execute();
    }

    @Override
    public BroadcastOrder getOrder() {
        return this.order;
    }

    @Override
    public void setOrder(BroadcastOrder order) {
        this.order = order;
        DefaultDKBans.getInstance().getStorage().getBroadcastGroup().update()
                .set("Order", order)
                .where("Id", getId())
                .execute();
    }

    @Override
    public int getInterval() {
        return this.interval;
    }

    @Override
    public void setInterval(int interval) {
        this.interval = interval;
        DefaultDKBans.getInstance().getStorage().getBroadcastGroup().update()
                .set("Interval", interval)
                .where("Id", getId())
                .execute();
    }

    @Override
    public DKBansScope getScope() {
        return this.scope;
    }

    @Override
    public void setScope(DKBansScope scope) {
        this.scope = scope;
        DefaultDKBans.getInstance().getStorage().getBroadcastGroup().update()
                .set("ScopeName", scope.getName())
                .set("ScopeType", scope.getType())
                .where("Id", getId())
                .execute();
    }

    @Override
    public Collection<BroadcastAssignment> getAssignments() {
        return this.assignments;
    }

    @Override
    public BroadcastAssignment getAssignment(Broadcast broadcast) {
        return Iterators.findOne(this.assignments, assignment -> assignment.getBroadcast().equals(broadcast));
    }

    @Override
    public BroadcastAssignment searchAssignment(Object search) {
        return Iterators.findOne(this.assignments, assignment -> {
            if(search instanceof Integer) {
                return assignment.getId() == Integer.parseInt(search.toString());
            } else {
                if(GeneralUtil.isNaturalNumber(search.toString())) {
                    return assignment.getId() == Integer.parseInt(search.toString());
                }
                return assignment.getBroadcast().getName().equalsIgnoreCase(search.toString());
            }
        });
    }

    @Override
    public List<Broadcast> getBroadcasts() {
        return Iterators.map(this.assignments, BroadcastAssignment::getBroadcast);
    }

    @Override
    public BroadcastAssignment addBroadcast(Broadcast broadcast, int position) {
        Validate.notNull(broadcast);
        int id = DefaultDKBans.getInstance().getStorage().getBroadcastGroupAssignment().insert()
                .set("BroadcastId", broadcast.getId())
                .set("GroupId", getId())
                .set("Position", position)
                .executeAndGetGeneratedKeyAsInt("Id");
        DefaultBroadcastAssignment assignment = new DefaultBroadcastAssignment(id, broadcast.getId(), getId(), position);
        this.assignments.add(assignment);
        return assignment;
    }

    @Override
    public void removeBroadcast(Broadcast broadcast) {
        Validate.notNull(broadcast);
        DefaultDKBans.getInstance().getStorage().getBroadcastGroupAssignment().delete()
                .where("BroadcastId", broadcast.getId())
                .where("GroupId", getId())
                .execute();
        Iterators.remove(this.assignments, assignment -> assignment.getBroadcast().getId() == broadcast.getId());
    }

    @Override
    public void removeAssignment(BroadcastAssignment assignment) {

    }

    @Override
    public BroadcastAssignment getNext(int position) {
        if(getOrder() == BroadcastOrder.RANDOM) {
            return new ArrayList<>(this.assignments).get(GeneralUtil.getDefaultRandom().nextInt(this.assignments.size()));
        }
        BroadcastAssignment first = null;
        BroadcastAssignment next = null;
        for (BroadcastAssignment assignment : this.assignments) {
            if(first == null || assignment.getPosition() < first.getPosition()) {
                first = assignment;
            }
            if(assignment.getPosition() > position && (next == null || assignment.getPosition() < next.getPosition())) {
                next = assignment;
            }
        }
        return next != null ? next : first;
    }

    @Override
    public BroadcastAssignment getNext(BroadcastAssignment current) {
        Validate.notNull(current);
        return getNext(current.getPosition());
    }

    @Override
    public Iterator<BroadcastAssignment> iterator() {
        return this.assignments.iterator();
    }

    public long getLastBroadcastTime() {
        return lastBroadcastTime;
    }

    public int getCurrentBroadcast() {
        return currentBroadcast;
    }

    public void setLastBroadcastTime(long lastBroadcastTime) {
        this.lastBroadcastTime = lastBroadcastTime;
    }

    public void setCurrentBroadcast(int currentBroadcast) {
        this.currentBroadcast = currentBroadcast;
    }

}
