package net.bigmangohead.crystalworks.datagen;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.registery.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CrystalWorksMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.SAPPHIRE_BLOCK);
        blockWithItem(ModBlocks.JADE_BLOCK);

        blockWithItem(ModBlocks.SAPPHIRE_ORE);
        blockWithItem(ModBlocks.DEEPSLATE_SAPPHIRE_ORE);

        simpleBlockWithItem(ModBlocks.CRUSHER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/crusher")));
        blockWithItem(ModBlocks.BASIC_GENERATOR);
        horizontalBlockWithItem(ModBlocks.PLATE_FORMER);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }

    private void horizontalBlockWithItem(RegistryObject<Block> blockRegistryObject) {
        ResourceLocation textureLocation = blockTexture(blockRegistryObject.get());
        ModelFile modelFile = this.models().orientable(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(),
                textureLocation.withSuffix("_side"), textureLocation.withSuffix("_front"), textureLocation.withSuffix("_top"));

        horizontalBlock(blockRegistryObject.get(), modelFile);
        simpleBlockItem(blockRegistryObject.get(), modelFile);
    }
}
