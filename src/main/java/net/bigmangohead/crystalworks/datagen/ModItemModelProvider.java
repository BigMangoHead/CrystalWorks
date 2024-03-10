package net.bigmangohead.crystalworks.datagen;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.item.CrystalItems;
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
        simpleItem(CrystalItems.SAPPHIRE);

        simpleItem(CrystalItems.IRON_DUST);
        simpleItem(CrystalItems.GOLD_DUST);
        simpleItem(CrystalItems.DIAMOND_DUST);
        simpleItem(CrystalItems.QUARTZ_DUST);
        simpleItem(CrystalItems.NETHERITE_DUST);
        simpleItem(CrystalItems.EMERALD_DUST);
        simpleItem(CrystalItems.COPPER_DUST);
        simpleItem(CrystalItems.LAPIS_DUST);

        simpleItem(CrystalItems.METAL_DETECTOR);
        simpleItem(CrystalItems.CRYSTAL_FORMER);
    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(CrystalWorksMod.MOD_ID,"item/" + item.getId().getPath()));
    }
}
