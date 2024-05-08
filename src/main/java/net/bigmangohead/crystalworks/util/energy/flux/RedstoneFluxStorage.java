package net.bigmangohead.crystalworks.util.energy.flux;

import net.bigmangohead.crystalworks.registery.ModRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.energy.IEnergyStorage;

public class RedstoneFluxStorage extends SingleFluxStorage implements IEnergyStorage {

    public RedstoneFluxStorage(int capacity, int maxReceive, int maxExtract, int flux) {
        super(FluxUtils.getFluxType("redstone"), capacity, maxReceive, maxExtract, flux);
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
