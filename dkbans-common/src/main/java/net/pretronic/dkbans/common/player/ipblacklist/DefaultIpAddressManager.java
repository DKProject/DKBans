/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 23.07.20, 18:27
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

import net.pretronic.databasequery.api.query.SearchOrder;
import net.pretronic.databasequery.api.query.result.QueryResult;
import net.pretronic.databasequery.api.query.result.QueryResultEntry;
import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansExecutor;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressInfo;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressManager;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlock;
import net.pretronic.dkbans.api.player.ipaddress.IpAddressBlockType;
import net.pretronic.dkbans.api.template.punishment.PunishmentTemplate;
import net.pretronic.dkbans.common.DefaultDKBans;
import net.pretronic.dkbans.common.player.session.DefaultIpAddressInfo;
import net.pretronic.libraries.caching.ArrayCache;
import net.pretronic.libraries.caching.Cache;
import net.pretronic.libraries.caching.CacheQuery;
import net.pretronic.libraries.utility.Validate;
import net.pretronic.libraries.utility.exception.OperationFailedException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class DefaultIpAddressManager implements IpAddressManager {

    private final Cache<IpAddressBlock> ipAddressBlockCache;

    public DefaultIpAddressManager() {
        this.ipAddressBlockCache = new ArrayCache<>();
        this.ipAddressBlockCache.setExpireAfterAccess(10, TimeUnit.MINUTES)
                .setMaxSize(100)
                .registerQuery("ipAddress", new IpAddressQuery());
    }

    @Override
    public IpAddressInfo getIpAddressInfo(String ipAddress) {
        QueryResult result = DefaultDKBans.getInstance().getStorage().getPlayerSessions().find().where("ipAddress",ipAddress)
                .limit(0).orderBy("ConnectTime", SearchOrder.DESC).execute();
        if(result.isEmpty()){
            try {
                return new DefaultIpAddressInfo(InetAddress.getByName(ipAddress),"Unknown","Unknown");
            } catch (UnknownHostException e) {
                throw new OperationFailedException(e);
            }
        }
        QueryResultEntry entry = result.first();
        return new DefaultIpAddressInfo(entry.getObject("ipAddress",InetAddress.class)
                ,entry.getString("Country"),entry.getString("Region"));
    }

    @Override
    public IpAddressBlock getIpAddressBlock(String ipAddress) {
        IpAddressBlock ipAddressBlock = this.ipAddressBlockCache.get("ipAddress", ipAddress);
        if(ipAddressBlock != null) {
            if(System.currentTimeMillis() > ipAddressBlock.getTimeout()) {
                unblockIpAddress(ipAddressBlock);
                return null;
            } else {
                return ipAddressBlock;
            }
        }
        return null;
    }

    @Override
    public boolean isIpAddressBlocked(InetAddress address) {
        return getIpAddressBlock(address.getHostAddress()) != null;
    }

    @Override
    public boolean isIpAddressBlocked(String ipAddress) {
        return getIpAddressBlock(ipAddress) != null;
    }

    @Override
    public IpAddressBlock blockIpAddress(String ipAddress, IpAddressBlockType type, DKBansExecutor staff, String reason, long timeout, String forReason, long forDuration) {
        IpAddressBlock addressBlock = DKBans.getInstance().getStorage().blockIpAddress(ipAddress, type, staff, reason, timeout, forReason, forDuration);
        this.ipAddressBlockCache.insert(addressBlock);
        return addressBlock;
    }

    @Override
    public IpAddressBlock blockIpAddress(String ipAddress, IpAddressBlockType type, DKBansExecutor staff, String reason, long timeout, PunishmentTemplate forTemplate) {
        IpAddressBlock addressBlock = DKBans.getInstance().getStorage().blockIpAddress(ipAddress, type, staff, reason, timeout, forTemplate);
        this.ipAddressBlockCache.insert(addressBlock);
        return addressBlock;
    }


    @Override
    public void unblockIpAddress(IpAddressBlock addressBlock) {
        Validate.notNull(addressBlock);
        DKBans.getInstance().getStorage().unblockIpAddress(addressBlock);
        this.ipAddressBlockCache.remove(addressBlock);
    }

    private static class IpAddressQuery implements CacheQuery<IpAddressBlock> {

        @Override
        public boolean check(IpAddressBlock ipAddressBlock, Object[] identifiers) {
            return ipAddressBlock.getAddress().equals(identifiers[0]);
        }

        @Override
        public void validate(Object[] identifiers) {
            Validate.isTrue(identifiers.length == 1 && identifiers[0] instanceof String);
        }

        @Override
        public IpAddressBlock load(Object[] identifiers) {
            return DKBans.getInstance().getStorage().getIpAddressBlock((String) identifiers[0]);
        }
    }
}
