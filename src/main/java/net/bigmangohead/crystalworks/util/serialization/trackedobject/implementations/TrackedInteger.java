package net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations;

import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class TrackedInteger extends TrackedObject<Integer> {

    public TrackedInteger(Integer obj, String key, TrackedType trackedType, int ticksBetweenCheckForSync) {
        super(obj, key, trackedType, ticksBetweenCheckForSync, false);
    }

    public TrackedInteger(Integer obj, String key, TrackedType trackedType) {
        super(obj, key, trackedType, 1, false);
    }

    @Override
    public void putInTag(CompoundTag nbt) {
        nbt.putInt(key, obj);
    }

    @Override
    public void updateWithTag(CompoundTag nbt) {
        obj = nbt.getInt(key);
    }

    @Override
    public void writeToByteBuffer(FriendlyByteBuf buf) {
        buf.writeInt(obj);
    }

    @Override
    public void updateFromByteBuffer(FriendlyByteBuf buf) {
        obj = buf.readInt();
    }
}
