package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.libraries.utility.Iterators;
import net.pretronic.libraries.utility.Validate;

import java.util.ArrayList;
import java.util.Collection;

public final class TemplateType {

    private static final Collection<TemplateType> REGISTRY = new ArrayList<>();

    public static final TemplateType PUNISHMENT = register("PUNISHMENT", PunishmentTemplate.class);


    private final String name;
    private final Class<? extends Template> templateClass;

    private TemplateType(String name, Class<? extends Template> templateClass) {
        this.name = name;
        this.templateClass = templateClass;
    }

    public String getName() {
        return this.name;
    }

    public Class<? extends Template> getTemplateClass() {
        return this.templateClass;
    }


    public static TemplateType register(String name, Class<? extends Template> templateClass) {
        TemplateType templateType = new TemplateType(name, templateClass);
        REGISTRY.add(templateType);
        return templateType;
    }

    public static TemplateType byName(String name) {
        Validate.notNull(name);
        TemplateType templateType = Iterators.findOne(REGISTRY, type -> type.getName().equalsIgnoreCase(name));
        if(templateType == null) throw new IllegalArgumentException("No template type for name " + name + " found");
        return templateType;
    }
}
