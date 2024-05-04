package net.bigmangohead.crystalworks.util.energy.flux;

import java.util.HashMap;

public class FluxType {

    private final float fluxModifier;
    private final float capacityModifier;
    private final float maxExtractModifier;
    private final float maxReceiveModifier;
    private final String name;

    private static final HashMap<String, FluxType> fluxTypeDirectory = new HashMap<>();

    public FluxType(float fluxModifier, float capacityModifier, float maxExtractModifier, float maxReceiveModifier, String name) {
        this.fluxModifier = fluxModifier;
        this.capacityModifier = capacityModifier;
        this.maxExtractModifier = maxExtractModifier;
        this.maxReceiveModifier = maxReceiveModifier;
        this.name = name;

        //Might be a better way to do this since I'm using registries, but I can't find it at the least.
        fluxTypeDirectory.put(name, this);
    }

    public static FluxType getFluxType(String name) {
        return fluxTypeDirectory.get(name);
    }

    public static HashMap<String, FluxType> getFluxTypeDirectory() {
        return fluxTypeDirectory;
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
