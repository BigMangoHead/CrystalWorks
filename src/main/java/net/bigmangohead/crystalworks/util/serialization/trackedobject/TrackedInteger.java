package net.bigmangohead.crystalworks.util.serialization.trackedobject;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class TrackedInteger extends TrackedObject<Integer> {

    public TrackedInteger(Integer integer, String key, TrackedType trackedType, Supplier<Level> level, BlockPos blockPos) {
        super(integer, key, trackedType, level, blockPos, false);
    }

    @Override
    public void putInTag(CompoundTag nbt) {
        nbt.putInt(key, obj);
    }

    @Override
    public void updateWithTag(CompoundTag nbt) {
        obj = nbt.getInt(key);
    }
}
