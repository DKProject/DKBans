package net.pretronic.dkbans.common.template;

import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.dkbans.api.template.TemplateType;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultTemplateGroup implements TemplateGroup {

    private int id;
    private final String name;
    private final TemplateType type;
    private final List<Template> templates;

    public DefaultTemplateGroup(int id, String name, TemplateType type, List<Template> templates) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.templates = templates;
    }

    public DefaultTemplateGroup(int id, String name, TemplateType type) {
        this(id, name, type, new ArrayList<>());
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
    public TemplateType getType() {
        return this.type;
    }

    @Override
    public List<Template> getTemplates() {
        return this.templates;
    }

    @Override
    public void addTemplate(Template template) {
        this.templates.add(template);
    }

    @Override
    public void addTemplates(List<Template> templates) {
        this.templates.addAll(templates);
    }

    @Override
    public Iterator<Template> iterator() {
        return this.templates.iterator();
    }


    @Internal
    public void addTemplateInternal(Template template) {
        this.templates.add(template);
    }

    @Internal
    public void setIdInternal(int id) {
        this.id = id;
    }
}
