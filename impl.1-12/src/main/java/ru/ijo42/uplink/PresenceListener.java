package ru.ijo42.uplink;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import ru.ijo42.uplink.api.PresenceState;

public class PresenceListener extends ru.ijo42.uplink.api.PresenceListener {

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        super.onTick();
    }

    @SubscribeEvent
    public void onMainMenu(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu && presenceManager.getCurState() != PresenceState.MENU_MAIN) {
            super.onMainMenu();
        }
    }

    @SubscribeEvent
    public void onJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayerMP || event.getEntity() instanceof EntityPlayerSP) {
            super.onJoin();
        }  // Ignore non-players.
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        super.onClientDisconnect();
    }
}