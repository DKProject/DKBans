package net.pretronic.dkbans.api.player.report;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;

import java.util.Collection;
import java.util.List;

public interface PlayerReport {

    int getId();

    DKBansPlayer getPlayer();


    ReportState getState();

    void setState(ReportState state);


    List<PlayerReportEntry> getEntries();


    boolean isWatched();

    Collection<DKBansPlayer> getWatchers();

    void watch(DKBansPlayer player);


    PlayerHistoryEntry accept(DKBansExecutor executor);

    void decline(DKBansExecutor executor);




    default int getCount(){
        return getEntries().size();
    }
}
