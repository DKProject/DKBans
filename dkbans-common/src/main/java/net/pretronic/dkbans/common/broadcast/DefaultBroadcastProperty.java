/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.12.20, 13:51
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

package net.pretronic.dkbans.common.broadcast;

import net.pretronic.dkbans.api.broadcast.BroadcastProperty;
import net.pretronic.libraries.utility.Convert;

public class DefaultBroadcastProperty implements BroadcastProperty {

    private final String key;
    private final Object value;

    public DefaultBroadcastProperty(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Object getValue() {
        return this.value;
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
    public String getStringValue() {
        return Convert.toString(getValue());
    }

    @Override
    public boolean getBooleanValue() {
        return Convert.toBoolean(getValue());
    }


}
