/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 06.07.20, 19:58
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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PunishmentList;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.GeneralUtil;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.text.components.MessageComponent;

import java.util.List;

public class PunishListCommand extends BasicCommand {

    private final PunishmentType type;
    private final DKBansScope scope;

    public PunishListCommand(ObjectOwner owner, CommandConfiguration configuration, PunishmentType type,DKBansScope scope) {
        super(owner, configuration);
        this.type = type;
        this.scope = scope;
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        int page = 1;
        if(arguments.length > 1 && GeneralUtil.isNaturalNumber(arguments[0])){
            page = Integer.parseInt(arguments[0]);
        }

        PunishmentList punishments = DKBans.getInstance().getHistoryManager().getActivePunishments(type,scope);
        List<PlayerHistoryEntry> result = punishments.getPage(page,12);

        MessageComponent<?> list;
        if(type == PunishmentType.BAN) list = Messages.PUNISH_LIST_BAN;
        else if(type == PunishmentType.MUTE) list = Messages.PUNISH_LIST_MUTE;
        else throw new UnsupportedOperationException("Not listable type");

        sender.sendMessage(list, VariableSet.create()
                .addDescribed("punishments",result)
                .add("page",page)
                .add("previousPage",page > 1 ? page-1 : 0)
                .add("nextPage",page+1));
    }
}
