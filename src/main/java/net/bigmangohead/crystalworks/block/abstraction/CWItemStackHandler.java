package net.bigmangohead.crystalworks.block.abstraction;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;


public class CWItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {
    private final BlockEntity blockEntity;

    // I don't feel very confident about the data structures I chose for these handlers.
    // Probably should look at this again later, could be pretty unoptimized.

    //Contains all item stacks, visible or not, based on pre-set indexes
    protected NonNullList<ItemStack> stacks;

    // Purpose of visible slots is to make parts of the container
    // completely invisible to other block entities that attempt
    // to access the container. This is distinct from adding a slot to
    // cannotExtract and cannonInsert as this prevents even reading
    // the slot.
    private LinkedList<Integer> visibleSlots;
    private LinkedList<Integer> defaultVisibleSlots;

    private Set<Integer> cannotInsert;
    private Set<Integer> cannotExtract;

    private int convertToInvisibleStackIndex(int slot) {
        return visibleSlots.get(slot);
    }

    public CWItemStackHandler(BlockEntity blockEntity, int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.blockEntity = blockEntity;
        this.defaultVisibleSlots = new LinkedList<Integer>();
        for (int i = 0; i < size; i++) { // Default state is {0, 1, ...}
            this.defaultVisibleSlots.add(i);
        }

        this.visibleSlots = new LinkedList<Integer>();
        this.visibleSlots = (LinkedList<Integer>) defaultVisibleSlots.clone();

        this.cannotInsert = Set.of();
        this.cannotExtract = Set.of();
    }

    public CWItemStackHandler(BlockEntity blockEntity, int size, Set<Integer> cannotInsert, Set<Integer> cannotExtract) {
        this(blockEntity, size);
        this.cannotInsert = cannotInsert;
        this.cannotExtract = cannotExtract;
    }

    public void setSize(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        this.defaultVisibleSlots = new LinkedList<Integer>();
        for (int i = 0; i < size; i++) { // Default state is {0, 1, ...}
            defaultVisibleSlots.add(i);
        }
        this.visibleSlots = (LinkedList<Integer>) defaultVisibleSlots.clone();
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.validateSlotIndex(slot);
        slot = this.convertToInvisibleStackIndex(slot);
        this.stacks.set(slot, stack);
        this.onContentsChanged(slot);
    }

    public int getSlots() {
        return this.visibleSlots.size();
    }

    public @NotNull ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        slot = this.convertToInvisibleStackIndex(slot);
        return (ItemStack) this.stacks.get(slot);
    }

    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate, boolean menu) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.canItemStackBeInserted(slot, stack, menu)) {
            return stack;
        } else {
            this.validateSlotIndex(slot);
            slot = this.convertToInvisibleStackIndex(slot);
            ItemStack existing = (ItemStack)this.stacks.get(slot);
            int limit = this.getStackLimit(slot, stack);
            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (!simulate) {
                    if (existing.isEmpty()) {
                        this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }

                    this.onContentsChanged(slot);
                }

                return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }

    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.insertItem(slot, stack, simulate, false);
    }

    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate, boolean menu) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            slot = this.convertToInvisibleStackIndex(slot);
            ItemStack existing = (ItemStack)this.stacks.get(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else if (!canItemStackBeExtracted(slot, menu)) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    if (!simulate) {
                        this.stacks.set(slot, ItemStack.EMPTY);
                        this.onContentsChanged(slot);
                        return existing;
                    } else {
                        return existing.copy();
                    }
                } else {
                    if (!simulate) {
                        this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                        this.onContentsChanged(slot);
                    }

                    return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
                }
            }
        }
    }

    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractItem(slot, amount, simulate, false);
    }

    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return canItemStackBeInserted(slot, stack, false);
    }

    public boolean canItemStackBeInserted(int slot, @NotNull ItemStack stack, boolean menu) {
        if (menu) return true;
        return !this.cannotInsert.contains(slot);
    }

    public boolean canItemStackBeExtracted(int slot, boolean menu) {
        if (menu) return true;
        return !this.cannotExtract.contains(slot);
    }

    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();

        for(int i = 0; i < this.stacks.size(); ++i) {
            if (!((ItemStack)this.stacks.get(i)).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                ((ItemStack)this.stacks.get(i)).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", this.stacks.size());
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.setSize(nbt.contains("Size", 3) ? nbt.getInt("Size") : this.stacks.size());
        ListTag tagList = nbt.getList("Items", 10);

        for(int i = 0; i < tagList.size(); ++i) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");
            if (slot >= 0 && slot < this.stacks.size()) {
                this.stacks.set(slot, ItemStack.of(itemTags));
            }
        }

        this.onLoad();
    }

    protected void validateSlotIndex(int externalSlot) {
        if (externalSlot < 0 || externalSlot >= this.visibleSlots.size()) {
            throw new RuntimeException("Slot " + externalSlot + " not in valid range - [0," + this.visibleSlots.size() + ")");
        }
    }



    protected void onContentsChanged(int slot) {
        this.blockEntity.setChanged();
    }

    private void onLoad() {
    }

    public void hideSlot(int internalSlot) {
        if (!this.visibleSlots.contains(internalSlot)) {
            return;
        }
        int externalSlot = this.visibleSlots.get(this.visibleSlots.indexOf(internalSlot));
        this.visibleSlots.remove(externalSlot);
    }

    public void revealSlot(int internalSlot) {
        this.visibleSlots.add(internalSlot);
        Collections.sort(this.visibleSlots);
    }

    public void setHiddenSlots(LinkedList<Integer> newVisibleSlots) {
        this.visibleSlots = newVisibleSlots;
    }

    public void resetHiddenSlots(LinkedList<Integer> newVisibleSlots) {
        this.visibleSlots = (LinkedList<Integer>) defaultVisibleSlots.clone();
    }
}
