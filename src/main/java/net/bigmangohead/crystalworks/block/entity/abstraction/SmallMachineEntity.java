package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.util.energy.CustomEnergyStorage;
import net.bigmangohead.crystalworks.util.energy.flux.FluxStorage;
import net.bigmangohead.crystalworks.util.energy.flux.FluxUtils;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedInteger;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedSerializable;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

// Small Machine refers to any machine (takes an input to produce an output)
// That requires a crystal block below it.
public abstract class SmallMachineEntity extends AbstractInventoryBlockEntity {
    private static final int DEFAULT_PROCESS_COUNT = 2;

    protected TrackedInteger progress = new TrackedInteger(0, "progress", TrackedType.SAVE, () -> this.level, this.worldPosition);
    protected final int defaultMaxProgress = 78; //Represents total amount of ticks per recipe by default
    protected int maxProgress = 78; //Represents total amount of ticks in a recipe after recipe modifier is applied

    protected final CustomEnergyStorage energy = new CustomEnergyStorage(1000000, 100, 0, 0);
    protected final LazyOptional<CustomEnergyStorage> energyOptional = LazyOptional.of(() -> this.energy);

    protected final TrackedObject<FluxStorage> flux;
    protected final LazyOptional<FluxStorage> fluxOptional;

    public SmallMachineEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);

        FluxStorage flux = new FluxStorage(1, 1000000, 100, 0, FluxUtils.getFluxTypes());
        this.flux = new TrackedSerializable<>(flux, "flux", TrackedType.SAVE, () -> this.level, this.worldPosition);
        this.fluxOptional = LazyOptional.of(() -> this.flux.obj);
    }

    @Override
    protected void registerTrackedObjects() {
        super.registerTrackedObjects();

        this.trackedObjects.add(() -> this.flux);
    }

    @Override
    public int getData(int index) {
        return switch (index) {
            case DataIndex.ENERGY -> SmallMachineEntity.this.energy.getEnergyStored();
            case DataIndex.MAX_ENERGY -> SmallMachineEntity.this.energy.getMaxEnergyStored();
            case DataIndex.PROGRESS -> SmallMachineEntity.this.progress.obj;
            case DataIndex.MAX_PROGRESS -> SmallMachineEntity.this.maxProgress;
            default -> super.getData(index);
        };
    }

    @Override
    public void setData(int index, int value) {
        switch (index) {
            case DataIndex.ENERGY -> SmallMachineEntity.this.energy.setEnergy(value);
            case DataIndex.MAX_ENERGY -> SmallMachineEntity.this.energy.setMaxEnergyStored(value);
            case DataIndex.PROGRESS -> SmallMachineEntity.this.progress.obj = value;
            case DataIndex.MAX_PROGRESS -> SmallMachineEntity.this.maxProgress = value;
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
        public static final int PROGRESS = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 2;
        public static final int MAX_PROGRESS = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 3;
    }

    public LazyOptional<CustomEnergyStorage> getEnergyOptional() {
        return this.energyOptional;
    }

    public LazyOptional<FluxStorage> getFluxOptional() {
        return this.fluxOptional;
    }

    public CustomEnergyStorage getEnergy() {
        return this.energy;
    }

    @Override
    protected void saveData(CompoundTag nbt) {
        nbt.put("energy", this.energy.serializeNBT());

        super.saveData(nbt);
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);

        if (nbt.contains("energy")) energy.deserializeNBT(nbt.get("energy"));
    }

}
