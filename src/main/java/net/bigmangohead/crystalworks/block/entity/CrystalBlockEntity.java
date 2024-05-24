package net.bigmangohead.crystalworks.block.entity;

import net.bigmangohead.crystalworks.block.block.CrusherBlock;
import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.block.entity.machine.CrusherBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.registery.ModCapabilities;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedEnum;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CrystalBlockEntity extends CWBlockEntity {

    protected enum AttachmentState {
        UNATTACHED,
        SINGLE_MACHINE
    }

    protected TrackedEnum<AttachmentState> attachmentState;
    protected TrackedPosition attachedBlockPosition;

    // While the supplier object will never be null, the attached block entity can be.
    // The supplier is used so that the attached block entity never has to be tracked,
    // it can just be grabbed whenever necessary, and any checks for grabbing it can be done on the fly.
    protected Supplier<CrusherBlockEntity> attachedBlockEntity = () -> {
        if (this.level != null && this.attachedBlockPosition.obj != null) {
            return (CrusherBlockEntity) this.level.getExistingBlockEntity(attachedBlockPosition.obj);
        } else {
            return null;
        }
    };

    public CrystalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GEM_BE.get(), pPos, pBlockState);

        this.attachmentState = new TrackedEnum<>(AttachmentState.UNATTACHED, "AttachmentState", TrackedType.SAVE);
        this.attachedBlockPosition = new TrackedPosition(null, "AttachedPosition", TrackedType.SAVE);

        finishCreation();
    }

    @Override
    protected void registerTrackedObjects() {
        super.registerTrackedObjects();

        this.trackedObjectHandler.register(this.attachmentState);
        this.trackedObjectHandler.register(this.attachedBlockPosition);
    }

    @Override
    public void drops() {

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        if(attachmentState.obj == AttachmentState.SINGLE_MACHINE) {
            CrusherBlockEntity blockEntity = this.attachedBlockEntity.get();
            System.out.println(attachedBlockEntity);
            if(blockEntity != null) {
                blockEntity.getEnergyOptional().invalidate();
                blockEntity.getFluxOptional().invalidate();
            }
        }

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if ((cap == ForgeCapabilities.ENERGY || cap == ModCapabilities.FLUX) && attachmentState.obj == AttachmentState.SINGLE_MACHINE) {
            CrusherBlockEntity attachedBlockEntity = this.attachedBlockEntity.get();
            if (attachedBlockEntity == null) return super.getCapability(cap, side);

            if (cap == ForgeCapabilities.ENERGY) {
                return attachedBlockEntity.getEnergyOptional().cast();
            } else {
                return attachedBlockEntity.getFluxOptional().cast();
            }
        }

        return super.getCapability(cap, side);
    }

    // Maybe switch to having the machine blocks force updating the gem blocks?
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide()) return;

        Block resultingBlock = level.getBlockState(neighborPos).getBlock();

        // Note: this could cause an issue if changing from attached -> attached in the same tick.
        if (attachmentState.obj == AttachmentState.UNATTACHED && neighborPos.equals(this.worldPosition.above()) && (resultingBlock instanceof CrusherBlock)) {
            this.attachmentState.obj = AttachmentState.SINGLE_MACHINE;
            this.attachedBlockPosition.obj = neighborPos;
            setChangedWithoutRedstoneCheck();
        }

        if (attachmentState.obj == AttachmentState.SINGLE_MACHINE && neighborPos.equals(attachedBlockPosition.obj) && !(resultingBlock instanceof CrusherBlock)) {
            this.attachmentState.obj = AttachmentState.UNATTACHED;
            this.attachedBlockPosition.obj = null;
            setChangedWithoutRedstoneCheck();
        }
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level.isClientSide()) return;

        Block attachableBlock = level.getBlockState(pos.above()).getBlock();

        if (attachableBlock instanceof CrusherBlock) {
            this.attachmentState.obj = AttachmentState.SINGLE_MACHINE;
            this.attachedBlockPosition.obj = pos.above();
            setChangedWithoutRedstoneCheck();
        }
    }
}
