package net.pretronic.dkbans.minecraft.config;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateFactory;
import net.pretronic.dkbans.api.template.TemplateType;
import net.pretronic.dkbans.common.DefaultDKBansScope;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.entry.DocumentEntry;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class DKBansConfig {

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
        //@Todo just for testing
        loadTemplateConfig(dkBans, TemplateType.PUNISHMENT, DocumentFileType.YAML.getReader().read(new File("plugins/DKBans/demo.yml")));
    }

    private static void loadTemplateConfig(DKBans dkBans, TemplateType templateType, Document document) {
        System.out.println(DocumentFileType.JSON.getWriter().write(document, true));
        for (DocumentEntry entry0 : document) {
            Document entry = entry0.toDocument();

            int id = Convert.toInteger(entry.getKey());

            Collection<DKBansScope> scopes = new ArrayList<>();

            //@Todo maybe scope id
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
            Template template = TemplateFactory.create(templateType,
                    id,
                    entry.getString("name"),
                    entry.getString("displayName"),
                    entry.getString("permission"),
                    aliases,
                    null/*dkBans.getHistoryManager().getHistoryType(entry.getString("historyType"))@Todo replace if history manager exists*/,
                    PunishmentType.getPunishmentType(entry.getString("punishmentType")),
                    entry.getBoolean("enabled"),
                    entry.getBoolean("hidden"),
                    scopes,
                    category,
                    Document.newDocument().add("durations", entry.getDocument("durations")).add("points", entry.getDocument("points")));
            dkBans.getTemplateManager().addTemplate(template);
        }
    }
}
