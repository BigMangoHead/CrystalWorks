package net.bigmangohead.crystalworks.util.serialization.trackedobject.implementations;

import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Supplier;

public class TrackedSerializable<S extends Tag, T extends INBTSerializable<S>> extends TrackedObject<T> {

    public TrackedSerializable(T obj, String key, TrackedType trackedType, Supplier<Level> level, BlockPos blockPos, Boolean updateRedstone) {
        super(obj, key, trackedType, level, blockPos, updateRedstone);
    }

    public TrackedSerializable(T obj, String key, TrackedType trackedType, Supplier<Level> level, BlockPos blockPos) {
        super(obj, key, trackedType, level, blockPos, false);
    }

    @Override
    public void putInTag(CompoundTag nbt) {
        nbt.put(this.key, this.obj.serializeNBT());
    }

    @Override
    public void updateWithTag(CompoundTag nbt) {
        this.obj.deserializeNBT((S) nbt.get(this.key));
    }

    @Override
    public void writeToByteBuffer(FriendlyByteBuf buf) {
        buf.writeNbt((CompoundTag) obj.serializeNBT());
    }

    @Override
    public void updateFromByteBuffer(FriendlyByteBuf buf) {
        obj.deserializeNBT((S) buf.readNbt());
    }
}
