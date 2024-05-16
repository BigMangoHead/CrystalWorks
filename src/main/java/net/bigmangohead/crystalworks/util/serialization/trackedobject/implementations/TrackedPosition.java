package net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations;

import net.bigmangohead.crystalworks.util.serialization.SerializationUtils;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class TrackedPosition extends TrackedObject<BlockPos>  {
    public TrackedPosition(BlockPos obj, String key, TrackedType trackedType, Supplier<Level> level, BlockPos blockPos) {
        super(obj, key, trackedType, level, blockPos, false);
    }

    @Override
    public void putInTag(CompoundTag nbt) {
        nbt.put(key, SerializationUtils.serialize(obj));
    }

    @Override
    public void updateWithTag(CompoundTag nbt) {
        obj = SerializationUtils.deserializeBlockPos(nbt.getCompound(key));
    }

    @Override
    public void writeToByteBuffer(FriendlyByteBuf buf) {
        buf.writeBlockPos(obj);
    }

    @Override
    public void updateFromByteBuffer(FriendlyByteBuf buf) {
        obj = buf.readBlockPos();
    }
}
