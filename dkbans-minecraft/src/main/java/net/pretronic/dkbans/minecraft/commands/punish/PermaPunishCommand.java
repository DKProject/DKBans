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

package net.pretronic.dkbans.minecraft.commands.punish;

import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntrySnapshot;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.commands.CommandUtil;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;

public class PermaPunishCommand extends BasicCommand {

    private final PunishmentType type;

    public PermaPunishCommand(ObjectOwner owner, CommandConfiguration configuration, PunishmentType type) {
        super(owner, configuration);
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(arguments.length <= 1){
            //@Todo usage
            return;
        }

        MinecraftPlayer player = CommandUtil.getPlayer(sender,arguments[0],true);
        if(player == null) return;

        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);
        if(CommandUtil.checkBypass(sender,dkBansPlayer)) return;


        boolean override = arguments[1].equalsIgnoreCase("--override");
        String reason = CommandUtil.readStringFromArguments(arguments,override ? 2 : 1);

        if(dkBansPlayer.hasActivePunish(type)){
            if(override && CommandUtil.canOverridePunish(sender,dkBansPlayer,type)){
                dkBansPlayer.unpunish(CommandUtil.getExecutor(sender),type,"Overriding punishment with a new one");
            }else{
                String command = getConfiguration().getName()+arguments[0]+" --override "+reason;
                CommandUtil.sendAlreadyPunished(sender,dkBansPlayer,type,command);
            }
            return;
        }

        PlayerHistoryEntrySnapshot result = dkBansPlayer.punish()//@Todo configurable default history type
                .stuff(CommandUtil.getExecutor(sender))
                .punishmentType(type)
                .timeout(-1)
                .reason(reason)
                .execute();
        CommandUtil.sendPunishResultMessage(sender,result);
    }
}
