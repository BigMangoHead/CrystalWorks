package net.bigmangohead.crystalworks.network.packet.server;

import net.bigmangohead.crystalworks.network.packet.abstraction.CustomPacket;
import net.bigmangohead.crystalworks.network.packet.client.CWBlockEntityUpdateHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CWBlockEntityUpdatePacket extends CustomPacket {

    private final UpdateType updateType;
    private final BlockPos blockPos;
    private final CompoundTag nbt;

    public CWBlockEntityUpdatePacket(UpdateType updateType, BlockPos blockPos, CompoundTag nbt) {
        this.updateType = updateType;
        this.blockPos = blockPos;
        this.nbt = nbt;
    }

    public CWBlockEntityUpdatePacket(FriendlyByteBuf buffer) {
        // Note that the order here MUST match the encoding order.
        this(buffer.readEnum(UpdateType.class), buffer.readBlockPos(), buffer.readNbt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(updateType);
        buffer.writeBlockPos(blockPos);
        buffer.writeNbt(nbt);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CWBlockEntityUpdateHandler.handlePacket(this, contextSupplier.get()));
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public CompoundTag getNBTData() {
        return nbt;
    }

    public enum UpdateType {
        ADD_CW_DATA
    }
}
