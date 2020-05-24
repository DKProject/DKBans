package net.pretronic.dkbans.common.player.history;

import net.pretronic.dkbans.api.player.history.PlayerHistoryType;

public class DefaultPlayerHistoryType implements PlayerHistoryType {

    private final int id;
    private final String name;

    public DefaultPlayerHistoryType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
