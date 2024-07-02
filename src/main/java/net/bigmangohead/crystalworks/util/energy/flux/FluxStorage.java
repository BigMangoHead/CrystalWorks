package net.bigmangohead.crystalworks.util.energy.flux;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

// TODO: Max flux types does not work properly
public class FluxStorage implements INBTSerializable<Tag>, IFluxStorage {

    // Note: I could make the hashmaps/sets here start with an initial capacity
    // so that they don't have to be recreated at any point. I doubt this optimization
    // matters though.
    protected HashMap<FluxType, SingleFluxStorage> storedFlux;
    protected Set<FluxType> acceptableFluxTypes;
    protected Set<FluxType> storedFluxTypes; // Only includes types with more than 0 flux

    // -1 represents no maximum
    protected int maxFluxTypesCount = -1;

    protected boolean containsForgeEnergy = false;
    protected RedstoneFluxStorage forgeEnergyStorage = null;
    protected LazyOptional<IEnergyStorage> optionalForgeEnergyStorage = LazyOptional.of(() -> this.forgeEnergyStorage);

    public FluxStorage(Set<SingleFluxStorage> fluxStorageDataSet) {
        this.storedFlux = new HashMap<>();
        this.storedFluxTypes = new HashSet<>();
        this.acceptableFluxTypes = new HashSet<>();
        for (SingleFluxStorage fluxStorageData : fluxStorageDataSet) {
            this.storedFlux.put(fluxStorageData.fluxType, fluxStorageData);

            this.acceptableFluxTypes.add(fluxStorageData.fluxType);

            if (fluxStorageData.flux > 0) {
                this.storedFluxTypes.add(fluxStorageData.fluxType);
            }
        }
    }

    public FluxStorage(int maxFluxTypesCount, int globalCapacity, int globalMaxReceive, int globalMaxExtract, Set<FluxType> acceptableFluxTypes, TriConsumer<FluxType, Integer, Integer> onFluxChange, Set<SingleFluxStorage> fluxStorageDataSet) {
        this.storedFlux = new HashMap<>();
        this.storedFluxTypes = new HashSet<>();
        this.acceptableFluxTypes = acceptableFluxTypes;
        this.maxFluxTypesCount = maxFluxTypesCount;

        //First register the pre-set flux storages
        for (SingleFluxStorage fluxStorageData : fluxStorageDataSet) {
            this.storedFlux.put(fluxStorageData.fluxType, fluxStorageData);

            this.acceptableFluxTypes.add(fluxStorageData.fluxType);

            if (fluxStorageData.flux > 0) {
                this.storedFluxTypes.add(fluxStorageData.fluxType);
            }
        }

        //Set up data using the global values
        for (FluxType acceptedFluxType : acceptableFluxTypes) {
            if (!storedFlux.containsKey(acceptedFluxType)) {

                SingleFluxStorage newFluxStorage;
                if (acceptedFluxType.getName().equals("redstone")) {
                    RedstoneFluxStorage newStorage = new RedstoneFluxStorage(acceptedFluxType.applyCapacityModifier(globalCapacity),
                            acceptedFluxType.applyMaxReceiveModifier(globalMaxReceive), acceptedFluxType.applyMaxExtractModifier(globalMaxExtract),
                            acceptedFluxType.applyFluxModifier(0), onFluxChange);

                    this.forgeEnergyStorage = newStorage;
                    this.containsForgeEnergy = true;
                    newFluxStorage = newStorage;

                } else {
                    newFluxStorage = new SingleFluxStorage(acceptedFluxType, acceptedFluxType.applyCapacityModifier(globalCapacity),
                            acceptedFluxType.applyMaxReceiveModifier(globalMaxReceive), acceptedFluxType.applyMaxExtractModifier(globalMaxExtract),
                            acceptedFluxType.applyFluxModifier(0), onFluxChange);
                }


                this.storedFlux.put(acceptedFluxType, newFluxStorage);

                if (newFluxStorage.flux > 0) {
                    this.storedFluxTypes.add(newFluxStorage.fluxType);
                }
            }
        }
    }

    public FluxStorage(int maxFluxTypesCount, int globalCapacity, int globalMaxReceive, int globalMaxExtract, Set<FluxType> acceptableFluxTypes, TriConsumer<FluxType, Integer, Integer> onFluxChange) {
        this(maxFluxTypesCount, globalCapacity, globalMaxReceive, globalMaxExtract, acceptableFluxTypes, onFluxChange, Set.of());
    }



    public int receiveFlux(int fluxAmount, FluxType fluxType, boolean simulate) {
        if (canReceive(fluxType)) {
            int insertedFlux = this.storedFlux.get(fluxType).receiveFlux(fluxAmount, simulate);

            if (insertedFlux > 0) {
                this.storedFluxTypes.add(fluxType);
            }

            return insertedFlux;
        }
        return 0;
    }

    public int extractFlux(int fluxAmount, FluxType fluxType, boolean simulate) {
        if (canExtract(fluxType)) {
            int extractedFlux = this.storedFlux.get(fluxType).extractFlux(fluxAmount, simulate);

            if (!simulate && this.storedFlux.get(fluxType).getFluxStored() == 0) {
                this.storedFluxTypes.remove(fluxType);
            }

            return extractedFlux;
        }
        return 0;
    }



