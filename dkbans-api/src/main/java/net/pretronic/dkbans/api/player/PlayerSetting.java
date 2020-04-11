package net.pretronic.dkbans.api.player;

import net.pretronic.libraries.document.Document;

public interface PlayerSetting {

    String getKey();

    String getValue();

    byte getByteValue();

    int getIntValue();

    long getLongValue();

    double getDoubleValue();

    float getFloatValue();

    boolean getBooleanValue();

    Document getDocumentValue();

    void setValue(Object value);

}
