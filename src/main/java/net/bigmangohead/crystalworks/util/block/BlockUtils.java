package net.bigmangohead.crystalworks.util.block;

import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockUtils {

    // CustomBlocks call this
    @Nullable
    public static <T extends BlockEntity, A extends CWBlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType, BlockEntityType<A> expectedBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, expectedBlockEntityType,
                (level, blockPos, blockState, blockEntity) -> blockEntity.tick(level, blockPos, blockState));
    }


    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker) {
        return pClientType == pServerType ? (BlockEntityTicker<A>) pTicker : null;
    }

}
