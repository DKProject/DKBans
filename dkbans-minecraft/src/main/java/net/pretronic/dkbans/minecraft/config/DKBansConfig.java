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

package net.pretronic.dkbans.minecraft.config;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.CalculationType;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.annotations.DocumentIgnored;
import net.pretronic.libraries.document.annotations.DocumentKey;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import net.pretronic.libraries.utility.io.FileUtil;
import net.pretronic.libraries.utility.map.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DKBansConfig {

    @DocumentKey("format.date.pattern")
    public static String FORMAT_DATE_PATTERN = "dd-MM-yyyy HH:mm";

    public static String FORMAT_DATE_ENDLESSLY = "-";

    @DocumentKey("player.onJoin.clearChat")
    public static boolean PLAYER_ON_JOIN_CLEAR_CHAT = true;
    @DocumentKey("player.onJoin.info.teamChat")
    public static boolean PLAYER_ON_JOIN_INFO_TEAMCHAT = true;

    @DocumentKey("player.onJoin.info.punishNotify")
    public static boolean PLAYER_ON_JOIN_PUNISH_NOTIFY = true;
    @DocumentKey("player.onJoin.info.report")
    public static boolean PLAYER_ON_JOIN_INFO_REPORT = true;
    @DocumentKey("player.onJoin.listReports")
    public static boolean PLAYER_ON_JOIN_LIST_REPORTS = true;

    public static boolean PLAYER_SESSION_LOGGING = true;
    public static String PLAYER_SESSION_RETENTION = "180d";

    public static boolean CHAT_FILTER_ENABLED = true;

    @DocumentKey("chat.filter.repeatDelay")
    public static long CHAT_FILTER_REPEAT_DELAY = 1000;


    public static boolean JOINME_HEAD_ENABLED = true;

    public static boolean JOINME_MULTIPLE_LINES = true;

    public static Collection<DKBansScope> JOINME_DISABLED_SCOPES = new ArrayList<>();

    public static String JOINME_COOLDOWN = DurationProcessor.getStandard().formatShort(Duration.ofMinutes(15));

    @DocumentIgnored
    public static long JOINME_COOLDOWN_DURATION = 0;

    @DocumentKey("ipAddress.blockAltMinPlaytime")
    public static String IP_ADDRESS_BLOCK_ALT_MIN_PLAYTIME = DurationProcessor.getStandard().formatShort(Duration.ofMinutes(3));

    @DocumentIgnored
    public static long IP_ADDRESS_BLOCK_ALT_MIN_PLAYTIME_TIME = 0;

    public static transient SimpleDateFormat FORMAT_DATE;


    public static void load() {
        FORMAT_DATE = new SimpleDateFormat(FORMAT_DATE_PATTERN);
        JOINME_COOLDOWN_DURATION = DurationProcessor.getStandard().parse(JOINME_COOLDOWN).toMillis();
        IP_ADDRESS_BLOCK_ALT_MIN_PLAYTIME_TIME = DurationProcessor.getStandard().parse(IP_ADDRESS_BLOCK_ALT_MIN_PLAYTIME).toMillis();

        File templates = new File("plugins/DKBans/templates/");

        if(!templates.exists()){
            templates.mkdirs();
        }
        File[] files = templates.listFiles();
        if(files != null && files.length == 0) {
            try {
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/ban.yml"), Paths.get(templates.getPath()+"/ban.yml"));
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/unban.yml"), Paths.get(templates.getPath()+"/unban.yml"));
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/report.yml"), Paths.get(templates.getPath()+"/report.yml"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if(DKBans.getInstance().getTemplateManager().getTemplateGroups().isEmpty()) {
            importTemplates();
        }
    }

    public static Pair<Integer, Integer> importTemplates() {
        File templates = new File("plugins/DKBans/templates/");

        int templateCount = 0;
        int groupCount = 0;
        DKBans.getInstance().getTemplateManager().clearCache();
        for (File file : FileUtil.getFilesHierarchically(templates)) {
            TemplateGroup group = loadTemplateConfig(DKBans.getInstance(), Document.read(file));
            DKBans.getInstance().getTemplateManager().importTemplateGroup(group);
            groupCount++;
            templateCount+=(group != null && group.getTemplates() != null ? group.getTemplates().size() : 0);
        }
        DKBans.getInstance().getTemplateManager().loadTemplateGroups();
        return new Pair<>(groupCount, templateCount);
    }

    private static TemplateGroup loadTemplateConfig(DKBans dkBans, Document document) {

        String groupName = document.getString("name");
        TemplateType templateType = TemplateType.byNameOrNull(document.getString("type"));

        if(templateType == null) {
            dkBans.getLogger().warn("Could not load template group {} with type {}", groupName, document.getString("type"));
            return null;
        }

        CalculationType calculationType = CalculationType.byName(document.getString("calculation"));

        List<Template> templates = new ArrayList<>();

        TemplateGroup templateGroup = dkBans.getTemplateManager().getTemplateGroup(groupName);
        if(templateGroup == null) {
            templateGroup = dkBans.getTemplateManager().createTemplateGroup(groupName, templateType, calculationType);
        }

        for (DocumentEntry entry0 : document.getDocument("templates")) {
            Document documentEntry = entry0.toDocument();

            int inGroupId = Convert.toInteger(documentEntry.getKey());

            String name = null;
            String display = null;
            String permission = null;
            boolean enabled = true;
            boolean hidden = false;

            TemplateCategory category = null;
            Collection<String> aliases = new ArrayList<>();
            PlayerHistoryType historyType = null;

            Document data = Document.newDocument();

            for (DocumentEntry entry : documentEntry) {
                switch (entry.getKey().toLowerCase()) {

                    case "name": {
                        name = entry.toPrimitive().getAsString();
                        break;
                    }
                    case "display": {
                        display = entry.toPrimitive().getAsString();
                        break;
                    }
                    case "permission": {
                        permission = entry.toPrimitive().getAsString();
                        break;
                    }
                    case "enabled": {
                        enabled = entry.toPrimitive().getAsBoolean();
                        break;
                    }
                    case "hidden": {
                        hidden = entry.toPrimitive().getAsBoolean();
                        break;
                    }
                    case "category": {
                        String categoryName = entry.toPrimitive().getAsString();
                        category = dkBans.getTemplateManager().getTemplateCategory(categoryName);
                        if(category == null) {
                            category = dkBans.getTemplateManager().createTemplateCategory(categoryName, categoryName);
                        }
                        break;
                    }
                    case "aliases": {
                        for (DocumentEntry alias : entry.toDocument()) {
                            aliases.add(alias.toPrimitive().getAsString());
                        }
                        break;
                    }
                    case "historytype": {
                        String historyType0 = entry.toPrimitive().getAsString();

                        historyType = dkBans.getHistoryManager().getHistoryType(historyType0);
                        if(historyType0 != null && historyType == null) {
                            historyType = dkBans.getHistoryManager().createHistoryType(historyType0);
                        }
                        break;
                    }
                    default: {
                        data.add(entry.getKey(), entry);
                        break;
                    }
                }
            }
            Template template = TemplateFactory.create(templateType, -1, inGroupId, name, templateGroup, display, permission, aliases,
                    historyType, enabled, hidden, category, data);
            templates.add(template);
        }
        ((DefaultTemplateGroup)templateGroup).addTemplatesInternal(templates);

        return templateGroup;
    }
}
