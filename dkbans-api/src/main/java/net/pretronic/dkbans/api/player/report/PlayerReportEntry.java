package net.pretronic.dkbans.api.player.report;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.libraries.document.Document;

public interface PlayerReportEntry {

    int getId();

    PlayerReport getReport();

    DKBansExecutor getReporter();

    //template

    String getReason();

    DKBansScope getScope();

    long getTime();

    Document getProperties();

}
