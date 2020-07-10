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
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import org.mcnative.common.text.Text;
import org.mcnative.common.text.components.MessageComponent;
import org.mcnative.common.text.components.TextComponent;
import org.mcnative.common.text.event.ClickAction;
import org.mcnative.common.text.event.TextEvent;
import org.mcnative.common.utils.ImageMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
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
            components.add(Messages.COMMAND_JOINME_LINE_1);
        }

        if(DKBansConfig.JOINME_HEAD_ENABLED) {
            try{
                BufferedImage image;
                try{
                    image = ImageIO.read(new URL("https://minotar.net/avatar/"+playerId+"/8.png"));
                }catch (Exception exception) {
                    throw new RuntimeException("Could not load joinme image from player "+playerId, exception);
                }
                if(image != null){
                    List<MessageComponent<?>> newComponents = new ArrayList<>();
                    ImageMessage message = new ImageMessage(image,8, '█');
                    int i = -1;
                    for(MessageComponent<?> line : components) {
                        if(i >= 0 && i < 8){
                            TextComponent component = new TextComponent(message.getLines()[i]);
                            component.setClickEvent(new TextEvent<>(ClickAction.RUN_COMMAND, "/joinme "+playerId));
                            newComponents.add(component);
                        }else newComponents.add(line);
                        i++;
                    }
                    return newComponents;
                }
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
        return components;
    }
}
