package net.pretronic.dkbans.api.template;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.utility.GeneralUtil;

import java.util.Collection;

public interface Template {

    int getId();

    String getName();

    TemplateGroup getGroup();

    String getDisplayName();

    String getPermission();

    Collection<String> getAliases();

    PlayerHistoryType getHistoryType();

    boolean isEnabled();

    boolean isHidden();

    Collection<? extends DKBansScope> getScopes();

    TemplateCategory getCategory();

    Document getData();

    default boolean hasName(String name){
        if(getName().equalsIgnoreCase(name) || GeneralUtil.isNaturalNumber(name) && getId() == Integer.parseInt(name)) return true;
        for (String alias : getAliases()){
            if(alias.equalsIgnoreCase(name)) return true;
        }
        return false;
    }

}
