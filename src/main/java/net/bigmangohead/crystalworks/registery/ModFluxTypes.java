package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.util.energy.flux.FluxType;
import net.minecraftforge.registries.RegisterEvent;

// TODO: Switch to deferred register
// I originally couldn't figure it out and tried this method, and it worked.
public class ModFluxTypes {

    public static void register(RegisterEvent event) {
        event.register(ModRegistries.fluxTypeKey,
                helper -> {
            helper.register("redstone", new FluxType(1, 1, 1, 1, "redstone"));
            helper.register("gold", new FluxType(1, 1, 1, 1, "gold"));
            helper.register("diamond", new FluxType(1, 2, 1, 1, "diamond"));
        });
    }
}
