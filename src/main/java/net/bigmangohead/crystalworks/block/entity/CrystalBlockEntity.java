package net.bigmangohead.crystalworks.block.entity;

import net.bigmangohead.crystalworks.block.block.CrusherBlock;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
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

public class CrystalBlockEntity extends BlockEntity {
    protected int attachmentState = attachmentStates.UNATTACHED;

    protected static class attachmentStates {
        public static final int UNATTACHED = 0;
        public static final int SINGLE_MACHINE = 1;
    }

    protected BlockPos attachedBlockPosition = null;

    public CrystalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GEM_BE.get(), pPos, pBlockState);
    }

    protected void sendUpdate() {
        setChanged();

        if(this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if(attachmentState == attachmentStates.SINGLE_MACHINE) {
            CrusherBlockEntity blockEntity = getBlockEntity();
            blockEntity.getEnergyOptional().invalidate();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY && attachmentState == attachmentStates.SINGLE_MACHINE) {
            return getBlockEntity().getEnergyOptional().cast();
        }
        return super.getCapability(cap, side);
    }

    // Maybe switch to having the machine blocks force updating the gem blocks?
    // TODO: Make attachment also trigger on placement of gem block
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide()) return;

        Block resultingBlock = level.getBlockState(neighborPos).getBlock();

        // Note: this could cause an issue if changing from attached -> attached in the same tick.
        if (attachmentState == attachmentStates.UNATTACHED && neighborPos.equals(this.worldPosition.above()) && (resultingBlock instanceof CrusherBlock)) {
            this.attachmentState = attachmentStates.SINGLE_MACHINE;
            this.attachedBlockPosition = neighborPos;
        }

        if (attachmentState == attachmentStates.SINGLE_MACHINE && neighborPos.equals(attachedBlockPosition) && !(resultingBlock instanceof CrusherBlock)) {
            this.attachmentState = attachmentStates.UNATTACHED;
            this.attachedBlockPosition = null;
        }
    }

    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("crystalBlock.attachmentState", this.attachmentState);
        pTag.put("crystalBlock.attachedBlockPosition", SerializationUtils.serialize(attachedBlockPosition));

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) { //Consider adding a specific mod tag to make sure that other mods don't try overriding this data
        super.load(pTag);

        this.attachmentState = pTag.getInt("crystalBlock.attachmentState");
        this.attachedBlockPosition = SerializationUtils.deserializeBlockPos(pTag.getCompound("crystalBlock.attachedBlockPosition"));
    }

    @Nullable
    private CrusherBlockEntity getBlockEntity() {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.attachedBlockPosition);
            if (blockEntity == null) throw new IllegalStateException("Attached block for gem block at " + this.getBlockPos() + " does not exist!");
            return (CrusherBlockEntity) blockEntity;
        }
        return null;
    }
}
