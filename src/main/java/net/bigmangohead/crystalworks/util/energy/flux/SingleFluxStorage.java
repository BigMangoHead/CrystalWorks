package net.bigmangohead.crystalworks.util.energy.flux;

import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.Date;

// TODO: deserializeNBT needs to also take in the flux capacity
public class SingleFluxStorage implements INBTSerializable<Tag>, ISingleFluxStorage {

    protected final FluxType fluxType;
    protected int flux;
    protected int capacity;
    protected long lastTimeChanged = 0;
    protected int maxReceive;
    protected int maxExtract;
    // Parameters are the flux type, old flux amount, then new flux amount
    private final TriConsumer<FluxType, Integer, Integer> onFluxChange;


    public SingleFluxStorage(FluxType fluxType, int capacity, int maxReceive, int maxExtract, int flux, TriConsumer<FluxType, Integer, Integer> onFluxChange) {
        this.fluxType = fluxType;
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.onFluxChange = onFluxChange;

        this.flux = Math.max(0, Math.min(capacity, flux));
    }



    public int receiveFlux(int maxReceive, boolean simulate) {
        if (!this.canReceive()) {
            return 0;
        } else {
            int fluxReceived = Math.min(this.capacity - this.flux, Math.min(this.maxReceive, maxReceive));
            if (!simulate && fluxReceived != 0) {
                onFluxChange(this.flux, this.flux - fluxReceived);
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
            if (!simulate && fluxExtracted != 0) {
                onFluxChange(this.flux, this.flux - fluxExtracted);
                this.flux -= fluxExtracted;
            }

            return fluxExtracted;
        }
    }

    public void forceSetFlux(int amount) {
        if (amount != this.flux) {
            onFluxChange(this.flux, amount);
        }

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

    public void forceSetCapacity(int amount) {
        if (amount >= 0) {
            this.capacity = amount;
            if (this.flux > this.capacity) {
                onFluxChange(this.flux, this.capacity);
                this.flux = this.capacity;
            }
        }
    }



    protected void onFluxChange(int oldFlux, int newFlux) {
        this.onFluxChange.accept(this.fluxType, oldFlux, newFlux);
        this.lastTimeChanged = new Date().getTime();
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

    public long getLastTimeChanged() {
        return lastTimeChanged;
    }

    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    // TODO: Make FluxStorage send lastTimeFluxChanged data only on menu
    public Tag serializeNBT() {
        long[] data = new long[2];
        data[0] = getFluxStored();
        data[1] = lastTimeChanged;

        return new LongArrayTag(data);
    }

    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof LongArrayTag arrayTag) {
            this.flux = arrayTag.get(0).getAsInt();
            this.lastTimeChanged = arrayTag.get(1).getAsLong();
        } else {
            throw new IllegalArgumentException("Cannot deserialize to an instance that isn't the default implementation");
        }
    }
}
