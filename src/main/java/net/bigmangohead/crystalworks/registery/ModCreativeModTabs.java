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
                        pOutput.accept(CrystalItems.SAPPHIRE.get());
                        pOutput.accept(CrystalBlocks.SAPPHIRE_BLOCK.get());
                        pOutput.accept(CrystalBlocks.JADE_BLOCK.get());

                        pOutput.accept(CrystalBlocks.SAPPHIRE_ORE.get());
                        pOutput.accept(CrystalBlocks.DEEPSLATE_SAPPHIRE_ORE.get());

                        pOutput.accept(CrystalItems.IRON_DUST.get());
                        pOutput.accept(CrystalItems.GOLD_DUST.get());
                        pOutput.accept(CrystalItems.EMERALD_DUST.get());
                        pOutput.accept(CrystalItems.DIAMOND_DUST.get());
                        pOutput.accept(CrystalItems.LAPIS_DUST.get());
                        pOutput.accept(CrystalItems.NETHERITE_DUST.get());
                        pOutput.accept(CrystalItems.QUARTZ_DUST.get());
                        pOutput.accept(CrystalItems.COPPER_DUST.get());

                        pOutput.accept(CrystalBlocks.CRUSHER.get());
                        pOutput.accept(CrystalBlocks.BASIC_GENERATOR.get());

                        pOutput.accept(CrystalItems.METAL_DETECTOR.get());
                        pOutput.accept(CrystalItems.CRYSTAL_FORMER.get());
                    }).build());



    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
