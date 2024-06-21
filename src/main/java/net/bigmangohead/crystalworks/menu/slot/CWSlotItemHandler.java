package net.bigmangohead.crystalworks.menu.slot;

import net.bigmangohead.crystalworks.util.item.CWItemStackHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CWSlotItemHandler extends SlotItemHandler {
    protected int index;
    protected CWItemStackHandler itemHandler;

    public CWSlotItemHandler(CWItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return !stack.isEmpty() && this.itemHandler.canItemStackBeInserted(index, stack, true);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return this.itemHandler.canItemStackBeExtracted(index, true);
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        return this.itemHandler.extractItem(this.index, amount, false, true);
    }
}
