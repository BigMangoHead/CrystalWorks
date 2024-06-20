package net.bigmangohead.crystalworks.block.entity.abstraction;

import com.mojang.datafixers.util.Pair;
import net.bigmangohead.crystalworks.recipe.SimpleProcessingRecipe;
import net.bigmangohead.crystalworks.util.energy.flux.FluxUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Optional;
import java.util.Set;

public abstract class SimpleProcessBlockEntity extends SmallMachineBlockEntity implements MenuProvider {

    private static final Pair<ResourceLocation, SimpleProcessingRecipe> emptyRecipe = Pair.of(null, null);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int SLOT_COUNT = 2;

    private static final int DEFAULT_ENERGY_COST = 5;

    // Note that currentRecipe is updated per tick while lastRecipe stores the last successful recipe
    private Pair<ResourceLocation, SimpleProcessingRecipe> lastRecipe = Pair.of(null, null);
    private Pair<ResourceLocation, SimpleProcessingRecipe> currentRecipe = Pair.of(null, null);

    private int recipeEnergyCost = 0;

    public SimpleProcessBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
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
    public void onServerTick(Level level, BlockPos blockPos, BlockState blockState) {
        updateCurrentRecipe();

        //TODO: More robust method of checking recipes to optimize more
        boolean canRunRecipeFromItems = canRunRecipeFromItems();

        // If we have items and energy, run recipe, and reset progress if recipe is different from previous recipe.
        // If we have just items, do nothing, as we're waiting for energy
        // If we don't have the items, reset the progress
        if(canRunRecipeFromItems && recipeEnergyCost <= getEnergy().getEnergyStored()) {
            if (currentRecipe.getSecond() != lastRecipe.getSecond()) {
                progress.obj = 0;
            }

            progress.obj++;
            flux.obj.forceRemoveFlux(FluxUtils.getFluxType("redstone"), recipeEnergyCost);
            flux.queueUpdate();
            progress.queueUpdate();

            if (hasProgressFinished()) {
                craftItem();
                inventory.queueUpdate();
                progress.obj = 0;
            }
        } else if (!canRunRecipeFromItems) {
            if (progress.obj > 0) {
                progress.obj = 0;
                progress.queueUpdate();
            }
        }

        super.onServerTick(level, blockPos, blockState);
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean enoughInputItems(SimpleProcessingRecipe recipe, int inputCount) {
        return (recipe.getInputCount() <= inputCount);
    }

    private boolean canRunRecipeFromItems() {
        if (this.currentRecipe == emptyRecipe) {
            return false;
        }
        SimpleProcessingRecipe recipe = this.currentRecipe.getSecond();
        ItemStack result = recipe.getResultItem(null);
        return enoughInputItems(recipe, this.getStackInSlot(INPUT_SLOT).getCount()) && canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private void updateCurrentRecipe() {
        // Save the old recipe, unless it's blank in which we should keep what was there before.
        if (this.currentRecipe != emptyRecipe) {
            this.lastRecipe = this.currentRecipe;
        }

        if (this.getStackInSlot(INPUT_SLOT).getCount() == 0) {
            this.currentRecipe = emptyRecipe;
            return;
        }

        // Create container and get recipe
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, this.getStackInSlot(INPUT_SLOT));
        Optional<Pair<ResourceLocation, SimpleProcessingRecipe>> recipeOptional = this.level.getRecipeManager().getRecipeFor(getRecipeType(), inventory, level, lastRecipe.getFirst());

        if (recipeOptional.isEmpty()) {
            this.currentRecipe = emptyRecipe;
            return;
        }

        this.currentRecipe = recipeOptional.get();
        updateEnergyCost();

        SimpleProcessingRecipe recipe = recipeOptional.get().getSecond();

        // Update maximum progress for the recipe
        // Note for max progress: Always rounds recipe progress up
        // This makes it harder to get a 1 tick machine
        if (this.maxProgress.obj != (int) Math.ceil(recipe.getRecipeTimeMultiplier() * defaultMaxProgress)) {
            this.maxProgress.obj = (int) Math.ceil(recipe.getRecipeTimeMultiplier() * defaultMaxProgress);
            this.maxProgress.queueUpdate();
        }
    }

    private void updateEnergyCost() {
        this.recipeEnergyCost = (int) (getMachineEnergyCost() * currentRecipe.getSecond().getEnergyMultiplier());
    }

    private void craftItem() {
        SimpleProcessingRecipe recipe = this.currentRecipe.getSecond();
        ItemStack result = recipe.getResultItem(null);
        ItemStack inputStack = this.getStackInSlot(INPUT_SLOT);


        this.setStackInSlot(INPUT_SLOT, ItemHandlerHelper.copyStackWithSize(
                inputStack, inputStack.getCount() - recipe.getInputCount()));

        this.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    protected abstract SimpleProcessingRecipe.Type getRecipeType();

    protected int getMachineEnergyCost() {
        return DEFAULT_ENERGY_COST;
    }
}
