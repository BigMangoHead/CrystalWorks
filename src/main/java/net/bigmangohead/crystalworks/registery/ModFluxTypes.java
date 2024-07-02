package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.util.energy.flux.FluxType;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.RegisterEvent;

import java.awt.*;

// TODO: Switch to deferred register
// I originally couldn't figure it out and tried this method, and it worked.
public class ModFluxTypes {

    public static void register(RegisterEvent event) {
        event.register(ModRegistries.fluxTypeKey,
                helper -> {
            helper.register("redstone", new FluxType(1, 1, 1,
                    1, "redstone", new Color(255, 0, 0), new Color(225, 51, 51),
                    Component.translatable("flux.crystalworks.redstone")));

            helper.register("gold", new FluxType(1, 1, 1,
                    1, "gold", new Color(253, 245, 95), new Color(255, 187, 0),
                    Component.translatable("flux.crystalworks.gold")));

            helper.register("diamond", new FluxType(1, 2, 1,
                    1, "diamond", new Color(74, 237, 217), new Color(85, 255, 255),
                    Component.translatable("flux.crystalworks.diamond")));
        });
    }
}
