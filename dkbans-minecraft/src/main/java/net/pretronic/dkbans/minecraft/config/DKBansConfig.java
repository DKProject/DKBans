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

package net.pretronic.dkbans.minecraft.config;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.CalculationType;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.annotations.DocumentKey;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.utility.Convert;
import net.pretronic.libraries.utility.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DKBansConfig {

    @DocumentKey("player.onJoin.clearChat")
    public static boolean PLAYER_ON_JOIN_CLEAR_CHAT = true;
    @DocumentKey("player.onJoin.info.teamChat")
    public static boolean PLAYER_ON_JOIN_INFO_TEAMCHAT = true;
    @DocumentKey("player.onJoin.info.report")
    public static boolean PLAYER_ON_JOIN_INFO_REPORT = true;

    public static boolean PLAYER_SESSION_LOGGING = true;
    public static long PLAYER_SESSION_RETENTION = TimeUnit.DAYS.toMillis(180);

    public static void load(DKBans dkBans) {
        File templates = new File("plugins/DKBans/templates/");

        if(!templates.exists()) templates.mkdirs();
        File[] files = templates.listFiles();
        if(files != null && files.length == 0) {
            try {
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/ban.yml"), Paths.get(templates.getPath()+"/ban.yml"));
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/unban.yml"), Paths.get(templates.getPath()+"/unban.yml"));
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/report.yml"), Paths.get(templates.getPath()+"/report.yml"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            FileUtil.processFilesHierarchically(templates, file -> {
                TemplateGroup group = loadTemplateConfig(dkBans, Document.read(file));
                dkBans.getStorage().importTemplateGroup(group);
            });
        }
    }

    public static int importTemplates() {
        File templates = new File("plugins/DKBans/templates/");

        AtomicInteger count = new AtomicInteger();
        FileUtil.processFilesHierarchically(templates, file -> {
            TemplateGroup group = loadTemplateConfig(DKBans.getInstance(), Document.read(file));
            DKBans.getInstance().getStorage().importTemplateGroup(group);
            count.incrementAndGet();
        });
        return count.get();
    }

    private static TemplateGroup loadTemplateConfig(DKBans dkBans, Document document) {
        System.out.println("load template config");

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
            templateGroup = dkBans.getTemplateManager().createTemplateGroup(groupName, templateType, calculationType, new ArrayList<>());
        }

        for (DocumentEntry entry0 : document.getDocument("templates")) {
            Document entry = entry0.toDocument();


            String name = entry.getString("name");
            int id = Convert.toInteger(entry.getKey());

            Document scopes = entry.contains("scopes") ? entry.getDocument("scopes") : Document.newDocument();

            TemplateCategory category = dkBans.getTemplateManager().getTemplateCategory(entry.getString("category"));

            if(category == null) {
                category = dkBans.getTemplateManager().createTemplateCategory(name, name);
            }

            Collection<String> aliases = new ArrayList<>();
            for (DocumentEntry alias : entry.getDocument("aliases")) {
                aliases.add(alias.toPrimitive().getAsString());
            }

            String historyType0 = entry.getString("historyType");

            PlayerHistoryType historyType = dkBans.getHistoryManager().getHistoryType(historyType0);
            if(historyType == null) {
                historyType = dkBans.getHistoryManager().createHistoryType(historyType0);
            }

            Template template = TemplateFactory.create(templateType,
                    id,
                    name,
                    templateGroup,
                    entry.getString("display"),
                    entry.getString("permission"),
                    aliases,
                    historyType,
                    entry.getBoolean("enabled"),
                    entry.getBoolean("hidden"),
                    category,
                    Document.newDocument().add("scopes", scopes).add("durations", entry.getDocument("durations")).add("points", entry.getDocument("points")));
            templates.add(template);
        }
        ((DefaultTemplateGroup)templateGroup).addTemplatesInternal(templates);

        return templateGroup;
    }
}
