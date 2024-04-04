package net.bigmangohead.crystalworks.util.energy.flux;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

public class FluxStorage implements IFluxStorage, INBTSerializable<Tag> {

    protected HashMap<FluxType, SingleFluxStorage> storedFlux;
    protected Set<FluxType> acceptableFluxTypes;

    public FluxStorage(Set<FluxType> acceptableFluxTypes, @Nullable HashMap<FluxType, SingleFluxStorage> storedFlux) {
        this.storedFlux = storedFlux;
        this.acceptableFluxTypes = acceptableFluxTypes;
        for (FluxType fluxType : acceptableFluxTypes) {
            if (!storedFlux.containsKey(fluxType)) {
                this.storedFlux.put(fluxType, new SingleFluxStorage(fluxType, 0, 0, 0, 0));
            }
        }
    }

    public static class FluxStorageData {
        public int flux;
        public int capacity;
        public int maxReceive;
        public int maxExtract;
        public FluxType fluxType;

    }

    public class test {
        private int i;
        public test(int i) {
            i = 1;
        }


    }

    @Override
    public int receiveFlux(int fluxAmount, IFluxType fluxType, boolean simulate) {
        if (canAcceptFluxType(fluxType)) {

        }
        return 0;
    }

    @Override
    public int extractFlux(int fluxAmount, IFluxType fluxType, boolean simulate) {
        return 0;
    }

    @Override
    public SingleFluxStorage getFluxStorage(IFluxType fluxType) {
        return null;
    }

    @Override
    public int getFluxAmount(IFluxType fluxType) {
        return 0;
    }

    @Override
    public Set<IFluxType> getStoredFluxTypes() {
        return null;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canAcceptFluxType(IFluxType fluxType) {
        return false;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }

    @Override
    public Tag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(Tag tag) {

    }
}
