package net.pretronic.dkbans.api.template;

import java.util.List;

public interface TemplateGroup extends Iterable<Template> {

    int getId();

    String getName();

    List<Template> getTemplates();

    void addTemplate(Template template);

    void addTemplates(List<Template> templates);

}
