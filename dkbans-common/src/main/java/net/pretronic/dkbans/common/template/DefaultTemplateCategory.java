package net.pretronic.dkbans.common.template;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateCategory;

import java.util.Collection;

public class DefaultTemplateCategory implements TemplateCategory {

    private final int id;
    private final String name;
    private final String displayName;

    public DefaultTemplateCategory(int id, String name, String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
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
    public Collection<Template> getTemplates() {
        return DKBans.getInstance().getTemplateManager().getTemplates(this);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TemplateCategory &&
                ((TemplateCategory)o).getId() == getId();
    }
}
