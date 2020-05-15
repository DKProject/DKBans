package net.pretronic.dkbans.common.player;

import net.pretronic.dkbans.api.player.PlayerSetting;
import net.pretronic.libraries.document.Document;
import net.pretronic.libraries.document.type.DocumentFileType;
import net.pretronic.libraries.utility.Convert;

public class DefaultPlayerSetting implements PlayerSetting {

    private final String key;
    private String value;

    public DefaultPlayerSetting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public byte getByteValue() {
        return Convert.toByte(getValue());
    }

    @Override
    public int getIntValue() {
        return Convert.toInteger(getValue());
    }

    @Override
    public long getLongValue() {
        return Convert.toLong(getValue());
    }

    @Override
    public double getDoubleValue() {
        return Convert.toDouble(getValue());
    }

    @Override
    public float getFloatValue() {
        return Convert.toFloat(getValue());
    }

    @Override
    public boolean getBooleanValue() {
        return Convert.toBoolean(getValue());
    }

    @Override
    public Document getDocumentValue() {
        return DocumentFileType.JSON.getReader().read(getValue());
    }

    @Override
    public void setValue(Object value) {
        this.value = value.toString();
    }
}
