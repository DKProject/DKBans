package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.Validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class TemplateFactory {

    private static final Map<TemplateType, TemplateFactory> FACTORY = new HashMap<>();


    public abstract Template create(int id, String name, String displayName, String permission, Collection<String> aliases,
                                    PlayerHistoryType historyType, PunishmentType punishmentType, boolean enabled, boolean hidden,
                                    Collection<? extends DKBansScope> scopes, TemplateCategory category, Document data);


    public static void register(TemplateType templateType, TemplateFactory factory) {
        FACTORY.put(templateType, factory);
    }

    public static Template create(TemplateType templateType, int id, String name, String displayName, String permission, Collection<String> aliases,
                                  PlayerHistoryType historyType, PunishmentType punishmentType, boolean enabled, boolean hidden,
                                  Collection<? extends DKBansScope> scopes, TemplateCategory category, Document data) {

        Validate.notNull(templateType, name, category);
        TemplateFactory factory = FACTORY.get(templateType);
        if(factory == null) throw new IllegalArgumentException("No template factory for template type " + templateType.getName() + " found");

        return factory.create(id, name, displayName, permission, aliases, historyType, punishmentType, enabled, hidden, scopes, category, data);
    }
}
