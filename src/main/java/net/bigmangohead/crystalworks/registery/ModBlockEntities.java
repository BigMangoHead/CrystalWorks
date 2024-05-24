package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.entity.*;
import net.bigmangohead.crystalworks.block.entity.machine.BasicGeneratorBlockEntity;
import net.bigmangohead.crystalworks.block.entity.machine.CrusherBlockEntity;
import net.bigmangohead.crystalworks.block.entity.machine.PlateFormerBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CrystalWorksMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<BasicGeneratorBlockEntity>> BASIC_GENERATOR_BE =
            BLOCK_ENTITIES.register("basic_generator_be", () ->
                    BlockEntityType.Builder.of(BasicGeneratorBlockEntity::new,
                            ModBlocks.BASIC_GENERATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CrusherBlockEntity>> CRUSHER_BE =
            BLOCK_ENTITIES.register("crusher_be", () ->
                    BlockEntityType.Builder.of(CrusherBlockEntity::new,
                            ModBlocks.CRUSHER.get()).build(null));
    public static final RegistryObject<BlockEntityType<PlateFormerBlockEntity>> PLATE_FORMER_BE =
            BLOCK_ENTITIES.register("plate_former_be", () ->
                    BlockEntityType.Builder.of(PlateFormerBlockEntity::new,
                            ModBlocks.PLATE_FORMER.get()).build(null));

    // TODO: Figure out how to make the vanilla blocks count as gem blocks.
    public static final RegistryObject<BlockEntityType<CrystalBlockEntity>> GEM_BE =
            BLOCK_ENTITIES.register("gem_be", () ->
                    BlockEntityType.Builder.of(CrystalBlockEntity::new,
                            ModBlocks.JADE_BLOCK.get(),
                            Blocks.DIAMOND_BLOCK,
                            Blocks.EMERALD_BLOCK).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
