package net.bigmangohead.crystalworks.block.block;

import net.bigmangohead.crystalworks.block.block.abstraction.CWBlock;
import net.bigmangohead.crystalworks.block.entity.CrystalBlockEntity;
import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CrystalBlock extends CWBlock {
    public CrystalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof CrystalBlockEntity crystalBlockEntity)) throw new IllegalStateException("Block entity is missing at " + pPos);

        crystalBlockEntity.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);

        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston) {
        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (!(blockEntity instanceof CrystalBlockEntity crystalBlockEntity)) throw new IllegalStateException("Block entity is missing at " + pPos);

        crystalBlockEntity.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);

        super.onPlace(pState, pLevel, pPos, pOldState, pMovedByPiston);
    }

    @Nullable
    @Override
    public CWBlockEntity getNewBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CrystalBlockEntity(blockPos, blockState);
    }
}
