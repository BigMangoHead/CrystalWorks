package net.bigmangohead.crystalworks.item.custom;

import net.bigmangohead.crystalworks.block.CrystalBlocks;
import net.bigmangohead.crystalworks.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalFormerItem extends Item {
    public CrystalFormerItem(Properties pProperties) { super(pProperties); }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(!pContext.getLevel().isClientSide()) {
            BlockPos positionClicked = pContext.getClickedPos();
            Level level = pContext.getLevel();
            BlockState blockClicked = level.getBlockState(positionClicked);
            Player player = pContext.getPlayer();
            if (isMachine(blockClicked)) {
                if (isStructure(positionClicked, level)) {
                    level.setBlock(positionClicked.above(1), Blocks.DIAMOND_BLOCK.defaultBlockState(), 0);
                    player.sendSystemMessage(Component.literal("Structure formed!"));
                    return InteractionResult.SUCCESS;
                }
            }
            player.sendSystemMessage(Component.literal("Structure forming failed."));
        }

        return InteractionResult.FAIL;
    }

    private boolean isStructure(BlockPos positionClicked, Level level) {
        BlockPos startingPosition = positionClicked.below(1).east(1).north(1);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; y < 3; y++) {
                    if (!validBlock(startingPosition.below(y).west(x).south(z), level)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean validBlock(BlockPos blockPos, Level level) {
        BlockState block = level.getBlockState(blockPos);
        return (block.is(ModTags.Blocks.CRYSTAL_MATRIX_BLOCKS));
    }

    private boolean isMachine(BlockState block) {
        return (block.is(Blocks.FURNACE));
    }
}
