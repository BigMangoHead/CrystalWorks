package net.bigmangohead.crystalworks.block.block.abstraction;

import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class CWCustomBlock extends BaseEntityBlock {
    protected CWCustomBlock(Properties pProperties) {
        super(pProperties);
    }

    //This makes the block entity drop its inventory contents. If drops function is blank, this will do nothing
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CWBlockEntity) {
                ((CWBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand hand, BlockHitResult pHit) {
        if (pLevel.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }

        BlockEntity entity = pLevel.getBlockEntity(pPos);
        if(!(entity instanceof CWBlockEntity)) {
            throw new IllegalStateException("Container provider is missing!");
        }
        return onServerUse(pState, pLevel, pPos, pPlayer, pHit, (CWBlockEntity) entity);
    }

    public abstract InteractionResult onServerUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CWBlockEntity blockEntity);

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
