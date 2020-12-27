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

package net.pretronic.dkbans.common.player.report;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.template.report.ReportTemplate;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

import java.util.UUID;

public class DefaultPlayerReportEntry implements PlayerReportEntry {

    private final int id;
    private final PlayerReport report;
    private final UUID reporterId;
    private final int tempalteId;
    private final String reason;
    private final String serverName;
    private final UUID serverId;
    private final long time;
    private final Document properties;

    private DKBansExecutor reporter;

    public DefaultPlayerReportEntry(int id, PlayerReport report, UUID reporterId, int tempalteId, String reason,
                                    String serverName, UUID serverId, long time, Document properties) {
        Validate.notNull(report, reporterId);
        this.id = id;
        this.report = report;
        this.reporterId = reporterId;
        this.tempalteId = tempalteId;
        this.reason = reason;
        this.serverName = serverName;
        this.serverId = serverId;
        this.time = time;
        this.properties = properties;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public PlayerReport getReport() {
        return this.report;
    }

    @Override
    public UUID getReporterId() {
        return reporterId;
    }

    @Override
    public DKBansExecutor getReporter() {
        if(reporter == null){
            reporter = DKBans.getInstance().getPlayerManager().getPlayer(this.reporterId);
        }
        return this.reporter;
    }

    @Override
    public ReportTemplate getTemplate() {
        return (ReportTemplate) DKBans.getInstance().getTemplateManager().getTemplate(tempalteId);
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public String getServerName() {
        return this.serverName;
    }

    @Override
    public UUID getServerId() {
        return this.serverId;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    @Override
    public Document getProperties() {
        return this.properties;
    }
}
