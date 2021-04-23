/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 21.12.20, 13:12
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

package net.pretronic.dkbans.minecraft;

import net.pretronic.dkbans.api.DKBansScope;
import net.pretronic.dkbans.api.broadcast.Broadcast;
import net.pretronic.dkbans.api.broadcast.BroadcastAssignment;
import net.pretronic.dkbans.api.broadcast.BroadcastGroup;
import net.pretronic.dkbans.api.broadcast.BroadcastProperty;
import net.pretronic.dkbans.common.broadcast.BroadcastSender;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.message.MessageProvider;
import net.pretronic.libraries.message.bml.MessageProcessor;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import net.pretronic.libraries.utility.interfaces.ObjectOwner;
import org.mcnative.runtime.api.McNative;
import org.mcnative.runtime.api.player.ConnectedMinecraftPlayer;
import org.mcnative.runtime.api.player.OnlineMinecraftPlayer;
import org.mcnative.runtime.api.player.Title;
import org.mcnative.runtime.api.player.bossbar.BarColor;
import org.mcnative.runtime.api.player.bossbar.BarDivider;
import org.mcnative.runtime.api.player.bossbar.BarFlag;
import org.mcnative.runtime.api.player.bossbar.BossBar;
import org.mcnative.runtime.api.utils.MinecraftTickConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MinecraftBroadcastSender implements BroadcastSender {

    private final MessageProcessor messageProcessor;
    private final Collection<BossBar> activeBars;

    public MinecraftBroadcastSender() {
        this.messageProcessor = McNative.getInstance().getRegistry().getService(MessageProvider.class).getProcessor();
        this.activeBars = ConcurrentHashMap.newKeySet();
    }

    @Override
    public void sendBroadcast(Broadcast broadcast) {
        Collection<ConnectedMinecraftPlayer> players = McNative.getInstance().getLocal().getConnectedPlayers();
        for (ConnectedMinecraftPlayer player : players) {
            sendMessage(player, broadcast);
        }
    }

    @Override
    public void sendBroadcast(BroadcastAssignment assignment) {
        Collection<ConnectedMinecraftPlayer> players = McNative.getInstance().getLocal().getConnectedPlayers();
        for (ConnectedMinecraftPlayer player : players) {
            if(player.hasPermission(assignment.getGroup().getPermission()) && isOnScope(player,assignment.getGroup().getScope())){
                sendMessage(player, assignment.getBroadcast());
            }
        }
    }

    private boolean isOnScope(ConnectedMinecraftPlayer player,DKBansScope scope){
        if(scope == null || scope.isGlobal()) return true;
        else if(scope.getType().equalsIgnoreCase(DKBansScope.DEFAULT_SERVER)){
            return player.getServer() != null && player.getServer().getName().equalsIgnoreCase(scope.getName());
        }else if(scope.getType().equalsIgnoreCase(DKBansScope.DEFAULT_SERVER_GROUP)){
            return player.getServer() != null && player.getServer().getGroup().equalsIgnoreCase(scope.getName());
        }
        return false;
    }

    private void sendMessage(ConnectedMinecraftPlayer onlinePlayer, Broadcast broadcast) {
        switch (broadcast.getVisibility()) {
            case TITLE: {
                Title title = Title.newTitle();
                title.title(Messages.BROADCAST_TITLE);

                VariableSet variableSet = VariableSet.create()
                        .add("title", messageProcessor.parse(broadcast.getProperties().getString(BroadcastProperty.TEXT)))
                        .addDescribed("player", onlinePlayer);

                title.variables(variableSet);


                if(broadcast.getProperties().contains(BroadcastProperty.SUBTITLE)) {
                    String subTitle = broadcast.getProperties().getString(BroadcastProperty.SUBTITLE);
                    title.subTitle(Messages.BROADCAST_TITLE_SUB);
                    title.getVariables().add(BroadcastProperty.SUBTITLE,
                            messageProcessor.parse(subTitle));
                }

                if(broadcast.getProperties().contains(BroadcastProperty.FADE_IN)) {
                    long fadeIn = MinecraftTickConverter.toTicks(broadcast.getProperties().getInt(BroadcastProperty.FADE_IN), TimeUnit.SECONDS);
                    title.fadeInTime((int) fadeIn);
                }

                if(broadcast.getProperties().contains(BroadcastProperty.FADE_OUT)) {
                    long fadeOut = MinecraftTickConverter.toTicks(broadcast.getProperties().getInt(BroadcastProperty.FADE_OUT), TimeUnit.SECONDS);
                    title.fadeInTime((int) fadeOut);
                }

                if(broadcast.getProperties().contains(BroadcastProperty.STAY)) {
                    long stay = MinecraftTickConverter.toTicks(broadcast.getProperties().getInt(BroadcastProperty.STAY), TimeUnit.SECONDS);
                    title.stayTime((int) stay);
                }
                onlinePlayer.sendTitle(title);
                return;
            }
            case ACTIONBAR: {
                long stayTime = 5;
                if(broadcast.getProperties().contains(BroadcastProperty.STAY)) {
                    stayTime = broadcast.getProperties().getLong(BroadcastProperty.STAY);
                }
                onlinePlayer.sendActionbar(Messages.BROADCAST_ACTIONBAR, buildVariableSet(broadcast, onlinePlayer), stayTime);
                return;
            }
            case BOSSBAR: {
                BossBar bossBar = BossBar.newBossBar();
                bossBar.setProgress(100);
                bossBar.setMaximum(100);
                bossBar.setDivider(BarDivider.SOLID);
                bossBar.setFlag(BarFlag.CREATE_FOG);
                bossBar.setTitle(Messages.BROADCAST_BOSSBAR);

                BarColor color = BarColor.WHITE;
                try {
                    color = BarColor.valueOf(broadcast.getProperties().getString(BroadcastProperty.BAR_COLOR).toUpperCase());
                }catch (Exception ignored){}

                bossBar.setColor(color);
                bossBar.setVariables(buildVariableSet(broadcast,onlinePlayer));
                activeBars.add(bossBar);
                onlinePlayer.addBossBar(bossBar);

                long stayTime = 5;
                if(broadcast.getProperties().contains(BroadcastProperty.STAY)) {
                    stayTime = broadcast.getProperties().getLong(BroadcastProperty.STAY);
                }

                McNative.getInstance().getScheduler().createTask(ObjectOwner.SYSTEM).delay(stayTime,TimeUnit.SECONDS).execute(() -> {
                    List<ConnectedMinecraftPlayer> players = new ArrayList<>(bossBar.getReceivers());
                    for (ConnectedMinecraftPlayer player : players) {
                        player.removeBossBar(bossBar);
                    }
                });
                return;
            }
            case CHAT: {
                onlinePlayer.sendMessage(Messages.BROADCAST_CHAT, buildVariableSet(broadcast, onlinePlayer));
            }
        }

    }

    private VariableSet buildVariableSet(Broadcast broadcast, OnlineMinecraftPlayer onlinePlayer) {
        return VariableSet.create()
                .add("message", messageProcessor.parse(broadcast.getProperties().getString(BroadcastProperty.TEXT)))
                .addDescribed("player", onlinePlayer);
    }
}
