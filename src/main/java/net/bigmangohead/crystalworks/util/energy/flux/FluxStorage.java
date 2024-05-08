package net.bigmangohead.crystalworks.util.energy.flux;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.registery.ModFluxTypes;
import net.bigmangohead.crystalworks.util.energy.CustomEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class FluxStorage implements INBTSerializable<Tag> {

    protected HashMap<FluxType, SingleFluxStorage> storedFlux;
    protected Set<FluxType> acceptableFluxTypes;
    protected Set<FluxType> storedFluxTypes; // Only includes types with more than 0 flux

    // -1 represents no maximum
    protected int maxFluxTypesCount = -1;

    protected boolean containsForgeEnergy = false;
    protected RedstoneFluxStorage forgeEnergyStorage = null;
    protected LazyOptional<RedstoneFluxStorage> optionalForgeEnergyStorage = LazyOptional.of(() -> this.forgeEnergyStorage);

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

    public FluxStorage(int maxFluxTypesCount, int globalCapacity, int globalMaxReceive, int globalMaxExtract, Set<FluxType> acceptableFluxTypes, Set<SingleFluxStorage> fluxStorageDataSet) {
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
                            acceptedFluxType.applyFluxModifier(0));

                    this.forgeEnergyStorage = newStorage;
                    this.containsForgeEnergy = true;
                    newFluxStorage = newStorage;

                } else {
                    newFluxStorage = new SingleFluxStorage(acceptedFluxType, acceptedFluxType.applyCapacityModifier(globalCapacity),
                            acceptedFluxType.applyMaxReceiveModifier(globalMaxReceive), acceptedFluxType.applyMaxExtractModifier(globalMaxExtract),
                            acceptedFluxType.applyFluxModifier(0));
                }


                this.storedFlux.put(acceptedFluxType, newFluxStorage);

                if (newFluxStorage.flux > 0) {
                    this.storedFluxTypes.add(newFluxStorage.fluxType);
                }
            }
        }
    }

    public FluxStorage(int maxFluxTypesCount, int globalCapacity, int globalMaxReceive, int globalMaxExtract, Set<FluxType> acceptableFluxTypes) {
        this(maxFluxTypesCount, globalCapacity, globalMaxReceive, globalMaxExtract, acceptableFluxTypes, Set.of());
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


    // TODO: Clean up these force set commands, a lot of repeated code here.
    public void forceSetFlux(FluxType fluxType, int fluxAmount) {
        if (fluxType == null) {
            CrystalWorksMod.LOGGER.warn("FluxType passed in to forceSetFlux is null!");
        } else if (this.acceptableFluxTypes.contains(fluxType)) {
            this.storedFlux.get(fluxType).forceSetFlux(fluxAmount);

            if (this.storedFlux.get(fluxType).getFluxStored() > 0) {
                this.storedFluxTypes.add(fluxType);
            } else {
                this.storedFluxTypes.remove(fluxType);
            }
        }
    }

    public void forceAddFlux(FluxType fluxType, int amount) {
        if (fluxType == null) {
            CrystalWorksMod.LOGGER.warn("FluxType passed in it to forceAddFlux is null!!");
        } else if (this.acceptableFluxTypes.contains(fluxType)) {
            this.storedFlux.get(fluxType).forceAddFlux(amount);

            if (this.storedFlux.get(fluxType).getFluxStored() > 0) {
                this.storedFluxTypes.add(fluxType);
            } else {
                this.storedFluxTypes.remove(fluxType);
            }
        }
    }

    public void forceRemoveFlux(FluxType fluxType, int amount) {
        if (fluxType == null) {
            CrystalWorksMod.LOGGER.warn("FluxType passed in to forceRemoveFlux is null!");
        } else if (this.acceptableFluxTypes.contains(fluxType)) {
            this.storedFlux.get(fluxType).forceRemoveFlux(amount);

            if (this.storedFlux.get(fluxType).getFluxStored() > 0) {
                this.storedFluxTypes.add(fluxType);
            } else {
                this.storedFluxTypes.remove(fluxType);
            }
        }
    }



    public void addFluxType(FluxType fluxType, int globalCapacity, int globalMaxReceive, int globalMaxExtract, int flux) {
        this.acceptableFluxTypes.add(fluxType);
        SingleFluxStorage newFluxStorage = new SingleFluxStorage(fluxType, fluxType.applyCapacityModifier(globalCapacity),
                fluxType.applyMaxReceiveModifier(globalMaxReceive), fluxType.applyMaxExtractModifier(globalMaxExtract),
                fluxType.applyFluxModifier(flux));

        this.storedFlux.put(fluxType, newFluxStorage);

        if (newFluxStorage.flux > 0) {
            this.storedFluxTypes.add(newFluxStorage.fluxType);
        }
    }

    public void addFluxType(FluxType fluxType, int globalCapacity, int globalMaxReceive, int globalMaxExtract) {
        this.addFluxType(fluxType, globalCapacity, globalMaxReceive, globalMaxExtract, 0);
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
        return this.storedFlux.get(fluxType).getFluxStored();
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

    public LazyOptional<RedstoneFluxStorage> getOptionalForgeEnergyStorage() {
        return optionalForgeEnergyStorage;
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

    @Override
    public Tag serializeNBT() {
        CompoundTag nbtData = new CompoundTag();
        this.storedFlux.forEach(((fluxType, singleFluxStorage) ->
                nbtData.put(fluxType.getName(), singleFluxStorage.serializeNBT())));

        return nbtData;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        CompoundTag nbt = (CompoundTag) tag;

        //Use FluxType Directory to convert string back to object
        for (String name : FluxUtils.getFluxNames()) {
            if (nbt.contains(name)) {
                SingleFluxStorage fluxStorage = this.storedFlux.get(FluxUtils.getFluxType(name));
                fluxStorage.deserializeNBT(nbt.get(name));
            }
        }

    }
}
