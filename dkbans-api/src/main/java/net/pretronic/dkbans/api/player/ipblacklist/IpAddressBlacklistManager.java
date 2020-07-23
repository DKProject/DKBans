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

package net.pretronic.dkbans.api.player.ipblacklist;

import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.libraries.utility.annonations.Nullable;

public interface IpAddressBlacklistManager {

    @Nullable
    IpAddressBlock getIpAddressBlock(String ipAddress);

    IpAddressBlock blockIpAddress(String ipAddress, IpAddressBlockType type, DKBansExecutor staff, String reason, long timeout, String forReason, long forDuration);

    IpAddressBlock blockIpAddress(String ipAddress, IpAddressBlockType type, DKBansExecutor staff, String reason, long timeout, PunishmentTemplate forTemplate);


    void unblockIpAddress(IpAddressBlock addressBlock);
}