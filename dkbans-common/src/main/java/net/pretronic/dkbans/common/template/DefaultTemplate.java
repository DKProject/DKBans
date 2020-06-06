package net.pretronic.dkbans.common.template;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshotBuilder;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.TemplateType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplateEntry;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.Collection;

public class DefaultTemplate implements Template {

    private int id;
    private final String name;
    private final TemplateGroup group;
    private final String displayName;
    private final String permission;
    private final Collection<String> aliases;
    private final PlayerHistoryType historyType;
    private final boolean enabled;
    private final boolean hidden;
    private final Collection<? extends DKBansScope> scopes;
    private final TemplateCategory category;
    private final Document data;

    public DefaultTemplate(int id, String name, TemplateGroup group, String displayName, String permission, Collection<String> aliases, PlayerHistoryType historyType,
                           boolean enabled, boolean hidden, Collection<? extends DKBansScope> scopes,
                           TemplateCategory category, Document data) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.displayName = displayName;
        this.permission = permission;
        this.aliases = aliases;
        this.historyType = historyType;
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
    public TemplateGroup getGroup() {
        return this.group;
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
