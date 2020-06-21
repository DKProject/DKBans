/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.06.20, 17:26
 * @web %web%
 *
 * The DKBans Project is under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

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

    @Override
    public boolean equalsValue(Object value) {
        throw new UnsupportedOperationException();
    }
}
