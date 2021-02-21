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
    private final int reportId;
    private final UUID reporterId;
    private final int templateId;
    private final String reason;
    private final String serverName;
    private final UUID serverId;
    private final long time;
    private final Document properties;

    private transient PlayerReport cachedReport;
    private transient DKBansExecutor cachedReporter;

    public DefaultPlayerReportEntry(int id, int reportId, UUID reporterId, int templateId, String reason,
                                    String serverName, UUID serverId, long time,
                                    Document properties,PlayerReport cachedReport,DKBansExecutor cachedReporter) {
        this.id = id;
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.templateId = templateId;
        this.reason = reason;
        this.serverName = serverName;
        this.serverId = serverId;
        this.time = time;
        this.properties = properties;

        this.cachedReport = cachedReport;
        this.cachedReporter = cachedReporter;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getReportId() {
        return reportId;
    }

    @Override
    public PlayerReport getReport() {
        if(cachedReport == null) cachedReport =DKBans.getInstance().getReportManager().getReport(reportId);
        return this.cachedReport;
    }

    @Override
    public UUID getReporterId() {
        return reporterId;
    }

    @Override
    public DKBansExecutor getReporter() {
        if(cachedReporter == null) cachedReporter = DKBans.getInstance().getPlayerManager().getPlayer(this.reporterId);
        return this.cachedReporter;
    }

    @Override
    public int getTemplateId() {
        return templateId;
    }

    @Override
    public ReportTemplate getTemplate() {
        return (ReportTemplate) DKBans.getInstance().getTemplateManager().getTemplate(templateId);
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
