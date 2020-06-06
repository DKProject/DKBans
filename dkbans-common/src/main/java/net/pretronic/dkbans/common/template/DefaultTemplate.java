package net.pretronic.dkbans.common.template;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.Collection;

public class DefaultTemplate implements Template {

    private int id;
    private final String name;
    private final String displayName;
    private final String permission;
    private final Collection<String> aliases;
    private final PlayerHistoryType historyType;
    private final PunishmentType punishmentType;
    private final boolean enabled;
    private final boolean hidden;
    private final Collection<? extends DKBansScope> scopes;
    private final TemplateCategory category;
    private final Document data;

    public DefaultTemplate(int id, String name, String displayName, String permission, Collection<String> aliases, PlayerHistoryType historyType,
                           PunishmentType punishmentType, boolean enabled, boolean hidden, Collection<? extends DKBansScope> scopes,
                           TemplateCategory category, Document data) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.permission = permission;
        this.aliases = aliases;
        this.historyType = historyType;
        this.punishmentType = punishmentType;
        this.enabled = enabled;
        this.hidden = hidden;
        this.scopes = scopes;
        this.category = category;
        this.data = data;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getPermission() {
        return this.permission;
    }

    @Override
    public Collection<String> getAliases() {
        return this.aliases;
    }

    @Override
    public PlayerHistoryType getHistoryType() {
        return this.historyType;
    }

    @Override
    public PunishmentType getPunishmentType() {
        return this.punishmentType;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isHidden() {
        return this.hidden;
    }

    @Override
    public Collection<? extends DKBansScope> getScopes() {
        return this.scopes;
    }

    @Override
    public TemplateCategory getCategory() {
        return this.category;
    }

    @Override
    public Document getData() {
        return this.data;
    }


    @Internal
    public void setIdInternal(int id) {
        this.id = id;
    }
}
