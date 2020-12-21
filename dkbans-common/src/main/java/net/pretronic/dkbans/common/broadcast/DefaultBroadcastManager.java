/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.11.20, 16:58
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

import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.broadcast.*;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DefaultBroadcastManager implements BroadcastManager {

    private final Collection<Broadcast> broadcasts;
    private final Collection<BroadcastGroup> groups;

    private final BroadcastSender broadcastSender;

    public DefaultBroadcastManager(BroadcastSender broadcastSender) {
        this.broadcasts = new ArrayList<>();
        this.groups = new ArrayList<>();
        this.broadcastSender = broadcastSender;
    }

    public void init() {
        this.broadcasts.addAll(loadBroadcasts());
        this.groups.addAll(loadGroups());
    }

    @Override
    public Collection<Broadcast> getBroadcasts() {
        return this.broadcasts;
    }

    @Override
    public Broadcast getBroadcast(int id) {
        return Iterators.findOne(this.broadcasts, broadcast -> broadcast.getId() == id);
    }

    @Override
    public Broadcast getBroadcast(String name) {
        return Iterators.findOne(this.broadcasts, broadcast -> broadcast.getName().equalsIgnoreCase(name));
    }

    @Override
    public Broadcast searchBroadcast(Object search) {
        return Iterators.findOne(this.broadcasts, broadcast -> {
            if(search instanceof Integer) {
                return broadcast.getId() == Integer.parseInt(search.toString());
            } else {
                if(GeneralUtil.isNaturalNumber(search.toString())) {
                    return broadcast.getId() == Integer.parseInt(search.toString());
                }
                return broadcast.getName().equalsIgnoreCase(search.toString());
            }
        });
    }

    @Override
    public Broadcast createBroadcast(String name, String text, BroadcastVisibility visibility) {
        Validate.notNull(name, visibility, text);
        int id = DefaultDKBans.getInstance().getStorage().getBroadcast().insert()
                .set("Name", name)
                .set("visibility", visibility)
                .set("text", text)
                .executeAndGetGeneratedKeyAsInt("Id");

        return new DefaultBroadcast(id, name, visibility, text);
    }

    @Override
    public void deleteBroadcast(int id) {
        Validate.isTrue(id > 0, "Id is invalid, must be greater then 0");
        DefaultDKBans.getInstance().getStorage().getBroadcast().delete().where("Id", id).execute();
    }

    @Override
    public Collection<BroadcastGroup> getGroups() {
        return this.groups;
    }

    @Override
    public BroadcastGroup searchGroup(Object value) {
        return Iterators.findOne(this.groups, group -> value instanceof Integer ? group.getId() == (int) value : group.getName().equalsIgnoreCase((String) value));
    }

    @Override
    public BroadcastGroup getGroup(int id) {
        Validate.isTrue(id > 0, "Id is invalid, must be greater then 0");
        return Iterators.findOne(this.groups, group -> group.getId() == id);
    }

    @Override
    public BroadcastGroup getGroup(String name) {
        Validate.notNull(name);
        return Iterators.findOne(this.groups, group -> group.getName().equalsIgnoreCase(name));
    }

    @Override
    public BroadcastGroup createGroup(String name, int interval) {
        Validate.notNull(name);
        int id = DefaultDKBans.getInstance().getStorage().getBroadcastGroup().insert()
                .set("Name", name)
                .set("Enabled", true)
                .set("Interval", interval)
                .set("Order", BroadcastOrder.SORTED)
                .executeAndGetGeneratedKeyAsInt("Id");
        return new DefaultBroadcastGroup(id, name, interval);
    }

    @Override
    public void deleteGroup(int id) {
        Validate.isTrue(id > 0, "Id is invalid, must be greater then 0");
        DefaultDKBans.getInstance().getStorage().getBroadcastGroup().delete()
                .where("Id", id)
                .execute();
    }

    @Override
    public Collection<DKBansPlayer> sendBroadcast(Broadcast broadcast) {
        return broadcastSender.sendBroadcast(broadcast);
    }

    @Override
    public Collection<DKBansPlayer> sendBroadcast(BroadcastAssignment broadcast) {
        return broadcastSender.sendBroadcast(broadcast);
    }

    private Collection<Broadcast> loadBroadcasts() {
        Collection<Broadcast> broadcasts = new ArrayList<>();
        DefaultDKBans.getInstance().getStorage().getBroadcast().find().execute().loadIn(broadcasts, result ->
                new DefaultBroadcast(result.getInt("Id"), result.getString("Name"),
                        BroadcastVisibility.valueOf(result.getString("Visibility")), result.getString("Text")));
        return broadcasts;
    }

    private Collection<BroadcastGroup> loadGroups() {
        Map<Integer, Collection<BroadcastAssignment>> assignmentsToGroup = new HashMap<>();
        for (QueryResultEntry result : DefaultDKBans.getInstance().getStorage().getBroadcastGroupAssignment().find().execute()) {
            int groupId = result.getInt("GroupId");
            if(!assignmentsToGroup.containsKey(groupId)) {
                assignmentsToGroup.put(groupId, new ArrayList<>());
            }
            Collection<BroadcastAssignment> assignments = assignmentsToGroup.get(groupId);
            assignments.add(new DefaultBroadcastAssignment(result.getInt("Id"), result.getInt("BroadcastId"), groupId, result.getInt("Position")));
        }

        Collection<BroadcastGroup> groups = new ArrayList<>();
        DefaultDKBans.getInstance().getStorage().getBroadcastGroup().find().execute().loadIn(groups, result -> {
            int groupId = result.getInt("Id");
            Collection<BroadcastAssignment> assignments = assignmentsToGroup.get(groupId);
            if(assignments == null) {
                assignments = new ArrayList<>();
            }
            DKBansScope scope = null;
            String rawScopeType = result.getString("ScopeType");
            String rawScopeName = result.getString("ScopeName");
            if(rawScopeType != null && rawScopeName != null) {
                scope = new DKBansScope(rawScopeType, rawScopeName, result.getUniqueId("ScopeId"));
            }
            DefaultBroadcastGroup group = new DefaultBroadcastGroup(groupId,
                    result.getString("Name"),
                    result.getBoolean("Enabled"),
                    result.getString("Permission"),
                    BroadcastOrder.valueOf(result.getString("Order")),
                    result.getInt("Interval"),
                    scope,
                    assignments);
            return group;
        });
        return groups;
    }
}
