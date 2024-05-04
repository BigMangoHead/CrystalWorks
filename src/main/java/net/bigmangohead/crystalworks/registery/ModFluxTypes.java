package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.util.energy.flux.FluxType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

// Definitely not how registering stuff is supposed to work,
// but I don't currently understand this well to do this properly
// TODO: Figure out proper registration of flux types so that other mods can implement this and the system is more robust
public class ModFluxTypes {
    public static final FluxType DIAMOND = new FluxType(1, 1, 1, 1, "diamond");

    public static DeferredRegister<FluxType> FLUX_TYPES;
    public static RegistryObject<FluxType> GOLD;

    public static void registerDeprecated(IEventBus eventBus) {
        FLUX_TYPES = DeferredRegister.create(ModRegistries.FLUX_TYPES.get(), CrystalWorksMod.MOD_ID);

        GOLD = FLUX_TYPES.register("gold", () -> new FluxType(1, 1, 1, 1, "gold"));

        FLUX_TYPES.register(eventBus);
    }

    public static void register(RegisterEvent event) {
        event.register(ModRegistries.fluxTypeKey,
                helper -> {
            helper.register("gold", new FluxType(1, 1, 1, 1, "gold"));
        });
    }
}
