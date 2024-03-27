package net.bigmangohead.crystalworks.datagen;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.registery.CrystalBlocks;
import net.bigmangohead.crystalworks.registery.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {

    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CrystalWorksMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Blocks.CRYSTAL_MATRIX_BLOCKS)
                .add(CrystalBlocks.SAPPHIRE_BLOCK.get(),
                        Blocks.DIAMOND_BLOCK,
                        Blocks.EMERALD_BLOCK);


        this.tag(BlockTags.NEEDS_STONE_TOOL);

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(CrystalBlocks.SAPPHIRE_BLOCK.get(),
                        CrystalBlocks.SAPPHIRE_ORE.get(),
                        CrystalBlocks.CRUSHER.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(CrystalBlocks.DEEPSLATE_SAPPHIRE_ORE.get());

        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL);


        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(CrystalBlocks.SAPPHIRE_BLOCK.get(),
                        CrystalBlocks.SAPPHIRE_ORE.get(),
                        CrystalBlocks.DEEPSLATE_SAPPHIRE_ORE.get());
    }
}
