package net.bigmangohead.crystalworks.block.block;

import net.bigmangohead.crystalworks.block.block.abstraction.CWFunctionalBlock;
import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.block.entity.machine.PlateFormerBlockEntity;
import net.bigmangohead.crystalworks.registery.ModBlockEntities;
import net.bigmangohead.crystalworks.util.block.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class PlateFormerBlock extends CWFunctionalBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public PlateFormerBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    // Define rotation and mirror commands, then register block state.
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected CWBlockEntity getNewBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PlateFormerBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult onServerUse(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit, CWBlockEntity blockEntity) {
        if(!(blockEntity instanceof PlateFormerBlockEntity)) {
            throw new IllegalStateException("Container provider is missing!");
        }
        NetworkHooks.openScreen((ServerPlayer) player, (PlateFormerBlockEntity) blockEntity, pos);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return BlockUtils.getTicker(pLevel, pState, pBlockEntityType, ModBlockEntities.PLATE_FORMER_BE.get());
    }

}
