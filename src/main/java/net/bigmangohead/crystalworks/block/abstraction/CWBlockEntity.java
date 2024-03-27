package net.bigmangohead.crystalworks.block.abstraction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class CWBlockEntity extends BlockEntity {

    protected final ContainerData data;

    public CWBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return getData(i);
            }

            @Override
            public void set(int i, int i1) {
                setData(i, i1);
            }

            @Override
            public int getCount() {
                return getDataCount();
            }
        };
    }

    public static class DataIndex {
        public static final int AMOUNT_OF_VALUES = 0;
    }

    public int getData(int index) {
        return 0;
    };

    // TODO: Validate data for setData function
    public void setData(int index, int value) {

    };

    public int getDataCount() {
        return DataIndex.AMOUNT_OF_VALUES;
    };

    protected void sendUpdate() {
        setChanged();

        if(this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public abstract void drops();

    //Gives a default state for the tick function so that the function can be assumed to exist when a ticker is created
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.level != null && !(this.level.isClientSide())) {
            onServerTick(level, blockPos, blockState);
        }
    }

    public void onServerTick(Level level, BlockPos blockPos, BlockState blockState) {

    }
}
