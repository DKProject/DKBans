/*
 * (C) Copyright 2020 The DKBans Project (Davide Wietlisbach & Philipp Elvin Friedhoff)
 *
 * @author Philipp Elvin Friedhoff
 * @since 10.07.20, 20:16
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

package net.pretronic.dkbans.minecraft.joinme;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.joinme.JoinMe;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.minecraft.config.CommandConfig;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.utility.exception.OperationFailedException;
import net.pretronic.libraries.utility.http.HttpClient;
import net.pretronic.libraries.utility.http.HttpResult;
import org.mcnative.runtime.api.text.ImageText;
import org.mcnative.runtime.api.text.components.MessageComponent;
import org.mcnative.runtime.api.text.components.TextComponent;
import org.mcnative.runtime.api.text.event.ClickAction;
import org.mcnative.runtime.api.text.event.TextEvent;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MinecraftJoinMe implements JoinMe {

    private final UUID playerId;
    private final String server;
    private long timeout;

    public MinecraftJoinMe(DKBansPlayer player, String server, long timeout) {
        this.playerId = player.getUniqueId();
        this.server = server;
        this.timeout = timeout;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public DKBansPlayer getPlayer() {
        return DKBans.getInstance().getPlayerManager().getPlayer(this.playerId);
    }

    @Override
    public String getServer() {
        return server;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public List<MessageComponent<?>> create(){
        List<MessageComponent<?>> components = new ArrayList<>();
        components.add(Messages.COMMAND_JOINME_LINE_1);
        if(DKBansConfig.JOINME_MULTIPLE_LINES){
            components.add(Messages.COMMAND_JOINME_LINE_2);
            components.add(Messages.COMMAND_JOINME_LINE_3);
            components.add(Messages.COMMAND_JOINME_LINE_4);
            components.add(Messages.COMMAND_JOINME_LINE_5);
            components.add(Messages.COMMAND_JOINME_LINE_6);
            components.add(Messages.COMMAND_JOINME_LINE_7);
            components.add(Messages.COMMAND_JOINME_LINE_8);
            components.add(Messages.COMMAND_JOINME_LINE_9);
            components.add(Messages.COMMAND_JOINME_LINE_10);
        }

        if(DKBansConfig.JOINME_HEAD_ENABLED) {
            try{
                BufferedImage image;
                try{
                    HttpClient client = new HttpClient();
                    client.setUrl(DKBansConfig.JOINME_HEAD_SOURCE.replace("{uuid}",playerId.toString()));
                    HttpResult result = client.connect();
                    image = ImageIO.read(result.getInputStream());
                }catch (Exception exception) {
                    throw new OperationFailedException("Could not load joinme image from player "+playerId, exception);
                }
                if(image != null){
                    ImageText message = ImageText.compile(image,8,1, '█');
                    int i = 0;
                    for(MessageComponent<?> line : components) {
                        message.addTextExtension(i,line);
                        i++;
                    }

                    components =  Arrays.asList(message.buildLines());
                }
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }

        List<MessageComponent<?>> result = new ArrayList<>();

        for (MessageComponent<?> component : components) {
            TextComponent root = new TextComponent();
            root.addExtra(component);
            root.setClickEvent(new TextEvent<>(ClickAction.RUN_COMMAND, "/"+CommandConfig.COMMAND_JOINME.getName()+" "+playerId));
            result.add(root);
        }

        return result;
    }
}
