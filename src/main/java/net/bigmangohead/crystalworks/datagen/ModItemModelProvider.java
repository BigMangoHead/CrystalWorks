package net.bigmangohead.crystalworks.datagen;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.registery.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CrystalWorksMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.SAPPHIRE);

        simpleItem(ModItems.IRON_DUST);
        simpleItem(ModItems.GOLD_DUST);
        simpleItem(ModItems.DIAMOND_DUST);
        simpleItem(ModItems.QUARTZ_DUST);
        simpleItem(ModItems.NETHERITE_DUST);
        simpleItem(ModItems.EMERALD_DUST);
        simpleItem(ModItems.COPPER_DUST);
        simpleItem(ModItems.LAPIS_DUST);

        simpleItem(ModItems.METAL_DETECTOR);
        simpleItem(ModItems.CRYSTAL_FORMER);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(CrystalWorksMod.MOD_ID,"item/" + item.getId().getPath()));
    }
}
