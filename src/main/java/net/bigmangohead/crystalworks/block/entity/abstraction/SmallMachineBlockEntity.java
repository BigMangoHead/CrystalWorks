package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.util.energy.flux.FluxStorage;
import net.bigmangohead.crystalworks.util.energy.flux.FluxUtils;
import net.bigmangohead.crystalworks.util.energy.flux.RedstoneFluxStorage;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedInteger;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedSerializable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;

// Small Machine refers to any machine (takes an input to produce an output)
// That requires a crystal block below it.
public abstract class SmallMachineBlockEntity extends AbstractInventoryBlockEntity implements ISmallAttachableToCrystal {
    private static final int DEFAULT_PROCESS_COUNT = 2;

    protected TrackedInteger progress = new TrackedInteger(0, "progress", TrackedType.SAVE_AND_SYNC_ON_MENU);
    protected final int defaultMaxProgress = 78; //Represents total amount of ticks per recipe by default
    protected TrackedInteger maxProgress = new TrackedInteger(defaultMaxProgress, "maxprogress", TrackedType.SYNC_ON_MENU);

    protected TrackedObject<FluxStorage> flux;
    protected final LazyOptional<FluxStorage> fluxOptional;

    public SmallMachineBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);

        FluxStorage flux = new FluxStorage(1, 1000000, 100, 0, FluxUtils.getFluxTypes(),
                (a, b, c) -> this.flux.queueUpdate());
        this.flux = new TrackedSerializable<>(flux, "flux", TrackedType.SAVE_AND_SYNC_ALL_UPDATES, 10);
        this.fluxOptional = LazyOptional.of(() -> this.flux.obj);
    }

    @Override
    public void invalidateCaps() {
        getFluxOptional().invalidate();
        getEnergyOptional().invalidate();
        super.invalidateCaps();
    }

    @Override
    protected void registerTrackedObjects() {
        super.registerTrackedObjects();

        this.trackedObjectHandler.register(this.flux);
        this.trackedObjectHandler.register(this.progress);
        this.trackedObjectHandler.register(this.maxProgress);
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

    protected boolean hasProgressFinished() {
        return progress.obj >= maxProgress.obj;
    }
}
