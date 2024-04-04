package net.bigmangohead.crystalworks.util.energy.flux;

public interface ISingleFluxStorage {

    int receiveFlux(int energy, boolean simulate);

    int extractFlux(int energy, boolean simulate);

    int getFluxStored();

    int getMaxFluxStored();

    boolean canExtract();

    boolean canReceive();

}
