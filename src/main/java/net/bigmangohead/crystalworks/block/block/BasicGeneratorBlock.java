package net.bigmangohead.crystalworks.block.block;

import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.block.block.abstraction.CWCustomBlock;
import net.bigmangohead.crystalworks.block.entity.BasicGeneratorBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.util.block.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BasicGeneratorBlock extends CWCustomBlock {

    public BasicGeneratorBlock(Properties pProperties) {
        super(pProperties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BasicGeneratorBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult onServerUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CWBlockEntity blockEntity) {
        if(!(blockEntity instanceof BasicGeneratorBlockEntity)) {
            throw new IllegalStateException("Container provider is missing!");
        }
        NetworkHooks.openScreen(((ServerPlayer)player), (BasicGeneratorBlockEntity)blockEntity, pos);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockUtils.getTicker(pLevel, pState, pBlockEntityType, ModBlockEntities.BASIC_GENERATOR_BE.get());
    }
}
