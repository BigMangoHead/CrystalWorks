package net.bigmangohead.crystalworks.block.block;

import net.bigmangohead.crystalworks.block.block.abstraction.CWFunctionalBlock;
import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.block.entity.machine.CrusherBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.util.block.BlockUtils;
import net.bigmangohead.crystalworks.util.network.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrusherBlock extends CWFunctionalBlock {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);

    public CrusherBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Nullable
    @Override
    public CWBlockEntity getNewBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CrusherBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult onServerUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CWBlockEntity blockEntity) {
        if(!(blockEntity instanceof CrusherBlockEntity)) {
            throw new IllegalStateException("Container provider is missing!");
        }
        NetworkUtils.openScreen(((ServerPlayer)player), (CrusherBlockEntity)blockEntity, pos, (buf) -> {});
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockUtils.getTicker(pLevel, pState, pBlockEntityType, ModBlockEntities.CRUSHER_BE.get());
    }
}
