package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.player.history.CalculationType;
import net.pretronic.libraries.utility.Iterators;

import java.util.List;

public interface TemplateGroup extends Iterable<Template> {

    int getId();

    String getName();

    TemplateType getTemplateType();

    CalculationType getCalculationType();

    List<Template> getTemplates();

    void addTemplate(Template template);

    void addTemplates(List<Template> templates);

    default Template getTemplate(String name){
        return Iterators.findOne(getTemplates(), template -> template.hasName(name));
    }

}
