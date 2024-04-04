package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.item.custom.CrystalFormerItem;
import net.bigmangohead.crystalworks.item.custom.MetalDetectorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CrystalWorksMod.MOD_ID);

    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire", 
            () -> new Item(new Item.Properties()));



    public static final RegistryObject<Item> METAL_DETECTOR = ITEMS.register("metal_detector",
            () -> new MetalDetectorItem(new Item.Properties().durability(100)));
    public static final RegistryObject<Item> CRYSTAL_FORMER = ITEMS.register("crystal_former",
            () -> new CrystalFormerItem(new Item.Properties()));

    public static final RegistryObject<Item> IRON_DUST = ITEMS.register("iron_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GOLD_DUST = ITEMS.register("gold_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_DUST = ITEMS.register("diamond_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> EMERALD_DUST = ITEMS.register("emerald_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LAPIS_DUST = ITEMS.register("lapis_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_DUST = ITEMS.register("netherite_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COPPER_DUST = ITEMS.register("copper_dust",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> QUARTZ_DUST = ITEMS.register("quartz_dust",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
