package net.bigmangohead.crystalworks.util.energy.flux;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

public class SingleFluxStorage implements INBTSerializable<Tag> {

    protected FluxType fluxType;
    protected int flux;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public SingleFluxStorage(FluxType fluxType, int capacity, int maxReceive, int maxExtract, int flux) {
        this.fluxType = fluxType;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;

        this.flux = Math.max(0, Math.min(capacity, flux));
    }



    public int receiveFlux(int maxReceive, boolean simulate) {
        if (!this.canReceive()) {
            return 0;
        } else {
            int fluxReceived = Math.min(this.capacity - this.flux, Math.min(this.maxReceive, maxReceive));
            if (!simulate) {
                this.flux += fluxReceived;
            }

            return fluxReceived;
        }
    }

    public int extractFlux(int maxExtract, boolean simulate) {
        if (!this.canExtract()) {
            return 0;
        } else {
            int fluxExtracted = Math.min(this.flux, Math.min(this.maxExtract, maxExtract));
            if (!simulate) {
                this.flux -= fluxExtracted;
            }

            return fluxExtracted;
        }
    }

    public void forceSetFlux(int amount) {
        if(amount < 0) {
            this.flux = 0;
        } else if (amount > this.capacity) {
            this.flux = this.capacity;
        } else {
            this.flux = amount;
        }
    }

    public void forceAddFlux(int amount) {
        if (amount >= 0) {
            forceSetFlux(this.flux + amount);
        } else {
            throw new IllegalArgumentException("Non-negative integer expected for forceAddFlux method");
        }
    }

    public void forceRemoveFlux(int amount) {
        if (amount >= 0) {
            forceSetFlux(this.flux - amount);
        } else {
            throw new IllegalArgumentException("Non-negative integer expected for forceRemoveFlux method");
        }
    }



    public FluxType getFluxType() {
        return fluxType;
    }

    public int getFluxStored() {
        return this.flux;
    }

    public int getMaxFluxStored() {
        return this.capacity;
    }

    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public Tag serializeNBT() {
        return IntTag.valueOf(this.getFluxStored());
    }

    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof IntTag intNbt) {
            this.flux = intNbt.getAsInt();
        } else {
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        }
    }
}
