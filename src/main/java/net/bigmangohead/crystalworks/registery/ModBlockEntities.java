package net.bigmangohead.crystalworks.registery;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.entity.*;
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

    public static final RegistryObject<BlockEntityType<CrystalBlockEntity>> GEM_BE =
            BLOCK_ENTITIES.register("gem_be", () ->
                    BlockEntityType.Builder.of(CrystalBlockEntity::new,
                            ModBlocks.JADE_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

}
