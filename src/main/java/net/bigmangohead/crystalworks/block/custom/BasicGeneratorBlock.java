package net.bigmangohead.crystalworks.block.custom;

import net.bigmangohead.crystalworks.block.entity.BasicGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BasicGeneratorBlock extends BaseEntityBlock {

    public BasicGeneratorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BasicGeneratorBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand hand, BlockHitResult pHit) {
        if (!pLevel.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(!(entity instanceof BasicGeneratorBlockEntity)) {
                throw new IllegalStateException("Our Container provider is missing!");
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        return InteractionResult.SUCCESS;
    }



}
