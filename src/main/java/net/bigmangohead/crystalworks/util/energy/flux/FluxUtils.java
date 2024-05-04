package net.bigmangohead.crystalworks.util.energy.flux;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.registery.ModRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FluxUtils {

    private static List<String> fluxNames = new ArrayList<>();
    private static Set<FluxType> fluxTypes;

    public static void generation() {

        ModRegistries.FLUX_TYPES.get().getKeys().forEach(resourceLocation -> {
            fluxNames.add(resourceLocation.getNamespace());
        });

        fluxTypes = Set.copyOf(ModRegistries.FLUX_TYPES.get().getValues());

    }


    public static FluxType getFluxType(String typeName) {
        return ModRegistries.FLUX_TYPES.get().getValue(new ResourceLocation(CrystalWorksMod.MOD_ID, typeName));
    }

    public static List<String> getFluxNames() {
        return fluxNames;
    }

    public static Set<FluxType> getFluxTypes() {
        return fluxTypes;
    }
}
