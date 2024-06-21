package net.bigmangohead.crystalworks.event;

import net.bigmangohead.crystalworks.menu.abstraction.ICWBlockEntityMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEvents {
    // This should NOT be referenced for things that MUST occur every certain amount of ticks,
    // just things that generally should be spaced apart based on the tick.
    // Note that this will only be consistent for intervals divisible by
    // 2^5 * 3^2 * 5^5 * 7 * 11 * 13
    public static int tickstandard;
    // This one will be consistent for any power of two amount of ticks up to 2^30.
    public static int tick;

    @SubscribeEvent
    public static void onMenuOpened(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof ICWBlockEntityMenu menu) {
            menu.getBlockEntity().playerOpenedMenu((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onMenuClosed(PlayerContainerEvent.Close event) {
        if (event.getContainer() instanceof ICWBlockEntityMenu menu) {
            menu.getBlockEntity().playerClosedMenu((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        tick += 1;
        if (tick > 1073741824) {
            tick -= 1073741824;
        }

        tickstandard += 1;
        if (tickstandard > 900900000) {
            tickstandard -= 900900000;
        }
    }

}
