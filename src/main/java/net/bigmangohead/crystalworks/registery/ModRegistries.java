package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.util.energy.flux.FluxType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;

import java.util.function.Supplier;

public class ModRegistries {
    private static final ResourceLocation fluxTypeName = new ResourceLocation(CrystalWorksMod.MOD_ID, "flux_type");
    public static final ResourceKey<Registry<FluxType>> fluxTypeKey = ResourceKey.createRegistryKey(fluxTypeName);

    public static Supplier<IForgeRegistry<FluxType>> FLUX_TYPES;

    public static void register(NewRegistryEvent event) {
        RegistryBuilder<FluxType> registryBuilder = RegistryBuilder.of(fluxTypeName);
        FLUX_TYPES = event.create(registryBuilder);
    }
}
