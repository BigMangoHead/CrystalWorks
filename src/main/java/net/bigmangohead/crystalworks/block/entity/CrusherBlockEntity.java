package net.bigmangohead.crystalworks.block.entity;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.abstraction.AbstractInventoryBlockEntity;
import net.bigmangohead.crystalworks.recipe.CrusherRecipe;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.screen.menu.CrusherMenu;
import net.bigmangohead.crystalworks.util.energy.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.windows.INPUT;

import java.util.Optional;
import java.util.Set;

public class CrusherBlockEntity extends AbstractInventoryBlockEntity implements MenuProvider {

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    private int progress = 0;
    private final int defaultMaxProgress = 78; //Represents total amount of ticks per recipe by default
    private int maxProgress = 78; //Represents total amount of ticks in a recipe after recipe modifier is applied

    private final CustomEnergyStorage energy = new CustomEnergyStorage(10000, 100, 0, 0);
    private final LazyOptional<CustomEnergyStorage> energyOptional = LazyOptional.of(() -> this.energy);

    public CrusherBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRUSHER_BE.get(), pPos, pBlockState, 2, Set.of(1), Set.of(0));
    }

    @Override
    public int getData(int index) {
        return switch (index) {
            case DataIndex.ENERGY -> CrusherBlockEntity.this.energy.getEnergyStored();
            case DataIndex.MAX_ENERGY -> CrusherBlockEntity.this.energy.getMaxEnergyStored();
            case DataIndex.PROGRESS -> CrusherBlockEntity.this.progress;
            case DataIndex.MAX_PROGRESS -> CrusherBlockEntity.this.maxProgress;
            default -> super.getData(index);
        };
    }

    @Override
    public void setData(int index, int value) {
        switch (index) {
            case DataIndex.ENERGY -> CrusherBlockEntity.this.energy.setEnergy(value);
            case DataIndex.MAX_ENERGY -> CrusherBlockEntity.this.energy.setMaxEnergyStored(value);
            case DataIndex.PROGRESS -> CrusherBlockEntity.this.progress = value;
            case DataIndex.MAX_PROGRESS -> CrusherBlockEntity.this.maxProgress = value;
        }
        super.setData(index, value);
    }

    @Override
    public int getDataCount() {
        return DataIndex.AMOUNT_OF_VALUES;
    }

    public static class DataIndex {
        public static final int AMOUNT_OF_VALUES = 4 + AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES;

        // Data index starts at previous final index
        public static final int PROGRESS = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES;
        public static final int MAX_PROGRESS = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 1;
        public static final int ENERGY = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 2;
        public static final int MAX_ENERGY = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 3;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyOptional.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    public LazyOptional<CustomEnergyStorage> getEnergyOptional() {
        return this.energyOptional;
    }

    public CustomEnergyStorage getEnergy() {
        return this.energy;
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
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("crusher.progress", progress);
        pTag.put("energy", this.energy.serializeNBT());

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) { //Consider adding a specific mod tag to make sure that other mods don't try overriding this data
        super.load(pTag);
        energy.deserializeNBT(pTag.get("energy"));
        progress = pTag.getInt("crusher.progress");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        // Note: This can be changed to only sync data that is necessary to send
        saveAdditional(nbt);
        return nbt;
    }

    // Below code is only necessary if you only want to sync certain data
    // By default, the load method is used.
    // Additionally, onDataPacket needs to be overridden
    /*
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }
    */

    @Override
    public void onServerTick(Level level, BlockPos blockPos, BlockState blockState) {
        //TODO: More robust method of checking recipes to optimize more

        if(hasRecipe()) {
            progress ++;
            setChanged(level, blockPos, blockState);

            if(hasProgressFinished()) {
                craftItem();
                progress = 0;
            }
        } else {
            progress = 0;
        }

        //sync to client. TODO: Switch for specific packets method
        // Note, I might have already added that above, can't remember ¯\_(ツ)_/¯
        this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    private void craftItem() {
        Optional<CrusherRecipe> recipe = getCurrentRecipe();
        ItemStack result = recipe.get().getResultItem(null);
        ItemStack inputStack = this.inventory.getStackInSlot(INPUT_SLOT);


        this.inventory.setStackInSlot(INPUT_SLOT, ItemHandlerHelper.copyStackWithSize(
                inputStack, inputStack.getCount() - recipe.get().getInputCount()));

        this.inventory.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.inventory.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.inventory.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.inventory.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.inventory.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.inventory.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean enoughInputItems(CrusherRecipe recipe, int inputCount) {
        return (recipe.getInputCount() <= inputCount);
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private boolean hasRecipe() {
        Optional<CrusherRecipe> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(null);

        // Note for max progress: Always rounds recipe progress up
        // This makes it harder to get a 1 tick machine
        this.maxProgress = (int) Math.ceil(recipe.get().getRecipeTimeModifier() * defaultMaxProgress);

        return enoughInputItems(recipe.get(), this.inventory.getStackInSlot(INPUT_SLOT).getCount()) && canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private Optional<CrusherRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.inventory.getSlots());
        for(int i = 0; i < this.inventory.getSlots(); i++) {
            inventory.setItem(i, this.inventory.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(CrusherRecipe.Type.INSTANCE, inventory, level); //Can be optimized by also sending in the last recipe
    }
}
