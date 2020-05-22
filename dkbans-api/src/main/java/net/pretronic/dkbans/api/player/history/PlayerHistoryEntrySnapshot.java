package net.pretronic.dkbans.api.player.history;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.template.Template;
import net.pretronic.libraries.document.Document;

public interface PlayerHistoryEntrySnapshot {

    PlayerHistoryEntry getEntry();

    int getId();


    PlayerHistoryType getHistoryType();

    PunishmentType getPunishmentType();



    String getReason();

    long getTimeout();


    Template getTemplate();


    DKBansExecutor getStuff();

    DKBansScope getScope();

    int getPoints();


    boolean isActive();

    Document getProperties();

    String getRevokeMessage();

    //getRevokeTemplate


    long getModifyTime();

    DKBansExecutor getModifiedBy();


}
