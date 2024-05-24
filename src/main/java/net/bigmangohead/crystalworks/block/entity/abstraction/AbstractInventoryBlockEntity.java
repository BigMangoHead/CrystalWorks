package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.util.item.CWItemStackHandler;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedSerializable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

//Includes some sort of menu
public abstract class AbstractInventoryBlockEntity extends CWBlockEntity implements MenuProvider {
    protected final TrackedObject<CWItemStackHandler> inventory;

    protected LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public AbstractInventoryBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        CWItemStackHandler inventory = new CWItemStackHandler(this, this.getSlotCount(), this.capabilitiesCannotInsert(), this.capabilitiesCannotExtract());

        this.inventory = new TrackedSerializable<>(inventory, "inventory", TrackedType.SAVE_AND_SYNC_ALL_UPDATES, 8, true);
    }

    @Override
    protected void registerTrackedObjects() {
        super.registerTrackedObjects();

        this.trackedObjectHandler.register(this.inventory);
    }

    public static class DataIndex {
        public static final int AMOUNT_OF_VALUES = 1;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.lazyItemHandler = LazyOptional.of(() -> this.inventory.obj);
    }


    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.lazyItemHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }


    public void drops() {
        SimpleContainer inventory = new SimpleContainer(this.inventory.obj.getSlots());
        for(int i = 0; i < this.inventory.obj.getSlots(); i++) {
            inventory.setItem(i, this.inventory.obj.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public abstract int getSlotCount();

    public Set<Integer> capabilitiesCannotInsert() {
        return Set.of();
    }

    public Set<Integer> capabilitiesCannotExtract() {
        return Set.of();
    }

    @Override
    public abstract Component getDisplayName();

    public CWItemStackHandler getInventory() {
        return this.inventory.obj;
    }

    public ItemStack getStackInSlot(int slot) {
        return this.inventory.obj.getStackInSlot(slot);
    }

    public void setStackInSlot(int slot, ItemStack item) {
        this.inventory.obj.setStackInSlot(slot, item);
    }
}
