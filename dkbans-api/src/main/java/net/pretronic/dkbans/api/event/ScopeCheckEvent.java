package net.pretronic.dkbans.api.event;

import net.pretronic.dkbans.api.DKBansScope;

public interface ScopeCheckEvent extends DKBansEvent {

    DKBansScope getScope();

    boolean isValid();

    void setValid(boolean valid);

}
