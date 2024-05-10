package net.bigmangohead.crystalworks.block.entity;

import net.bigmangohead.crystalworks.block.block.CrusherBlock;
import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.registery.ModCapabilities;
import net.bigmangohead.crystalworks.util.serialization.SerializationUtils;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedEnum;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations.TrackedPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalBlockEntity extends CWBlockEntity {

    protected enum AttachmentState {
        UNATTACHED,
        SINGLE_MACHINE
    }

    protected TrackedEnum<AttachmentState> attachmentState;
    protected TrackedPosition attachedBlockPosition;
    @Nullable
    protected CrusherBlockEntity attachedBlockEntity = null;

    public CrystalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GEM_BE.get(), pPos, pBlockState);

        this.attachmentState = new TrackedEnum<>(AttachmentState.UNATTACHED, "AttachmentState", TrackedType.SAVE, () -> this.level, this.worldPosition);
        this.attachedBlockPosition = new TrackedPosition(null, "AttachedPosition", TrackedType.SAVE, () -> this.level, this.worldPosition);
    }

    @Override
    protected void registerTrackedObjects() {
        super.registerTrackedObjects();

        this.trackedObjects.add(() -> this.attachmentState);
        this.trackedObjects.add(() -> this.attachedBlockPosition);
    }

    @Override
    public void drops() {

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        if(attachmentState.obj == AttachmentState.SINGLE_MACHINE) {
            CrusherBlockEntity blockEntity = this.attachedBlockEntity;
            if(blockEntity != null) {
                blockEntity.getEnergyOptional().invalidate();
                blockEntity.getFluxOptional().invalidate();
            }
        }

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if ((cap == ForgeCapabilities.ENERGY || cap == ModCapabilities.FLUX) && attachmentState.obj == AttachmentState.SINGLE_MACHINE) {
            CrusherBlockEntity attachedBlockEntity = this.attachedBlockEntity;
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
            this.attachedBlockEntity = (CrusherBlockEntity) level.getBlockEntity(neighborPos);
            setChangedWithoutRedstoneCheck();
        }

        if (attachmentState.obj == AttachmentState.SINGLE_MACHINE && neighborPos.equals(attachedBlockPosition.obj) && !(resultingBlock instanceof CrusherBlock)) {
            this.attachmentState.obj = AttachmentState.UNATTACHED;
            this.attachedBlockPosition.obj = null;
            this.attachedBlockEntity = null;
            setChangedWithoutRedstoneCheck();
        }
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (level.isClientSide()) return;

        Block attachableBlock = level.getBlockState(pos.above()).getBlock();

        if (attachableBlock instanceof CrusherBlock) {
            this.attachmentState.obj = AttachmentState.SINGLE_MACHINE;
            this.attachedBlockPosition.obj = pos.above();
            this.attachedBlockEntity = (CrusherBlockEntity) level.getBlockEntity(pos.above());
            setChangedWithoutRedstoneCheck();
        }
    }

    @Override
    public void loadServerData(CompoundTag nbt) {
        BlockPos oldAttachedPosition = this.attachedBlockPosition.obj;

        super.loadServerData(nbt);

        if (oldAttachedPosition != this.attachedBlockPosition.obj && this.attachedBlockPosition.obj != null && level != null) {
            this.attachedBlockEntity = (CrusherBlockEntity) this.level.getBlockEntity(this.attachedBlockPosition.obj);
        }
    }
}
