package net.pretronic.dkbans.api.player.chatlog;

import java.util.UUID;

public interface ChatLogManager {

    ServerChatLog getServerChatLog(String serverName);

    ServerChatLog getServerChatLog(UUID uniqueId);

    PlayerChatLog getPlayerChatLog(UUID uniqueId);

}
