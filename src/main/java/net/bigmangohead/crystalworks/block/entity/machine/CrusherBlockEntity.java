package net.bigmangohead.crystalworks.block.entity.machine;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.entity.abstraction.SmallMachineBlockEntity;
import net.bigmangohead.crystalworks.recipe.CrusherRecipe;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.screen.menu.CrusherMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public class CrusherBlockEntity extends SmallMachineBlockEntity implements MenuProvider {

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int SLOT_COUNT = 2;

    public CrusherBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRUSHER_BE.get(), pPos, pBlockState);

        finishCreation();
    }

    public int getSlotCount() {
        return SLOT_COUNT;
    }

    @Override
    public Set<Integer> capabilitiesCannotExtract() {
        return Set.of(INPUT_SLOT);
    }

    @Override
    public Set<Integer> capabilitiesCannotInsert() {
        return Set.of(OUTPUT_SLOT);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block." + CrystalWorksMod.MOD_ID + ".crusher");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CrusherMenu(i, inventory, this, this.data);
    }

    @Override
    public void onServerTick(Level level, BlockPos blockPos, BlockState blockState) {
        //TODO: More robust method of checking recipes to optimize more

        if(hasRecipe()) {
            progress.obj ++;
            progress.queueUpdate();

            if(hasProgressFinished()) {
                craftItem();
                inventory.queueUpdate();
                progress.obj = 0;
            }
        } else {
            if (progress.obj > 0) {
                progress.obj = 0;
                progress.queueUpdate();
            }
        }

        super.onServerTick(level, blockPos, blockState);
    }

    private void craftItem() {
        Optional<CrusherRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(null);
        ItemStack inputStack = this.getStackInSlot(INPUT_SLOT);


        this.setStackInSlot(INPUT_SLOT, ItemHandlerHelper.copyStackWithSize(
                inputStack, inputStack.getCount() - recipe.get().getInputCount()));

        this.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean enoughInputItems(CrusherRecipe recipe, int inputCount) {
        return (recipe.getInputCount() <= inputCount);
    }

    private boolean hasProgressFinished() {
        return progress.obj >= maxProgress.obj;
    }

    private boolean hasRecipe() {
        Optional<CrusherRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(null);

        // Note for max progress: Always rounds recipe progress up
        // This makes it harder to get a 1 tick machine
        this.maxProgress.obj = (int) Math.ceil(recipe.get().getRecipeTimeModifier() * defaultMaxProgress);

        return enoughInputItems(recipe.get(), this.getStackInSlot(INPUT_SLOT).getCount()) && canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<CrusherRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.inventory.obj.getSlots());
        for(int i = 0; i < this.inventory.obj.getSlots(); i++) {
            inventory.setItem(i, this.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(CrusherRecipe.Type.INSTANCE, inventory, level); //Can be optimized by also sending in the last recipe
    }
}
