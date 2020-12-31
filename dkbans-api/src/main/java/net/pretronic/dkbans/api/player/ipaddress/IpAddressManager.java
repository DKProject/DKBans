/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 20.07.20, 21:04
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

package net.pretronic.dkbans.api.player.ipaddress;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.libraries.utility.annonations.Nullable;

import java.net.InetAddress;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface IpAddressManager {

    IpAddressInfo getIpAddressInfo(String ipAddress);

    @Nullable
    IpAddressBlock getIpAddressBlock(String ipAddress);

    boolean isIpAddressBlocked(InetAddress address);

    boolean isIpAddressBlocked(String ipAddress);

    default IpAddressBlock blockIpAddress(String ipAddress, String reason, long timeout, DKBansExecutor staff){
        return blockIpAddress(IpAddressBlockType.BLOCK,ipAddress,reason,timeout,staff, (String) null);
    }

    default IpAddressBlock blockIpAddress(String ipAddress, String reason, Duration duration, DKBansExecutor staff){
        return blockIpAddress(ipAddress,reason,System.currentTimeMillis()+ TimeUnit.MILLISECONDS.toMillis(duration.getSeconds()),staff);
    }

    default IpAddressBlock blockIpAddress(IpAddressBlockType type, String ipAddress, String reason, long timeout, DKBansExecutor staff, String forReason){
        return blockIpAddress(type,ipAddress,reason,timeout,staff,forReason,-1);
    }

    default IpAddressBlock blockIpAddress(IpAddressBlockType type, String ipAddress, String reason, Duration duration, DKBansExecutor staff, String forReason){
        return blockIpAddress(type,ipAddress,reason,System.currentTimeMillis()+ TimeUnit.MILLISECONDS.toMillis(duration.getSeconds()),staff,forReason,-1);
    }

    IpAddressBlock blockIpAddress(IpAddressBlockType type, String ipAddress, String reason, long timeout, DKBansExecutor staff, String forReason, long forDuration);
    default IpAddressBlock blockIpAddress(IpAddressBlockType type, String ipAddress, String reason, Duration duration, DKBansExecutor staff, String forReason, Duration forDuration){
        return blockIpAddress(type,ipAddress,reason,System.currentTimeMillis()+ TimeUnit.MILLISECONDS.toMillis(duration.getSeconds())
                ,staff,forReason,TimeUnit.MILLISECONDS.toMillis(forDuration.getSeconds()));
    }

    default IpAddressBlock blockIpAddress(IpAddressBlockType type,String ipAddress, String reason, Duration duration, DKBansExecutor staff, PunishmentTemplate forTemplate){
        return blockIpAddress(type,ipAddress,reason,System.currentTimeMillis()+ TimeUnit.MILLISECONDS.toMillis(duration.getSeconds()),staff,forTemplate);
    }

    IpAddressBlock blockIpAddress(IpAddressBlockType type,String ipAddress, String reason, long timeout, DKBansExecutor staff, PunishmentTemplate forTemplate);


    void unblockIpAddress(IpAddressBlock addressBlock);
}
