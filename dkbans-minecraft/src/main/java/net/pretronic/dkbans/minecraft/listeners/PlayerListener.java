package net.pretronic.dkbans.minecraft.listeners;

import net.md_5.bungee.event.EventHandler;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.common.event.player.MinecraftPlayerChatEvent;
import org.mcnative.common.event.player.login.MinecraftPlayerLoginEvent;
import org.mcnative.common.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.common.text.Text;

public class PlayerListener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(MinecraftPlayerLoginEvent event){
        //@Todo online mode check
        if(event.isCancelled()) return;

        //@Todo check nickname filter

        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        if(player.hasActivePunish(PunishmentType.BAN)){
            PlayerHistoryEntry ban = player.getHistory().getActiveEntry(PunishmentType.BAN);
            event.setCancelled(true);
            event.setCancelReason(null, VariableSet.create()
                    .addDescribed("ban",ban)
                    .addDescribed("player",event.getPlayer()));
            return;
        }

        //@Todo check for new players because of alt manager

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPostLoginLow(MinecraftPlayerPostLoginEvent event){
        if(DKBansConfig.PLAYER_ON_JOIN_CLEAR_CHAT){
            for(int i = 1; i < 120; i++) event.getOnlinePlayer().sendMessage(Text.EMPTY);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)//@Todo async
    public void onPlayerPostLogin(MinecraftPlayerPostLoginEvent event){
        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        //@Todo send team chat info

        //@Todo send report info

        //@Todo start new session
    }

    @EventHandler//@Todo async
    public void onPlayerDisconnect(MinecraftPlayerPostLoginEvent event){
        //@Todo stop session
    }

    @EventHandler(priority = EventPriority.HIGHEST)//@Todo async
    public void onPlayerChat(MinecraftPlayerChatEvent event){
        if(event.isCancelled()) return;
    }

}
