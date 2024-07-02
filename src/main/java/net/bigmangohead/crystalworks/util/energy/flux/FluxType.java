package net.bigmangohead.crystalworks.util.energy.flux;

import net.minecraft.network.chat.Component;

import java.awt.*;

public class FluxType {

    private final float fluxModifier;
    private final float capacityModifier;
    private final float maxExtractModifier;
    private final float maxReceiveModifier;
    private final String name;
    private final Color fluxColor;
    private final Color fluxTextColor;
    private final Component translatedName;

    public FluxType(float fluxModifier, float capacityModifier, float maxExtractModifier, float maxReceiveModifier, String name, Color fluxColor, Color fluxTextColor, Component translatedName) {
        this.fluxModifier = fluxModifier;
        this.capacityModifier = capacityModifier;
        this.maxExtractModifier = maxExtractModifier;
        this.maxReceiveModifier = maxReceiveModifier;
        this.name = name;
        this.fluxColor = fluxColor;
        this.fluxTextColor = fluxTextColor;
        this.translatedName = translatedName;
    }


    public String getName() {
        return this.name;
    }

    public Component getTranslatedName() {
        return this.translatedName;
    }

    public Color getFluxColor() {
        return fluxColor;
    }

    public Color getFluxTextColor() {
        return fluxTextColor;
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
