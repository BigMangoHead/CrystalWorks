package net.bigmangohead.crystalworks.block.entity.machine;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.entity.abstraction.SmallMachineBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PlateFormerBlockEntity extends SmallMachineBlockEntity {

    public static class SlotIndex {
        public static final int INPUT_SLOT = 0;
        public static final int OUTPUT_SLOT = 1;
        public static final int SLOT_COUNT = 2;
    }

    public PlateFormerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.PLATE_FORMER_BE.get(), pPos, pBlockState);
        finishCreation();
    }

    @Override
    public int getSlotCount() {
        return SlotIndex.SLOT_COUNT;
    }

    @Override
    public Set<Integer> capabilitiesCannotExtract() {
        return Set.of(SlotIndex.INPUT_SLOT);
    }

    @Override
    public Set<Integer> capabilitiesCannotInsert() {
        return Set.of(SlotIndex.OUTPUT_SLOT);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block." + CrystalWorksMod.MOD_ID + ".plate_former");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return null;
    }
}
