package net.pretronic.dkbans.common.template;

import net.pretronic.dkbans.api.template.Template;
import net.pretronic.dkbans.api.template.TemplateGroup;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultTemplateGroup implements TemplateGroup {

    private final int id;
    private final String name;
    private final List<Template> templates;

    public DefaultTemplateGroup(int id, String name, List<Template> templates) {
        this.id = id;
        this.name = name;
        this.templates = templates;
    }

    public DefaultTemplateGroup(int id, String name) {
        this(id, name, new ArrayList<>());
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
}
