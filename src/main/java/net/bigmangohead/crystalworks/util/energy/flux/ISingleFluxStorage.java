package net.bigmangohead.crystalworks.util.energy.flux;

public interface ISingleFluxStorage {

    int receiveFlux(int maxReceive, boolean simulate);

    int extractFlux(int maxExtract, boolean simulate);


    // See IFluxStorage note on using force commands
    void forceSetFlux(int amount);

    void forceAddFlux(int amount);

    void forceRemoveFlux(int amount);


    FluxType getFluxType();

    int getFluxStored();

    int getMaxFluxStored();

    boolean canExtract();

    boolean canReceive();
}
