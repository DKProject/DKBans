package net.pretronic.dkbans.minecraft.listeners;

import net.pretronic.dkbans.api.DKBans;
import net.pretronic.dkbans.api.filter.FilterAffiliationArea;
import net.pretronic.dkbans.api.filter.FilterManager;
import net.pretronic.dkbans.api.player.DKBansPlayer;
import net.pretronic.dkbans.api.player.history.PlayerHistoryEntry;
import net.pretronic.dkbans.api.player.history.PunishmentType;
import net.pretronic.dkbans.minecraft.config.DKBansConfig;
import net.pretronic.dkbans.minecraft.config.Messages;
import net.pretronic.libraries.event.EventPriority;
import net.pretronic.libraries.event.Listener;
import net.pretronic.libraries.message.bml.variable.VariableSet;
import org.mcnative.common.event.player.MinecraftPlayerChatEvent;
import org.mcnative.common.event.player.MinecraftPlayerCommandPreprocessEvent;
import org.mcnative.common.event.player.login.MinecraftPlayerLoginEvent;
import org.mcnative.common.event.player.login.MinecraftPlayerPostLoginEvent;
import org.mcnative.common.player.OnlineMinecraftPlayer;
import org.mcnative.common.text.Text;
import org.mcnative.common.text.components.MessageComponent;

public class PlayerListener {

    @Listener(priority = EventPriority.HIGH)
    public void onPlayerLogin(MinecraftPlayerLoginEvent event){
        //@Todo online mode check
        if(event.isCancelled()) return;

        /*if(!DKBans.getInstance().getFilterManager().checkFilter(FilterAffiliationArea.PLAYER_NAME,event.getPlayer().getName())){
            event.setCancelled(true);
            event.setCancelReason(Messages.FILTER_BLOCKED_NAME,VariableSet.create()
                    .add("name",event.getPlayer().getName()));
            return;
        }*/

        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        if(player.hasActivePunish(PunishmentType.BAN)){
            PlayerHistoryEntry ban = player.getHistory().getActiveEntry(PunishmentType.BAN);
            event.setCancelled(true);
            MessageComponent<?> message = ban.getCurrent().getTimeout() == -1
                    ? Messages.PUNISH_BAN_SCREEN_PERMANENTLY : Messages.PUNISH_BAN_SCREEN_TEMPORARY;
            event.setCancelReason(message
                    ,VariableSet.create()
                    .addDescribed("ban",ban)
                    .addDescribed("player",event.getPlayer()));
            return;
        }

        //@Todo check for new players because of alt manager

    }

    @Listener(priority = EventPriority.LOWEST)
    public void onPlayerPostLoginLow(MinecraftPlayerPostLoginEvent event){
        if(DKBansConfig.PLAYER_ON_JOIN_CLEAR_CHAT){
          //  for(int i = 1; i < 120; i++) event.getOnlinePlayer().sendMessage(Text.EMPTY);
        }
    }

    @Listener(priority = EventPriority.HIGHEST)//@Todo async
    public void onPlayerPostLogin(MinecraftPlayerPostLoginEvent event){
        OnlineMinecraftPlayer player = event.getOnlinePlayer();
        DKBansPlayer dkBansPlayer = player.getAs(DKBansPlayer.class);

        //@Todo send info message

        if(DKBansConfig.PLAYER_ON_JOIN_INFO_TEAMCHAT){
            boolean teamChat = event.getPlayer().hasSetting("DKBans","TeamChatLogin",true);

        }

        if(DKBansConfig.PLAYER_ON_JOIN_INFO_REPORT){
            boolean report = event.getPlayer().hasSetting("DKBans","ReportLogin",true);

            //@Todo send amount open reports
        }

        if(DKBansConfig.PLAYER_SESSION_LOGGING){
           // dkBansPlayer.startSession(player.getName(),player.getAddress().getAddress());
        }
    }

    @Listener//@Todo async
    public void onPlayerDisconnect(MinecraftPlayerPostLoginEvent event){
        event.getPlayer().getAs(DKBansPlayer.class).finishSession();
    }

    @Listener(priority = EventPriority.HIGHEST)//@Todo async
    public void onPlayerChat(MinecraftPlayerChatEvent event){
        if(event.isCancelled()) return;
        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        if(player.hasActivePunish(PunishmentType.MUTE)){
            //@Todo send mute message
            event.setCancelled(true);
            return;
        }
    }

    @Listener(priority = EventPriority.HIGHEST)//@Todo async
    public void onPlayerCommand(MinecraftPlayerCommandPreprocessEvent event){
        if(event.isCancelled()) return;
        FilterManager filterManager = DKBans.getInstance().getFilterManager();

        if(filterManager.checkFilter(FilterAffiliationArea.COMMAND,event.getCommand())){
            //@Todo send block/help message
            event.setCancelled(true);
            return;
        }

        DKBansPlayer player = event.getPlayer().getAs(DKBansPlayer.class);

        if(player.hasActivePunish(PunishmentType.MUTE)){
            if(filterManager.checkFilter(FilterAffiliationArea.COMMAND_MUTE,event.getCommand())){
                //@Todo send mute message
                event.setCancelled(true);
                return;
            }
        }
    }

}
