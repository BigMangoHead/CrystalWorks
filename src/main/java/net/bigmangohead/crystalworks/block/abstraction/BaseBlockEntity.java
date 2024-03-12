package net.bigmangohead.crystalworks.block.abstraction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BaseBlockEntity extends BlockEntity {
    public BaseBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    //Gives a default state for the tick function so that it can be called in SimpleTickingBlock.java
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {}
}
