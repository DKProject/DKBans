package net.pretronic.dkbans.api;

import net.pretronic.libraries.logging.PretronicLogger;

public abstract class DKBans {

    private static DKBans INSTANCE;

    public abstract PretronicLogger getLogger();





    public static DKBans getInstance() {
        return INSTANCE;
    }

    public static void setInstance(DKBans instance) {
        if(INSTANCE != null) throw new IllegalArgumentException("Instance is already set");
        INSTANCE = instance;
    }
}
