package net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations;

import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class TrackedString extends TrackedObject<String> {

    public TrackedString(String string, String key, TrackedType trackedType, Supplier<Level> level, BlockPos blockPos) {
        super(string, key, trackedType, level, blockPos, false);
    }

    @Override
    public void putInTag(CompoundTag nbt) {
        nbt.putString(key, obj);
    }

    @Override
    public void updateWithTag(CompoundTag nbt) {
        obj = nbt.getString(key);
    }

    @Override
    public void writeToByteBuffer(FriendlyByteBuf buf) {
        buf.writeInt(obj.length());
        buf.writeCharSequence(obj, StandardCharsets.UTF_8);
    }

    @Override
    public void updateFromByteBuffer(FriendlyByteBuf buf) {
        int length = buf.readInt();
        buf.readCharSequence(length, StandardCharsets.UTF_8);
    }
}
