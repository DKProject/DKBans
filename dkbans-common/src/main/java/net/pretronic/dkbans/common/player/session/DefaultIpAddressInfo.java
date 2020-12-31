/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 24.12.20, 09:48
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

package net.pretronic.dkbans.common.player.session;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressInfo;

import java.net.InetAddress;

public class DefaultIpAddressInfo implements IpAddressInfo {

    private final InetAddress address;
    private final String country;
    private final String region;

    public DefaultIpAddressInfo(InetAddress address, String country, String region) {
        this.address = address;
        this.country = country;
        this.region = region;
    }

    @Override
    public InetAddress getAddress() {
        return address;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public boolean isBlocked() {
        return DKBans.getInstance().getIpAddressManager().isIpAddressBlocked(address);
    }
}
