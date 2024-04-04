package net.bigmangohead.crystalworks.util.energy.flux;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.Set;

@AutoRegisterCapability
public interface IFluxStorage {

    int receiveFlux(int fluxAmount, IFluxType fluxType, boolean simulate);

    int extractFlux(int fluxAmount, IFluxType fluxType, boolean simulate);

    SingleFluxStorage getFluxStorage(IFluxType fluxType);

    int getFluxAmount(IFluxType fluxType);

    Set<IFluxType> getStoredFluxTypes();

    int getMaxEnergyStored();

    boolean canAcceptFluxType(IFluxType fluxType);

    boolean canExtract();

    boolean canReceive();

}
