package net.bigmangohead.crystalworks.util.energy.flux;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface IFluxStorage {

    int receiveFlux(int fluxAmount, FluxType fluxType, boolean simulate);

    int extractFlux(int fluxAmount, FluxType fluxType, boolean simulate);


    // NOTE: These force commands are intended to only be used within
    // a single machine (for example, for adding energy to a generator)
    // You should only use these when interacting with other mods if
    // you really need to (for example, if you're making a mod that has
    // an item that clears out the flux in a machine)
    void forceSetFlux(FluxType fluxType, int fluxAmount);

    void forceAddFlux(FluxType fluxType, int amount);

    void forceRemoveFlux(FluxType fluxType, int amount);


    // Adds/removes a flux type from the accepted flux types
    void addFluxType(FluxType fluxType, int globalCapacity, int globalMaxReceive, int globalMaxExtract, TriConsumer<FluxType, Integer, Integer> onFluxChange, int flux);

    void removeFluxType(FluxType fluxType);


    ISingleFluxStorage getFluxStorage(FluxType fluxType);

    int getFluxAmount(FluxType fluxType);

    int getMaxFluxAmount(FluxType fluxType);

    // Should return flux types with NONZERO flux in them
    Set<FluxType> getStoredFluxTypes();

    Set<FluxType> getAcceptedFluxTypes();

    @Nullable
    IEnergyStorage getForgeEnergyStorage();

    LazyOptional<IEnergyStorage> getOptionalForgeEnergyStorage();

    Long getLastTimeFluxChanged(FluxType fluxType);


    boolean canExtract(FluxType fluxType);

    boolean canReceive(FluxType fluxType);

    boolean canAccept(FluxType fluxType);


}
