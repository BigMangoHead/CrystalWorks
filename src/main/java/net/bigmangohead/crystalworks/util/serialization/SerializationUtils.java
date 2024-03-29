package net.bigmangohead.crystalworks.util.serialization;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import javax.annotation.Nullable;

public class SerializationUtils {

    //Returns empty tag if position is null
    public static Tag serialize(BlockPos pos) {
        var positionData = new CompoundTag();
        if (pos == null) return positionData;
        positionData.putInt("x", pos.getX());
        positionData.putInt("y", pos.getY());
        positionData.putInt("z", pos.getZ());
        return positionData;
    }

    @Nullable
    public static BlockPos deserializeBlockPos(CompoundTag tag) {
        if (tag.contains("x") && tag.contains("y") && tag.contains("z")) {
            return new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        }
        return null;
    }



}
