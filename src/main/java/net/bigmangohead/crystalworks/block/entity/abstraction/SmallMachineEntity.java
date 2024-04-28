package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.block.entity.CrusherBlockEntity;
import net.bigmangohead.crystalworks.registery.ModFluxTypes;
import net.bigmangohead.crystalworks.util.energy.CustomEnergyStorage;
import net.bigmangohead.crystalworks.util.energy.flux.FluxStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Set;

// Small Machine refers to any machine (takes an input to produce an output)
// That requires a crystal block below it.
public abstract class SmallMachineEntity extends AbstractInventoryBlockEntity {
    private static final int DEFAULT_PROCESS_COUNT = 2;

    protected int progress = 0;
    protected final int defaultMaxProgress = 78; //Represents total amount of ticks per recipe by default
    protected int maxProgress = 78; //Represents total amount of ticks in a recipe after recipe modifier is applied

    protected final CustomEnergyStorage energy = new CustomEnergyStorage(1000000, 100, 0, 0);
    protected final LazyOptional<CustomEnergyStorage> energyOptional = LazyOptional.of(() -> this.energy);

    protected final FluxStorage flux = new FluxStorage(1, 1000000, 100, 0, Set.of(ModFluxTypes.DIAMOND));
    protected final LazyOptional<FluxStorage> fluxOptional = LazyOptional.of(() -> this.flux);

    public SmallMachineEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public int getData(int index) {
        return switch (index) {
            case DataIndex.ENERGY -> SmallMachineEntity.this.energy.getEnergyStored();
            case DataIndex.MAX_ENERGY -> SmallMachineEntity.this.energy.getMaxEnergyStored();
            case DataIndex.PROGRESS -> SmallMachineEntity.this.progress;
            case DataIndex.MAX_PROGRESS -> SmallMachineEntity.this.maxProgress;
            default -> super.getData(index);
        };
    }

    @Override
    public void setData(int index, int value) {
        switch (index) {
            case DataIndex.ENERGY -> SmallMachineEntity.this.energy.setEnergy(value);
            case DataIndex.MAX_ENERGY -> SmallMachineEntity.this.energy.setMaxEnergyStored(value);
            case DataIndex.PROGRESS -> SmallMachineEntity.this.progress = value;
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
    protected void saveData(CompoundTag pTag) {
        pTag.putInt("machine.progress", progress);
        pTag.put("energy", this.energy.serializeNBT());
        pTag.put("diamond_flux", this.flux.serializeNBT());

        super.saveData(pTag);
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);
        energy.deserializeNBT(nbt.get("energy"));
        progress = nbt.getInt("machine.progress");
        flux.deserializeNBT(nbt.get("diamond_flux"));
    }

}
