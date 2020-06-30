/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Davide Wietlisbach
 * @since 30.06.20, 17:45
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

package net.pretronic.dkbans.minecraft.commands.history;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.CalculationType;
import net.pretronic.dkbans.api.player.history.PlayerHistoryType;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.command.command.BasicCommand;
import net.pretronic.libraries.command.command.configuration.CommandConfiguration;
import net.pretronic.libraries.command.sender.CommandSender;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.common.player.MinecraftPlayer;
import org.mcnative.common.player.OnlineMinecraftPlayer;

import java.util.ArrayList;
import java.util.Collection;

public class MyHistoryPointsCommand extends BasicCommand {

    public MyHistoryPointsCommand(ObjectOwner owner, CommandConfiguration configuration) {
        super(owner, configuration);
    }

    @Override
    public void execute(CommandSender sender, String[] arguments) {
        if(!(sender instanceof MinecraftPlayer)){
            sender.sendMessage(Messages.ERROR_ONLY_PLAYER);
            return;
        }
        OnlineMinecraftPlayer player = (OnlineMinecraftPlayer) sender;
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

        Collection<ResultEntry> points = new ArrayList<>();
        for (PlayerHistoryType type : DKBans.getInstance().getHistoryManager().getHistoryTypes()) {
            int result = dkBansPlayer.getHistory().calculate(CalculationType.POINTS,type);
            points.add(new ResultEntry(type,result));
        }

        sender.sendMessage(Messages.COMMAND_MY_HISTORY_POINTS, VariableSet.create()
                .addDescribed("points",points));
    }

    private static class ResultEntry {

        private final PlayerHistoryType type;
        private final int points;

        public ResultEntry(PlayerHistoryType type, int points) {
            this.type = type;
            this.points = points;
        }

        public PlayerHistoryType getType() {
            return type;
        }

        public int getPoints() {
            return points;
        }
    }
}
