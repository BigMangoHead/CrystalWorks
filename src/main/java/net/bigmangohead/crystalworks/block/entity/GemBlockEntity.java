package net.bigmangohead.crystalworks.block.entity;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.util.serialization.SerializationUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GemBlockEntity extends BlockEntity {
    protected int attachmentState;

    protected static class attachmentStates {
        public static final int UNATTACHED = 0;
        public static final int SINGLE_MACHINE = 1;
    }

    protected BlockPos attachedBlockPosition;
    protected CrusherBlockEntity attachedSingleMachine;

    public GemBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GEM_BE.get(), pPos, pBlockState);
    }

    public void sendUpdate() { //TEMP
        setChanged();

        if(this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        CrystalWorksMod.LOGGER.info("test!");
        return super.getUpdatePacket();
    }

    protected void saveAdditional(CompoundTag pTag) {
        pTag.putInt("gemblock.attachmentState", this.attachmentState);
        pTag.put("gemblock.attachedBlockPosition", SerializationUtils.serialize(attachedBlockPosition));

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) { //Consider adding a specific mod tag to make sure that other mods don't try overriding this data
        super.load(pTag);

        this.attachmentState = pTag.getInt("gemBlock.attachmentState");
        this.attachedBlockPosition = SerializationUtils.deserialize(pTag.getCompound("gemBlock.attachedBlockPosition"));

        if (attachmentState == attachmentStates.SINGLE_MACHINE) {
            this.attachedSingleMachine = (CrusherBlockEntity) this.level.getBlockEntity(attachedBlockPosition);
        }
    }
}
