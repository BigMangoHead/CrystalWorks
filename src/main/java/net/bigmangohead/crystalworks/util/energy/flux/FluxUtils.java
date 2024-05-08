package net.bigmangohead.crystalworks.util.energy.flux;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.registery.ModRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class FluxUtils {

    private static final List<String> fluxNames = new ArrayList<>();
    private static Set<FluxType> fluxTypes;
    private static final HashMap<String, FluxType> fluxNameToTypeMap = new HashMap<>();

    public static void generation() {

        ModRegistries.FLUX_TYPES.get().getKeys().forEach(resourceLocation -> {
            fluxNames.add(resourceLocation.getNamespace());
            fluxNameToTypeMap.put(resourceLocation.getNamespace(), ModRegistries.FLUX_TYPES.get().getValue(resourceLocation));
        });

        fluxTypes = Set.copyOf(ModRegistries.FLUX_TYPES.get().getValues());


    }


    public static FluxType getFluxType(String typeName) {
        return fluxNameToTypeMap.get(typeName);
    }

    public static List<String> getFluxNames() {
        return fluxNames;
    }

    public static Set<FluxType> getFluxTypes() {
        return fluxTypes;
    }
}
