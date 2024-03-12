package net.bigmangohead.crystalworks.block.abstraction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleTickingBlock extends BaseEntityBlock {
    public final BlockEntityType<? extends BaseBlockEntity> blockEntityType;

    protected SimpleTickingBlock(Properties pProperties, BlockEntityType<? extends BaseBlockEntity> blockEntityType) {
        super(pProperties);
        this.blockEntityType = blockEntityType;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, this.blockEntityType,
                (level, blockPos, blockState, blockEntity) -> blockEntity.tick(level, blockPos, blockState));
    }

    public abstract class
}
