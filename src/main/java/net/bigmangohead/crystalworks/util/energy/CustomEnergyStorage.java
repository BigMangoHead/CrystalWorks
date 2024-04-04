package net.bigmangohead.crystalworks.util.energy;

import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {
    public CustomEnergyStorage(int capacity) {
        super(capacity);
    }

    public CustomEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getMaxReceive() {
        return this.maxReceive;
    }

    public int getMaxExtract() {
        return this.maxExtract;
    }

    public void setEnergy(int energy) {
        if(energy < 0) {
            this.energy = 0;
        } else if (energy > this.capacity) {
            this.energy = this.capacity;
        } else {
            this.energy = energy;
        }
    }

    public void addEnergy(int amount) {
        if (amount >= 0) {
            setEnergy(this.energy + amount);
        } else {
            throw new IllegalArgumentException("Non-negative integer expected for addEnergy method");
        }
    }

    public void removeEnergy(int amount) {
        if (amount >= 0) {
            setEnergy(this.energy - amount);
        } else {
            throw new IllegalArgumentException("Non-negative integer expected for removeEnergy method");
        }
    }

    public void setMaxEnergyStored(int capacity) {
        if(capacity > 0) {
            this.capacity = capacity;
        }
    }
}
