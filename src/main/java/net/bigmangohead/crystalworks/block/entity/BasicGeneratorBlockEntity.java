package net.bigmangohead.crystalworks.block.entity;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.block.abstraction.AbstractInventoryBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.screen.menu.BasicGeneratorMenu;
import net.bigmangohead.crystalworks.util.energy.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.bigmangohead.crystalworks.util.item.ItemUtils.canBurn;
import static net.bigmangohead.crystalworks.util.item.ItemUtils.getBurnTime;

public class BasicGeneratorBlockEntity extends AbstractInventoryBlockEntity {
    private static final int INPUT_SLOT = 0;

    private int burnTime = 0;
    private final int defaultMaxProgress = 80; //Represents total amount of ticks per recipe by default
    private int maxBurnTime = 0; //Represents total amount of ticks in a recipe after recipe modifier is applied

    private final CustomEnergyStorage energy = new CustomEnergyStorage(10000, 0, 100, 0);
    private final LazyOptional<CustomEnergyStorage> energyOptional = LazyOptional.of(() -> this.energy);

    public BasicGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BASIC_GENERATOR_BE.get(), pos, blockState, 1);
    }

    @Override
    public int getData(int index) {
        return switch (index) {
            case DataIndex.ENERGY -> BasicGeneratorBlockEntity.this.energy.getEnergyStored();
            case DataIndex.MAX_ENERGY -> BasicGeneratorBlockEntity.this.energy.getMaxEnergyStored();
            case DataIndex.BURN_TIME -> BasicGeneratorBlockEntity.this.burnTime;
            case DataIndex.MAX_BURN_TIME -> BasicGeneratorBlockEntity.this.maxBurnTime;
            default -> super.getData(index);
        };
    }

    @Override
    public void setData(int index, int value) {
        switch (index) {
            case DataIndex.ENERGY -> BasicGeneratorBlockEntity.this.energy.setEnergy(value);
            case DataIndex.MAX_ENERGY -> BasicGeneratorBlockEntity.this.energy.setMaxEnergyStored(value);
            case DataIndex.BURN_TIME -> BasicGeneratorBlockEntity.this.burnTime = value;
            case DataIndex.MAX_BURN_TIME -> BasicGeneratorBlockEntity.this.maxBurnTime = value;
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
        public static final int ENERGY = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES;
        public static final int MAX_ENERGY = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 1;
        public static final int BURN_TIME = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 2;
        public static final int MAX_BURN_TIME = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 3;
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
        return Component.translatable("block." + CrystalWorksMod.MOD_ID + ".basic_generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new BasicGeneratorMenu(i, inventory, this, this.data);
    }

    @Override
    public void onServerTick(Level level, BlockPos blockPos, BlockState blockState) {
        if(this.energy.getEnergyStored() < this.energy.getMaxEnergyStored()) {
            if(this.burnTime <= 0) {
                if(canBurn(this.inventory.getStackInSlot(INPUT_SLOT))) {
                    this.burnTime = this.maxBurnTime = getBurnTime(this.inventory.getStackInSlot(0));
                    this.inventory.getStackInSlot(INPUT_SLOT).shrink(1);
                    sendUpdate();
                }
            } else {
                this.burnTime --;
                this.energy.addEnergy(1);
                sendUpdate();
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("basicgenerator.maxburntime", maxBurnTime);
        pTag.putInt("basicgenerator.burntime", burnTime);
        pTag.put("energy", this.energy.serializeNBT());

        super.saveAdditional(pTag);
    }

    public void load(CompoundTag pTag) { //Consider adding a specific mod tag to make sure that other mods don't try overriding this data
        super.load(pTag);
        energy.deserializeNBT(pTag.get("energy"));
        burnTime = pTag.getInt("basicgenerator.burntime");
        maxBurnTime = pTag.getInt("basicgenerator.maxburntime");
    }
}
