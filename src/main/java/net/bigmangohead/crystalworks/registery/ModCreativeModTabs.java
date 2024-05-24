package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
                DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CrystalWorksMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CRYSTALWORKS_TAB = CREATIVE_MODE_TABS.register("crystalworks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(Items.AMETHYST_SHARD))
                    .title(Component.translatable("creativetab." + CrystalWorksMod.MOD_ID + "_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SAPPHIRE.get());
                        pOutput.accept(ModBlocks.SAPPHIRE_BLOCK.get());
                        pOutput.accept(ModBlocks.JADE_BLOCK.get());

                        pOutput.accept(ModBlocks.SAPPHIRE_ORE.get());
                        pOutput.accept(ModBlocks.DEEPSLATE_SAPPHIRE_ORE.get());

                        pOutput.accept(ModItems.IRON_DUST.get());
                        pOutput.accept(ModItems.GOLD_DUST.get());
                        pOutput.accept(ModItems.EMERALD_DUST.get());
                        pOutput.accept(ModItems.DIAMOND_DUST.get());
                        pOutput.accept(ModItems.LAPIS_DUST.get());
                        pOutput.accept(ModItems.NETHERITE_DUST.get());
                        pOutput.accept(ModItems.QUARTZ_DUST.get());
                        pOutput.accept(ModItems.COPPER_DUST.get());

                        pOutput.accept(ModBlocks.CRUSHER.get());
                        pOutput.accept(ModBlocks.PLATE_FORMER.get());
                        pOutput.accept(ModBlocks.BASIC_GENERATOR.get());

                        pOutput.accept(ModItems.METAL_DETECTOR.get());
                        pOutput.accept(ModItems.CRYSTAL_FORMER.get());
                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
