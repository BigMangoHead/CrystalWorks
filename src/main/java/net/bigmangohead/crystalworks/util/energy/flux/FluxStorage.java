package net.bigmangohead.crystalworks.util.energy.flux;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FluxStorage implements IFluxStorage, INBTSerializable<Tag> {

    protected HashMap<IFluxType, SingleFluxStorage> storedFlux;
    protected Set<IFluxType> acceptableFluxTypes;
    protected Set<IFluxType> storedFluxTypes;

    public FluxStorage(Set<FluxStorageData> fluxStorageDataSet) {
        this.storedFlux = new HashMap<>();
        this.storedFluxTypes = new HashSet<>();
        for (FluxStorageData fluxStorageData : fluxStorageDataSet) {
            this.storedFlux.put(fluxStorageData.fluxType, new SingleFluxStorage(fluxStorageData.fluxType,
                    fluxStorageData.flux, fluxStorageData.capacity, fluxStorageData.maxReceive, fluxStorageData.maxExtract));

            this.acceptableFluxTypes.add(fluxStorageData.fluxType);

            if (fluxStorageData.flux > 0) {
                this.storedFluxTypes.add(fluxStorageData.fluxType);
            }
        }
    }

    public static class FluxStorageData {
        public int flux;
        public int capacity;
        public int maxReceive;
        public int maxExtract;
        public IFluxType fluxType;

        public FluxStorageData(IFluxType fluxType, int capacity, int maxReceive, int maxExtract) {
            this.capacity = capacity;
            this.maxReceive = maxReceive;
            this.maxExtract = maxExtract;
            this.fluxType = fluxType;
            this.flux = 0;
        }

        public FluxStorageData(IFluxType fluxType, int capacity, int maxReceive, int maxExtract, int currentFlux) {
            this.capacity = capacity;
            this.maxReceive = maxReceive;
            this.maxExtract = maxExtract;
            this.fluxType = fluxType;
            this.flux = currentFlux;
        }
    }

    @Override
    public int receiveFlux(int fluxAmount, IFluxType fluxType, boolean simulate) {
        if (canAcceptFluxType(fluxType)) {
            int insertedFlux = this.storedFlux.get(fluxType).receiveFlux(fluxAmount, simulate);

            if (insertedFlux > 0) {
                this.storedFluxTypes.add(fluxType);
            }

            return insertedFlux;
        }
        return 0;
    }

    @Override
    public int extractFlux(int fluxAmount, IFluxType fluxType, boolean simulate) {
        if (canAcceptFluxType(fluxType) && this.storedFluxTypes.contains(fluxType)) {
            int extractedFlux = this.storedFlux.get(fluxType).extractFlux(fluxAmount, simulate);

            if (!simulate && this.storedFlux.get(fluxType).getFluxStored() == 0) {
                this.storedFluxTypes.remove(fluxType);
            }

            return extractedFlux;
        }
        return 0;
    }

    @Override
    public SingleFluxStorage getFluxStorage(IFluxType fluxType) {
        return this.storedFlux.get(fluxType);
    }

    @Override
    public int getFluxAmount(IFluxType fluxType) {
        return this.storedFlux.get(fluxType).getFluxStored();
    }

    @Override
    public Set<IFluxType> getStoredFluxTypes() {
        return storedFluxTypes;
    }

    @Override
    public Set<IFluxType> getAcceptedFluxTypes() {
        return acceptableFluxTypes;
    }

    @Override
    public boolean canAcceptFluxType(IFluxType fluxType) {
        return acceptableFluxTypes.contains(fluxType);
    }

    @Override
    public boolean canExtract(IFluxType fluxType) {
        return false;
    }

    @Override
    public boolean canReceive(IFluxType fluxType) {
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
