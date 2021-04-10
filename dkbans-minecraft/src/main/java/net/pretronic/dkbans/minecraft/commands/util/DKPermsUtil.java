/*
 * (C) Copyright 2021 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 01.03.21, 20:22
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

package net.pretronic.dkbans.minecraft.commands.util;

import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.dkperms.api.object.PermissionObject;
import net.pretronic.dkperms.api.object.meta.ObjectMetaEntry;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.duration.DurationProcessor;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;

import java.time.Duration;

public class DKPermsUtil {

    public static boolean checkMaximumAllowedDuration(OnlineMinecraftPlayer sender, PunishmentType type, Duration duration){
        PermissionObject object = sender.getAs(PermissionObject.class);
        ObjectMetaEntry entry = object.getCurrentSnapshot().getMeta(type.getName()+"_maxDuration");
        if(entry == null) return false;
        else{
            Duration max = DurationProcessor.getStandard().parse(entry.getValue());
            boolean result = duration.getSeconds() > max.getSeconds();
            if(result) sender.sendMessage(Messages.ERROR_PUNISHMENT_TO_LONG, VariableSet.create()
                    .addDescribed("type",type)
                    .add("prefix",Messages.PREFIX)
                    .add("duration",entry.getValue()));
            return result;
        }
    }


}
