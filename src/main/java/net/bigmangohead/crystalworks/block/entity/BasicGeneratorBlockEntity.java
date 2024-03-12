package net.bigmangohead.crystalworks.block.entity;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.abstraction.AbstractInventoryBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BasicGeneratorBlockEntity extends AbstractInventoryBlockEntity {
    private static final int INPUT_SLOT = 0;

    protected final ContainerData data;
    private int progress = 0;
    private final int defaultMaxProgress = 80; //Represents total amount of ticks per recipe by default
    private int maxProgress = 80; //Represents total amount of ticks in a recipe after recipe modifier is applied

    public BasicGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BASIC_GENERATOR_BE.get(), pos, blockState, 1);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> BasicGeneratorBlockEntity.this.progress;
                    case 1 -> BasicGeneratorBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> BasicGeneratorBlockEntity.this.progress = pValue;
                    case 1 -> BasicGeneratorBlockEntity.this.maxProgress = pValue;
                }

            }

            @Override
            public int getCount() {
                return 2; //Represents amount of integers that need to be synced
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block." + CrystalWorksMod.MOD_ID + ".basic_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("basicgenerator.progress", progress);

        super.saveAdditional(pTag);
    }

    public void load(CompoundTag pTag) { //Consider adding a specific mod tag to make sure that other mods don't try overriding this data
        super.load(pTag);
        progress = pTag.getInt("basicgenerator.progress");
    }
}
