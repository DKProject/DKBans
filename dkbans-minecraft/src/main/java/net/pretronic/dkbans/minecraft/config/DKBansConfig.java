package net.pretronic.dkbans.minecraft.config;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.*;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class DKBansConfig {

    public static boolean PLAYER_ON_JOIN_CLEAR_CHAT = true;


    public static CommandConfiguration COMMAND_JUMP_TO = CommandConfiguration.newBuilder()
            .name("jumpto")
            .aliases("goto")
            .permission("dkbans.command.jumpto")
            .create();

    public static CommandConfiguration COMMAND_ONLINE_TIME = CommandConfiguration.newBuilder()
            .name("onlinetime")
            .permission("dkbans.command.onlinetime")
            .create();

    public static CommandConfiguration COMMAND_PING = CommandConfiguration.newBuilder()
            .name("ping")
            .permission("dkbans.command.ping")
            .create();

    public static void load(DKBans dkBans) {
        File templates = new File("plugins/DKBans/templates/");

        if(!templates.exists()) templates.mkdirs();

        AtomicBoolean createDefaultPunishmentTemplate = new AtomicBoolean(true);
        AtomicBoolean createDefaultUnPunishmentTemplate = new AtomicBoolean(true);
        AtomicBoolean createDefaultReportTemplate = new AtomicBoolean(true);


        FileUtil.processFilesHierarchically(templates, file -> {
            TemplateType templateType = loadTemplateConfig(dkBans, Document.read(file));
            if(templateType != null) {
                if(templateType.equals(TemplateType.PUNISHMENT)) createDefaultPunishmentTemplate.set(false);
                else if(templateType.equals(TemplateType.UNPUNISHMENT)) createDefaultUnPunishmentTemplate.set(false);
                else if(templateType.equals(TemplateType.REPORT)) createDefaultReportTemplate.set(false);
            }
        });

        if(createDefaultPunishmentTemplate.get()) {
            try {
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/ban.yml"), Paths.get(templates.getPath()+"/ban.yml"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if(createDefaultUnPunishmentTemplate.get()) {
            try {
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/unban.yml"), Paths.get(templates.getPath()+"/unban.yml"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        if(createDefaultReportTemplate.get()) {
            try {
                Files.copy(DKBansConfig.class.getResourceAsStream("/templates/report.yml"), Paths.get(templates.getPath()+"/report.yml"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static TemplateType loadTemplateConfig(DKBans dkBans, Document document) {

        String groupName = document.getString("name");
        TemplateType templateType = TemplateType.byNameOrNull(document.getString("type"));

        if(templateType == null) {
            DKBans.getInstance().getLogger().warn("Could not load template group {} with type {}", groupName, document.getString("type"));
            return null;
        }

        List<Template> templates = new ArrayList<>();

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
                    entry.getString("displayName"),
                    entry.getString("permission"),
                    aliases,
                    null/*dkBans.getHistoryManager().getHistoryType(entry.getString("historyType"))@Todo replace if history manager exists, create if not exists*/,
                    PunishmentType.getPunishmentType(entry.getString("punishmentType")),
                    entry.getBoolean("enabled"),
                    entry.getBoolean("hidden"),
                    scopes,
                    category,
                    Document.newDocument().add("durations", entry.getDocument("durations")).add("points", entry.getDocument("points")));
            templates.add(template);
        }

        TemplateGroup templateGroup = dkBans.getTemplateManager().getTemplateGroup(groupName);
        if(templateGroup == null) {
            dkBans.getTemplateManager().createTemplateGroup(groupName, templates);
        } else {
            templateGroup.addTemplates(templates);
        }
        return templateType;
    }
}
