package net.bigmangohead.crystalworks.util.energy.flux;

import java.util.HashMap;

public class FluxType {

    private final float fluxModifier;
    private final float capacityModifier;
    private final float maxExtractModifier;
    private final float maxReceiveModifier;
    private final String name;

    public FluxType(float fluxModifier, float capacityModifier, float maxExtractModifier, float maxReceiveModifier, String name) {
        this.fluxModifier = fluxModifier;
        this.capacityModifier = capacityModifier;
        this.maxExtractModifier = maxExtractModifier;
        this.maxReceiveModifier = maxReceiveModifier;
        this.name = name;
    }


    public String getName() {
        return this.name;
    }

    public int applyFluxModifier(int globalFlux) {
        return (int) (globalFlux * this.fluxModifier);
    }

    public int applyCapacityModifier(int globalCapacity) {
        return (int) (globalCapacity * this.capacityModifier);
    }

    public int applyMaxExtractModifier(int globalMaxExtract) {
        return (int) (globalMaxExtract * this.maxExtractModifier);
    }

    public int applyMaxReceiveModifier(int globalMaxReceive) {
        return (int) (globalMaxReceive * this.maxReceiveModifier);
    }

}
