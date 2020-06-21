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

package net.pretronic.dkbans.common.template.unpunishment;

import net.pretronic.dkbans.api.template.unpunishment.UnPunishmentTemplateEntryOption;

import java.util.Map;

public class DefaultUnPunishmentTemplateEntryOption implements UnPunishmentTemplateEntryOption {

    private final long removeTime;
    private final double divider;
    private final Map<String, Object> properties;

    public DefaultUnPunishmentTemplateEntryOption(long removeTime, double divider, Map<String, Object> properties) {
        this.removeTime = removeTime;
        this.divider = divider;
        this.properties = properties;
    }

    @Override
    public long getRemoveTime() {
        return this.removeTime;
    }

    @Override
    public double getDivider() {
        return this.divider;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }
}
