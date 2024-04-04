package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.util.energy.flux.IFluxStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class ModCapabilities {
    public static final Capability<IFluxStorage> FLUX = CapabilityManager.get(new CapabilityToken<IFluxStorage>() {
    });

    public ModCapabilities() {

    }

}
