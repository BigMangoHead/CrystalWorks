package net.bigmangohead.crystalworks.util.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;

public class ItemUtils {
    public static int getBurnTime(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
    }

    public static boolean canBurn(ItemStack stack) {
        return getBurnTime(stack) > 0;
    }
}
