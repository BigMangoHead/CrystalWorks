package net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations;

import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class TrackedEnum<T extends Enum<T>> extends TrackedObject<T> {

    public TrackedEnum(T obj, String key, TrackedType trackedType, Supplier<Level> level, BlockPos blockPos) {
        super(obj, key, trackedType, level, blockPos, false);
    }

    @Override
    public void putInTag(CompoundTag nbt) {
        nbt.putInt(key, obj.ordinal());
    }

    @Override
    public void updateWithTag(CompoundTag nbt) {
        obj = obj.getDeclaringClass().getEnumConstants()[nbt.getInt(key)];
    }

    @Override
    public void writeToByteBuffer(FriendlyByteBuf buf) {
        buf.writeEnum(obj);
    }

    @Override
    public void updateFromByteBuffer(FriendlyByteBuf buf) {
        obj = buf.readEnum(obj.getDeclaringClass());
    }
}
