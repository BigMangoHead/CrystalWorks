package net.bigmangohead.crystalworks.menu.slot;

import net.bigmangohead.crystalworks.util.item.ItemUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class CustomFuelSlot extends SlotItemHandler {
    private final Predicate<ItemStack> fuelPredicate;
    public CustomFuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        this(itemHandler, index, xPosition, yPosition, ItemUtils::canBurn);
    }

    public CustomFuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> fuelPredicate) {
        super(itemHandler, index, xPosition, yPosition);
        this.fuelPredicate = fuelPredicate;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return this.fuelPredicate.test(stack);
    }
}
