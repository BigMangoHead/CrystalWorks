package net.bigmangohead.crystalworks.block.abstraction;

import net.bigmangohead.crystalworks.block.entity.CrusherBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;

public class CustomItemStackHandler extends ItemStackHandler {
    private final BlockEntity blockEntity;
    public CustomItemStackHandler(BlockEntity blockEntity, int size) {
        super(size);
        this.blockEntity = blockEntity;
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.blockEntity.setChanged();
    }
}
