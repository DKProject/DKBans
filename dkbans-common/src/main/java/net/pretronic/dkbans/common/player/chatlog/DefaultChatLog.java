/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 05.07.20, 15:48
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

package net.pretronic.dkbans.common.player.chatlog;

import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.databasequery.api.query.type.FindQuery;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.chatlog.ChatLog;
import net.pretronic.dkbans.api.player.chatlog.ChatLogEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class DefaultChatLog implements ChatLog {

    @Override
    public List<ChatLogEntry> getAll() {
        return getChatLogEntries(findQuery -> {});
    }

    @Override
    public List<ChatLogEntry> getLast(int amount) {
        return getChatLogEntries(query -> query
                .orderBy("Time", SearchOrder.DESC)
                .limit(amount));
    }

    @Override
    public ChatLogEntry getLast() {
        return getLast(1).get(0);
    }

    @Override
    public List<ChatLogEntry> getFirst(int amount) {
        return getChatLogEntries(query -> query
                .orderBy("Time", SearchOrder.ASC)
                .limit(amount));
    }

    @Override
    public ChatLogEntry getByIndex(int index) {
        return getByIndexRange(index, index).get(0);
    }

    @Override
    public List<ChatLogEntry> getByIndexRange(int startIndex, int endIndex) {
        return getChatLogEntries(query -> query
                .orderBy("Time", SearchOrder.DESC)
                .index(startIndex, endIndex));
    }

    @Override
    public List<ChatLogEntry> getSince(long time) {
        return getChatLogEntries(query -> query
                .whereHigher("Time", time)
                .orderBy("Time", SearchOrder.ASC));
    }

    @Override
    public List<ChatLogEntry> getUntil(long time) {
        return getChatLogEntries(query -> query
                .whereLower("Time", time)
                .orderBy("Time", SearchOrder.ASC));
    }

    @Override
    public List<ChatLogEntry> getBetween(long startTime, long endTime) {
        return getChatLogEntries(query -> query
                .whereBetween("Time", startTime, endTime)
                .orderBy("Time", SearchOrder.ASC));
    }

    @Override
    public List<ChatLogEntry> getPage(int page, int sizePerPage) {
        return getChatLogEntries(query -> query
                .page(page, sizePerPage)
                .orderBy("Time", SearchOrder.ASC));
    }

    private List<ChatLogEntry> getChatLogEntries(Consumer<FindQuery> queryConsumer) {
        FindQuery query = getBaseQuery();
        queryConsumer.accept(query);
        List<ChatLogEntry> entries = new ArrayList<>();
        query.execute().loadIn(entries, this::loadEntry);
        return entries;
    }

    private ChatLogEntry loadEntry(QueryResultEntry entry) {
        return new DefaultChatLogEntry(entry.getInt("Id"),
                entry.getUniqueId("PlayerId"),
                entry.getString("Message"),
                entry.getLong("Time"),
                entry.getString("ServerName"),
                entry.getUniqueId("ServerId"),
                entry.getString("FilterAffiliationArea"));
    }

    public abstract FindQuery getBaseQuery();
}
