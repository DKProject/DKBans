package net.pretronic.dkbans.common.player.report;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.report.PlayerReport;
import net.pretronic.dkbans.api.player.report.PlayerReportEntry;
import net.pretronic.dkbans.api.player.report.ReportState;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.annonations.Internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultPlayerReport implements PlayerReport {

    private final int id;
    private final DKBansPlayer player;
    private ReportState state;
    private final List<PlayerReportEntry> entries;
    private final Collection<DKBansPlayer> watchers;

    public DefaultPlayerReport(int id, DKBansPlayer player, ReportState state, List<PlayerReportEntry> entries, Collection<DKBansPlayer> watchers) {
        this.id = id;
        this.player = player;
        this.state = state;
        this.entries = entries;
        this.watchers = watchers;
    }

    public DefaultPlayerReport(int id, DKBansPlayer player, ReportState state) {
        this(id, player, state, new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public DKBansPlayer getPlayer() {
        return this.player;
    }

    @Override
    public ReportState getState() {
        return this.state;
    }

    @Override
    public void setState(ReportState state) {
        Validate.notNull(state, "Report state can't be null");
        this.state = state;
    }

    @Override
    public List<PlayerReportEntry> getEntries() {
        return this.entries;
    }

    @Override
    public boolean isWatched() {
        return !this.watchers.isEmpty();
    }

    @Override
    public Collection<DKBansPlayer> getWatchers() {
        return this.watchers;
    }

    @Override
    public void watch(DKBansPlayer player) {
        this.watchers.add(player);
        //@Todo teleport player
    }

    @Override
    public PlayerHistoryEntry accept(DKBansExecutor executor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decline(DKBansExecutor executor) {
        throw new UnsupportedOperationException();
    }

    @Internal
    public void addEntry(DefaultPlayerReportEntry entry) {
        this.entries.add(entry);
    }
}
