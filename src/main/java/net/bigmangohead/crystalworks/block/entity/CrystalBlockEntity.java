package net.bigmangohead.crystalworks.block.entity;

import net.bigmangohead.crystalworks.block.block.CrusherBlock;
import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.registery.ModCapabilities;
import net.bigmangohead.crystalworks.util.block.LevelUtils;
import net.bigmangohead.crystalworks.util.serialization.SerializationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrystalBlockEntity extends CWBlockEntity {
    protected AttachmentState attachmentState = AttachmentState.UNATTACHED;

    protected enum AttachmentState {
        UNATTACHED,
        SINGLE_MACHINE
    }

    protected BlockPos attachedBlockPosition = null;
    @Nullable
    protected CrusherBlockEntity attachedBlockEntity = null;

    public CrystalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GEM_BE.get(), pPos, pBlockState);
    }

    @Override
    public void drops() {

    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        if(attachmentState == AttachmentState.SINGLE_MACHINE) {
            CrusherBlockEntity blockEntity = this.attachedBlockEntity;
            if(blockEntity != null) {
                blockEntity.getEnergyOptional().invalidate();
                blockEntity.getFluxOptional().invalidate();
            }
        }

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if ((cap == ForgeCapabilities.ENERGY || cap == ModCapabilities.FLUX) && attachmentState == AttachmentState.SINGLE_MACHINE) {
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
    // TODO: Make attachment also trigger on placement of gem block
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide()) return;

        Block resultingBlock = level.getBlockState(neighborPos).getBlock();

        // Note: this could cause an issue if changing from attached -> attached in the same tick.
        if (attachmentState == AttachmentState.UNATTACHED && neighborPos.equals(this.worldPosition.above()) && (resultingBlock instanceof CrusherBlock)) {
            this.attachmentState = AttachmentState.SINGLE_MACHINE;
            this.attachedBlockPosition = neighborPos;
            this.attachedBlockEntity = (CrusherBlockEntity) level.getBlockEntity(neighborPos);
        }

        if (attachmentState == AttachmentState.SINGLE_MACHINE && neighborPos.equals(attachedBlockPosition) && !(resultingBlock instanceof CrusherBlock)) {
            this.attachmentState = AttachmentState.UNATTACHED;
            this.attachedBlockPosition = null;
            this.attachedBlockEntity = null;
        }
    }

    @Override
    protected void saveData(CompoundTag pTag) {
        pTag.putInt("attachmentstate", this.attachmentState.ordinal());
        pTag.put("attachedpos", SerializationUtils.serialize(attachedBlockPosition));

        super.saveData(pTag);
    }

    @Override
    public void loadData(CompoundTag nbt) {
        super.loadData(nbt);

        if (nbt.contains("attachmentstate")) this.attachmentState = AttachmentState.values()[nbt.getInt("attachmentstate")];
        if (nbt.contains("attachedpos")) this.attachedBlockPosition = SerializationUtils.deserializeBlockPos(nbt.getCompound("attachedpos"));
    }

    @Nullable
    private CrusherBlockEntity getAttachedBlockEntity() {
        if (this.level != null && this.attachmentState == AttachmentState.SINGLE_MACHINE) {
            if (this.attachedBlockPosition == null) throw new IllegalStateException("Attached block position for crystal cannot be null when attachment state is SINGLE_MACHINE!");

            // This safe method is used to guarantee that this method call does not create
            // a new block entity when getting it. In this case, this creates recursive calls.
            BlockEntity blockEntity = LevelUtils.getBlockEntitySafe(this.level, this.attachedBlockPosition);
            if (blockEntity == null) return null;

            return (CrusherBlockEntity) blockEntity;
        }
        return null;
    }
}
