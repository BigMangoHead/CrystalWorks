package net.bigmangohead.crystalworks.util.energy.flux;

import net.minecraftforge.energy.IEnergyStorage;
import org.apache.logging.log4j.util.TriConsumer;

public class RedstoneFluxStorage extends SingleFluxStorage implements IEnergyStorage {

    public RedstoneFluxStorage(int capacity, int maxReceive, int maxExtract, int flux, TriConsumer<FluxType, Integer, Integer> onFluxChange) {
        super(FluxUtils.getFluxType("redstone"), capacity, maxReceive, maxExtract, flux, onFluxChange);
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate) {
        return receiveFlux(amount, simulate);
    }

    @Override
    public int extractEnergy(int amount, boolean simulate) {
        return extractFlux(amount, simulate);
    }

    @Override
    public int getEnergyStored() {
        return getFluxStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return getMaxFluxStored();
    }
}
