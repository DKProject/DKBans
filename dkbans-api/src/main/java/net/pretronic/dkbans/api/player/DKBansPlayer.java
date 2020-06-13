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

package net.pretronic.dkbans.api.player;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.chatlog.PlayerChatLog;
import net.pretronic.dkbans.api.player.history.PlayerHistory;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.player.note.PlayerNote;
import net.pretronic.dkbans.api.player.note.PlayerNoteList;
import net.pretronic.dkbans.api.player.note.PlayerNoteType;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplate;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.net.InetAddress;

public interface DKBansPlayer extends DKBansExecutor {


    PlayerHistory getHistory();

    @Nullable
    PlayerSession getActiveSession();

    @Nullable
    PlayerSession getLastSession();

    long getOnlineTime();


    DKBansScope getCurrentScope();//Ableitung von Active Session (Server)


    boolean hasBypass();


    PlayerChatLog getChatLog();

    PlayerNoteList getNotes();

    default PlayerNote createNote(DKBansExecutor creator, String message){
        return createNote(creator,message,PlayerNoteType.NORMAL);
    }

    PlayerNote createNote(DKBansExecutor creator, String message, PlayerNoteType type);



    boolean hasActivePunish(PunishmentType type);

    PlayerHistoryEntrySnapshot punish(DKBansExecutor executor, PunishmentTemplate template);

    PlayerHistoryEntrySnapshotBuilder punish();


    void unpunish(DKBansExecutor executor, UnPunishmentTemplate template);

    void unpunish(DKBansExecutor executor,PunishmentType type,String reason);


    PlayerHistoryEntrySnapshot kick(DKBansExecutor executor,String reason);


    boolean hasReport();

    PlayerReport getReport();


    default PlayerReportEntry report(DKBansExecutor player, Template template){
        return report(player,template,getCurrentScope());
    }

    PlayerReportEntry report(DKBansExecutor player, Template template, DKBansScope scope);

    default PlayerReportEntry report(DKBansExecutor player,String reason){
        return report(player,reason,getCurrentScope());
    }

    PlayerReportEntry report(DKBansExecutor player,String reason, DKBansScope scope);


    void startSession(String currentName, InetAddress address);

    void finishSession();


}
