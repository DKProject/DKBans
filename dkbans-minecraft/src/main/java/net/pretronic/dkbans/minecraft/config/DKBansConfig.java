package net.pretronic.dkbans.minecraft.config;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.dkbans.common.template.DefaultTemplateGroup;
import net.pretronic.libraries.document.Document;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DKBansConfig {

    public static boolean PLAYER_ON_JOIN_CLEAR_CHAT = true;
    public static boolean PLAYER_ON_JOIN_INFO_TEAMCHAT = true;
    public static boolean PLAYER_ON_JOIN_INFO_REPORT = true;

    public static boolean PLAYER_SESSION_LOGGING = true;
    public static long PLAYER_SESSION_RETENTION = TimeUnit.DAYS.toMillis(180);

    public static void load(DKBans dkBans) {
        File templates = new File("plugins/DKBans/templates/");

        if(!templates.exists()) templates.mkdirs();
        if(templates.listFiles().length == 0) {
            System.out.println("create defaults");
            try {
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/ban.yml"), Paths.get(templates.getPath()+"/ban.yml"));
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/unban.yml"), Paths.get(templates.getPath()+"/unban.yml"));
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/report.yml"), Paths.get(templates.getPath()+"/report.yml"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            FileUtil.processFilesHierarchically(templates, file -> {
                System.out.println("process " + file.getName());
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

        List<Template> templates = new ArrayList<>();

        TemplateGroup templateGroup = dkBans.getTemplateManager().getTemplateGroup(groupName);
        if(templateGroup == null) {
            templateGroup = dkBans.getTemplateManager().createTemplateGroup(groupName, templateType, null, new ArrayList<>());
        }

        for (DocumentEntry entry0 : document.getDocument("templates")) {
            Document entry = entry0.toDocument();


            String name = entry.getString("name");
            int id = Convert.toInteger(entry.getKey());

            Collection<DKBansScope> scopes = new ArrayList<>();

            Document scopes0 = entry.getDocument("scopes");
            if(scopes0 != null) scopes0.forEach(dummyScope -> {
                for (DocumentEntry documentEntry : dummyScope.toDocument()) {
                    scopes.add(new DefaultDKBansScope(documentEntry.getKey(), documentEntry.toPrimitive().getAsString(), null));
                }
            });

            TemplateCategory category = dkBans.getTemplateManager().getTemplateCategory(entry.getString("category"));

            Collection<String> aliases = new ArrayList<>();
            for (DocumentEntry alias : entry.getDocument("aliases")) {
                aliases.add(alias.toPrimitive().getAsString());
            }

            Template template = TemplateFactory.create(templateType, //@Todo remove because defined in template group
                    id,
                    name,
                    templateGroup,
                    entry.getString("displayName"),
                    entry.getString("permission"),
                    aliases,
                    dkBans.getHistoryManager().getHistoryType(entry.getString("historyType")),
                    entry.getBoolean("enabled"),
                    entry.getBoolean("hidden"),
                    scopes,
                    category,
                    Document.newDocument().add("durations", entry.getDocument("durations")).add("points", entry.getDocument("points")));
            templates.add(template);
        }
        ((DefaultTemplateGroup)templateGroup).addTemplatesInternal(templates);

        return templateGroup;
    }
}
