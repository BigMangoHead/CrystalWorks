package net.bigmangohead.crystalworks.block.entity.abstraction;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Supplier;

public abstract class CWBlockEntity extends BlockEntity {

    protected final ContainerData data;
    protected final ArrayList<Supplier<TrackedObject<?>>> trackedObjects;

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

        this.trackedObjects = new ArrayList<>();
        registerTrackedObjects();
    }

    public static class DataIndex {
        public static final int AMOUNT_OF_VALUES = 0;
    }

    public int getData(int index) {
        return 0;
    }

    // TODO: Validate data for setData function
    public void setData(int index, int value) {

    }

    public int getDataCount() {
        return DataIndex.AMOUNT_OF_VALUES;
    }

    public void setChangedWithoutRedstoneCheck() {
        if(this.level != null) {
            this.level.blockEntityChanged(this.worldPosition);
        }
    }

    public void sendUpdate() {
        setChanged();

        if(this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    public abstract void drops();



    protected void registerTrackedObjects() {

    }

    protected void saveData(CompoundTag nbt) {
        for (Supplier<TrackedObject<?>> trackedObject : this.trackedObjects) {
            trackedObject.get().putInTag(nbt);
        }
    }

    protected void loadData(CompoundTag nbt) {
        for(Supplier<TrackedObject<?>> trackedObject : this.trackedObjects) {
            if (nbt.contains(trackedObject.get().getKey())) {
                trackedObject.get().updateWithTag(nbt);
            }
        }
    }

    protected void clientboundOnChunkLoad(CompoundTag nbt) {
        saveData(nbt);
    }

    protected void clientboundOnBlockUpdate(CompoundTag nbt) {
        saveData(nbt);
    }

    protected void receiveDataOnChunkLoad(CompoundTag nbt) {
        loadData(nbt);
    }

    protected void receiveDataOnBlockUpdate(CompoundTag nbt) {
        loadData(nbt);
    }



    //saveAdditional and load store data on the server side
    @Override
    protected void saveAdditional(CompoundTag pTag) {
        CompoundTag modNBT = new CompoundTag();
        saveData(modNBT);
        pTag.put(CrystalWorksMod.MOD_ID, modNBT);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag nbt) {
        CompoundTag modNBT = nbt.getCompound(CrystalWorksMod.MOD_ID);
        loadData(modNBT);

        super.load(nbt);
    }


    //getUpdateTag and handleUpdateTag occur on chunk load
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();

        CompoundTag modNBT = new CompoundTag();
        clientboundOnChunkLoad(modNBT);
        nbt.put(CrystalWorksMod.MOD_ID, modNBT);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag nbt) {
        super.handleUpdateTag(nbt);

        receiveDataOnChunkLoad(nbt.getCompound(CrystalWorksMod.MOD_ID));
    }

    //getUpdatePacket and onDataPacket occur on block update, getOnBlockUpdateTag is a helper function for that.
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, (unused) -> this.getOnBlockUpdateTag());
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag nbt = pkt.getTag();
        if (nbt != null) {
            super.load(nbt);
            receiveDataOnBlockUpdate(nbt.getCompound(CrystalWorksMod.MOD_ID));
        }
    }

    public CompoundTag getOnBlockUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();

        CompoundTag modNBT = new CompoundTag();
        clientboundOnBlockUpdate(modNBT);
        nbt.put(CrystalWorksMod.MOD_ID, modNBT);
        return nbt;
    }

    // updateSuperTag and updateCWTag handle results from packets.
    // Note that updateSuperTag overrides default NBT data,
    // while updateCWTag only replace data given by the newCWData tag.
    public void updateSuperTag(CompoundTag newNBTData) {
        if (newNBTData != null) {
            super.load(newNBTData);
        }
    }

    public void updateCWNBTData(CompoundTag nbt) {
        loadData(nbt);
    }


    //Gives a default state for the tick function so that the function can be assumed to exist when a ticker is created
    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (this.level != null && !(this.level.isClientSide())) {
            onServerTick(level, blockPos, blockState);
        }
    }

    public void onServerTick(Level level, BlockPos blockPos, BlockState blockState) {

    }
}
