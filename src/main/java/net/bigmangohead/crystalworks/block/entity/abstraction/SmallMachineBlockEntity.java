package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.util.energy.flux.FluxStorage;
import net.bigmangohead.crystalworks.util.energy.flux.FluxUtils;
import net.bigmangohead.crystalworks.util.energy.flux.RedstoneFluxStorage;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedInteger;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedSerializable;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

// Small Machine refers to any machine (takes an input to produce an output)
// That requires a crystal block below it.
public abstract class SmallMachineBlockEntity extends AbstractInventoryBlockEntity {
    private static final int DEFAULT_PROCESS_COUNT = 2;

    protected TrackedInteger progress = new TrackedInteger(0, "progress", TrackedType.SAVE_AND_SYNC_ON_MENU);
    protected final int defaultMaxProgress = 78; //Represents total amount of ticks per recipe by default
    protected int maxProgress = 78; //Represents total amount of ticks in a recipe after recipe modifier is applied

    protected final TrackedObject<FluxStorage> flux;
    protected final LazyOptional<FluxStorage> fluxOptional;

    public SmallMachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);

        FluxStorage flux = new FluxStorage(1, 1000000, 100, 0, FluxUtils.getFluxTypes());
        this.flux = new TrackedSerializable<>(flux, "flux", TrackedType.SAVE);
        this.fluxOptional = LazyOptional.of(() -> this.flux.obj);
    }

    @Override
    protected void registerTrackedObjects() {
        super.registerTrackedObjects();

        this.trackedObjectHandler.register(this.flux);
        this.trackedObjectHandler.register(this.progress);
    }

    @Override
    public int getMenuData(int index) {
        return switch (index) {
            case DataIndex.ENERGY -> SmallMachineBlockEntity.this.flux.obj.getForgeEnergyStorage().getEnergyStored();
            case DataIndex.MAX_ENERGY -> SmallMachineBlockEntity.this.flux.obj.getForgeEnergyStorage().getMaxEnergyStored();
            //case DataIndex.PROGRESS -> SmallMachineBlockEntity.this.progress.obj;
            //case DataIndex.MAX_PROGRESS -> SmallMachineBlockEntity.this.maxProgress;
            default -> super.getMenuData(index);
        };
    }

    @Override
    public void setMenuData(int index, int value) {
        switch (index) {
            case DataIndex.ENERGY -> SmallMachineBlockEntity.this.flux.obj.getForgeEnergyStorage().forceSetFlux(value);
            case DataIndex.MAX_ENERGY -> SmallMachineBlockEntity.this.flux.obj.getForgeEnergyStorage().forceSetCapacity(value);
            //case DataIndex.PROGRESS -> SmallMachineBlockEntity.this.progress.obj = value;
            //case DataIndex.MAX_PROGRESS -> SmallMachineBlockEntity.this.maxProgress = value;
        }
        super.setMenuData(index, value);
    }

    @Override
    public int getMenuDataCount() {
        return DataIndex.AMOUNT_OF_VALUES;
    }

    public static class DataIndex {
        public static final int AMOUNT_OF_VALUES = 2 + AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES;

        // Data index starts at previous final index
        public static final int ENERGY = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES;
        public static final int MAX_ENERGY = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 1;
        public static final int ENERGY_TYPE = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 2;
        //public static final int PROGRESS = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 3;
        //public static final int MAX_PROGRESS = AbstractInventoryBlockEntity.DataIndex.AMOUNT_OF_VALUES + 4;
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
