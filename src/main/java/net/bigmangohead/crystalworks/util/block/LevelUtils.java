package net.bigmangohead.crystalworks.util.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nullable;

public class LevelUtils {

    // Only difference from level.getBlockEntity is that this prevents
    // creating a new block entity, which could cause recursive calls
    // or other unintended behavior.
    @Nullable
    public static BlockEntity getBlockEntitySafe(Level level, BlockPos pPos) {
        if (level.isOutsideBuildHeight(pPos)) {
            return null;
        } else {
            return !level.isClientSide ? null : level.getChunkAt(pPos).getBlockEntity(pPos, LevelChunk.EntityCreationType.CHECK);
        }
    }

}