    public void forceSetFlux(FluxType fluxType, int fluxAmount) {
        if (canAccept(fluxType)) {
            this.storedFlux.get(fluxType).forceSetFlux(fluxAmount);

            updateStoredFlux(fluxType);
        }
    }

    public void forceAddFlux(FluxType fluxType, int amount) {
        if (canAccept(fluxType)) {
            this.storedFlux.get(fluxType).forceAddFlux(amount);

            updateStoredFlux(fluxType);
        }
    }

    public void forceRemoveFlux(FluxType fluxType, int amount) {
        if (canAccept(fluxType)) {
            this.storedFlux.get(fluxType).forceRemoveFlux(amount);

            updateStoredFlux(fluxType);
        }
    }


    public void addFluxType(FluxType fluxType, int globalCapacity, int globalMaxReceive, int globalMaxExtract, TriConsumer<FluxType, Integer, Integer> onFluxChange, int flux) {
        this.acceptableFluxTypes.add(fluxType);
        SingleFluxStorage newFluxStorage = new SingleFluxStorage(fluxType, fluxType.applyCapacityModifier(globalCapacity),
                fluxType.applyMaxReceiveModifier(globalMaxReceive), fluxType.applyMaxExtractModifier(globalMaxExtract),
                fluxType.applyFluxModifier(flux), onFluxChange);

        this.storedFlux.put(fluxType, newFluxStorage);

        if (newFluxStorage.flux > 0) {
            this.storedFluxTypes.add(newFluxStorage.fluxType);
        }
    }

    public void addFluxType(FluxType fluxType, int globalCapacity, int globalMaxReceive, int globalMaxExtract, TriConsumer<FluxType, Integer, Integer> onFluxChange) {
        this.addFluxType(fluxType, globalCapacity, globalMaxReceive, globalMaxExtract, onFluxChange, 0);
    }

    public void removeFluxType(FluxType fluxType) {
        this.storedFlux.remove(fluxType);
        this.storedFluxTypes.remove(fluxType);
        this.acceptableFluxTypes.remove(fluxType);
    }



    public SingleFluxStorage getFluxStorage(FluxType fluxType) {
        return this.storedFlux.get(fluxType);
    }

    public int getFluxAmount(FluxType fluxType) {
        if (this.storedFlux.containsKey(fluxType)) {
            return this.storedFlux.get(fluxType).getFluxStored();
        } else {
            return 0;
        }
    }

    public int getMaxFluxAmount(FluxType fluxType) {
        if (this.storedFlux.containsKey(fluxType)) {
            return this.storedFlux.get(fluxType).getMaxFluxStored();
        } else {
            return 0;
        }
    }

    public Set<FluxType> getStoredFluxTypes() {
        return storedFluxTypes;
    }

    public Set<FluxType> getAcceptedFluxTypes() {
        return acceptableFluxTypes;
    }

    public RedstoneFluxStorage getForgeEnergyStorage() {
        return forgeEnergyStorage;
    }

    public LazyOptional<IEnergyStorage> getOptionalForgeEnergyStorage() {
        return optionalForgeEnergyStorage;
    }

    public Long getLastTimeFluxChanged(FluxType fluxType) {
        if (storedFlux.containsKey(fluxType)) {
            return storedFlux.get(fluxType).getLastTimeChanged();
        } else {
            return null;
        }
    }



    //Currently, extracting a flux type requires that that flux is accepted.
    public boolean canExtract(FluxType fluxType) {
        if (fluxType == null) {
            CrystalWorksMod.LOGGER.warn("FluxType passed in to canExtract is null!");
            return false;
        }
        return this.storedFluxTypes.contains(fluxType) && this.acceptableFluxTypes.contains(fluxType);
    }

    public boolean canReceive(FluxType fluxType) {
        if (fluxType == null) {
            CrystalWorksMod.LOGGER.warn("FluxType passed in to canReceive is null!");
            return false;
        }
        return acceptableFluxTypes.contains(fluxType) && (this.storedFluxTypes.size() < this.maxFluxTypesCount || this.storedFluxTypes.contains(fluxType));
    }

    public boolean canAccept(FluxType fluxType) {
        if (fluxType == null) {
            CrystalWorksMod.LOGGER.warn("FluxType passed in to canAccept is null!");
            return false;
        }
        return acceptableFluxTypes.contains(fluxType);
    }



    protected void updateStoredFlux(FluxType fluxType) {
        if (fluxType == null || this.storedFlux.get(fluxType) == null) return;
        if (this.storedFlux.get(fluxType).getFluxStored() > 0) {
            this.storedFluxTypes.add(fluxType);
        } else {
            this.storedFluxTypes.remove(fluxType);
        }
    }



    @Override
    public Tag serializeNBT() {
        CompoundTag nbtData = new CompoundTag();
        this.storedFlux.forEach((fluxType, singleFluxStorage) ->
                nbtData.put(fluxType.getName(), singleFluxStorage.serializeNBT()));

        return nbtData;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;
        this.storedFluxTypes = new HashSet<>();

        // Update each key that has an update to run
        for (String name : nbt.getAllKeys()) {
            SingleFluxStorage fluxStorage = this.storedFlux.get(FluxUtils.getFluxType(name));
            fluxStorage.deserializeNBT(nbt.get(name));
            if (fluxStorage.flux > 0) {
                this.storedFluxTypes.add(FluxUtils.getFluxType(name));
            }
        }
    }
}
