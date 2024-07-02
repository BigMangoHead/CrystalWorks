package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.util.energy.flux.IFluxStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModCapabilities {
    public static final Capability<IFluxStorage> FLUX = CapabilityManager.get(new CapabilityToken<IFluxStorage>() {
    });

    public static void register(IEventBus eventBus) {
        eventBus.register(IFluxStorage.class);
    }
}
