/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.07.20, 18:23
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

package net.pretronic.dkbans.common.player.ipblacklist;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlock;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlockType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;

import java.util.UUID;

public class DefaultIpAddressBlock implements IpAddressBlock {

    private int id;
    private final String address;
    private final IpAddressBlockType type;
    private final UUID staffId;
    private final String reason;
    private final long timeout;
    private final String forReason;
    private final long forDuration;
    private final int forTemplateId;

    public DefaultIpAddressBlock(int id, String address, IpAddressBlockType type, UUID staffId, String reason, long timeout, String forReason, long forDuration, int forTemplateId) {
        this.id = id;
        this.address = address;
        this.type = type;
        this.staffId = staffId;
        this.reason = reason;
        this.timeout = timeout;
        this.forReason = forReason;
        this.forDuration = forDuration;
        this.forTemplateId = forTemplateId;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getAddress() {
        return this.address;
    }

    @Override
    public IpAddressBlockType getType() {
        return this.type;
    }

    @Override
    public DKBansExecutor getStaff() {
        return DKBans.getInstance().getPlayerManager().getExecutor(this.staffId);
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public long getTimeout() {
        return this.timeout;
    }

    @Override
    public String getForReason() {
        return this.forReason;
    }

    @Override
    public long getForDuration() {
        return this.forDuration;
    }

    @Override
    public PunishmentTemplate getForTemplate() {
        return (PunishmentTemplate) DKBans.getInstance().getTemplateManager().getTemplate(this.forTemplateId);
    }

    public void setId(int id ){
        this.id = id;
    }
}
