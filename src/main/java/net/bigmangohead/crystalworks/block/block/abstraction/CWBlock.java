package net.bigmangohead.crystalworks.block.block.abstraction;

import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class CWBlock extends BaseEntityBlock {
    protected CWBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        CWBlockEntity blockEntity = getNewBlockEntity(blockPos, blockState);
        blockEntity.finishCreation();
        return blockEntity;
    }

    protected abstract CWBlockEntity getNewBlockEntity(BlockPos blockPos, BlockState blockState);
}
