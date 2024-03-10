package net.bigmangohead.crystalworks.datagen;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.CrystalBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CrystalWorksMod.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(CrystalBlocks.SAPPHIRE_BLOCK);

        blockWithItem(CrystalBlocks.SAPPHIRE_ORE);
        blockWithItem(CrystalBlocks.DEEPSLATE_SAPPHIRE_ORE);

        simpleBlockWithItem(CrystalBlocks.CRUSHER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/crusher")));
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
