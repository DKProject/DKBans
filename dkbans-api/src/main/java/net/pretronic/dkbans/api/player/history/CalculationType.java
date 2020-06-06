package net.pretronic.dkbans.api.player.history;

public enum CalculationType {

    AMOUNT,
    POINTS;

    public static CalculationType byName(String name) {
        if(name.equalsIgnoreCase("points")) return POINTS;
        return AMOUNT;
    }
}
