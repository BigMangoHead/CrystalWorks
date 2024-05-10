package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.util.energy.flux.FluxStorage;
import net.bigmangohead.crystalworks.util.energy.flux.FluxUtils;
import net.bigmangohead.crystalworks.util.energy.flux.RedstoneFluxStorage;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedInteger;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedSerializable;
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
        this.trackedObjects.add(() -> this.progress);
    }

    @Override
    public int getData(int index) {
        return switch (index) {
            case DataIndex.ENERGY -> SmallMachineEntity.this.flux.obj.getForgeEnergyStorage().getEnergyStored();
            case DataIndex.MAX_ENERGY -> SmallMachineEntity.this.flux.obj.getForgeEnergyStorage().getMaxEnergyStored();
            case DataIndex.PROGRESS -> SmallMachineEntity.this.progress.obj;
            case DataIndex.MAX_PROGRESS -> SmallMachineEntity.this.maxProgress;
            default -> super.getData(index);
        };
    }

    @Override
    public void setData(int index, int value) {
        switch (index) {
            case DataIndex.ENERGY -> SmallMachineEntity.this.flux.obj.getForgeEnergyStorage().forceSetFlux(value);
            case DataIndex.MAX_ENERGY -> SmallMachineEntity.this.flux.obj.getForgeEnergyStorage().forceSetCapacity(value);
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

    public LazyOptional<RedstoneFluxStorage> getEnergyOptional() {
        return this.flux.obj.getOptionalForgeEnergyStorage();
    }

    public LazyOptional<FluxStorage> getFluxOptional() {
        return this.fluxOptional;
    }

    public RedstoneFluxStorage getEnergy() {
        return this.flux.obj.getForgeEnergyStorage();
    }
}
