package net.pretronic.dkbans.common;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.libraries.document.Document;

import java.util.UUID;

public class DefaultDKBansScope implements DKBansScope {

    private static final UUID ZERO_UUID = new UUID(0, 0);

    private final String type;
    private final String name;
    private final UUID id;

    public DefaultDKBansScope(String type, String name, UUID id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    public static DefaultDKBansScope fromData(Document data) {
        return new DefaultDKBansScope(data.getString("scopeType"), data.getString("scopeName"), ZERO_UUID);
    }
}
