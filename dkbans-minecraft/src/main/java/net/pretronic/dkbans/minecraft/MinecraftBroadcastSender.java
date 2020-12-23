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

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.broadcast.Broadcast;
import net.pretronic.dkbans.api.broadcast.BroadcastAssignment;
import net.pretronic.dkbans.api.broadcast.BroadcastProperty;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.common.broadcast.BroadcastSender;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.message.MessageProvider;
import net.pretronic.libraries.message.bml.MessageProcessor;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.common.McNative;
import org.mcnative.common.player.OnlineMinecraftPlayer;
import org.mcnative.common.player.Title;
import org.mcnative.common.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

public class MinecraftBroadcastSender implements BroadcastSender {

    private final MessageProcessor messageProcessor;

    public MinecraftBroadcastSender() {
        this.messageProcessor = McNative.getInstance().getRegistry().getService(MessageProvider.class)
                .getProcessor();
    }

    @Override
    public Collection<DKBansPlayer> sendBroadcast(Broadcast broadcast) {
        return sendBroadcast(onlinePlayer -> {
            sendMessage(onlinePlayer, broadcast);
            return true;
        });
    }

    @Override
    public Collection<DKBansPlayer> sendBroadcast(BroadcastAssignment broadcastAssignment) {
        return sendBroadcast(onlinePlayer -> {
            if(onlinePlayer.hasPermission(broadcastAssignment.getGroup().getPermission())) {
                sendMessage(onlinePlayer, broadcastAssignment.getBroadcast());
                return true;
            }
            return false;
        });
    }

    private void sendMessage(OnlineMinecraftPlayer onlinePlayer, Broadcast broadcast) {
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
                    int fadeIn = broadcast.getProperties().getInt(BroadcastProperty.FADE_IN);
                    title.fadeInTime(fadeIn);
                }

                if(broadcast.getProperties().contains(BroadcastProperty.FADE_OUT)) {
                    int fadeOut = broadcast.getProperties().getInt(BroadcastProperty.FADE_OUT);
                    title.fadeInTime(fadeOut);
                }

                if(broadcast.getProperties().contains(BroadcastProperty.STAY)) {
                    int stay = broadcast.getProperties().getInt(BroadcastProperty.STAY);
                    title.stayTime(stay);
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
                DKBans.getInstance().getLogger().warn("Bossbar broadcast is not implemented at the moment. Chat broadcast is used as fallback.");
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

    private Collection<DKBansPlayer> sendBroadcast(Predicate<OnlineMinecraftPlayer> playerConsumer) {
        Collection<DKBansPlayer> sent = new ArrayList<>();

        for (OnlineMinecraftPlayer onlinePlayer : McNative.getInstance().getLocal().getOnlinePlayers()) {
            if(playerConsumer.test(onlinePlayer)){
                sent.add(onlinePlayer.getAs(DKBansPlayer.class));
            }
        }

        return sent;
    }
}
