package net.bigmangohead.crystalworks.util.serialization.trackedobject;

import net.bigmangohead.crystalworks.network.PacketHandler;
import net.bigmangohead.crystalworks.network.packet.server.CWBlockEntityUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;
import java.util.function.Supplier;

// This acts as a storage for any object that can be serialized
// into a nbt object. This is used to make calls for sending packets/
// saving data more simple. Each of the constructors represents a
// different type of container for a trackedobject, which is needed
// when executing sendUpdate()
public abstract class TrackedObject<T> {

    public T obj;
    protected final String key;
    protected final TrackedType trackedType;

    protected final TrackedMethod trackedMethod;

    //Used for block entity saving
    protected final Supplier<Level> level;
    protected final BlockPos blockPos;
    protected final boolean updateRedstone;

    // Constructor for block entities
    public TrackedObject(T obj, String key, TrackedType trackedType, Supplier<Level> level, BlockPos blockPos, Boolean updateRedstone) {
        this.obj = obj;
        this.key = key;
        this.trackedType = trackedType;

        this.trackedMethod = TrackedMethod.BLOCK_ENTITY;

        this.level = level;
        this.blockPos = blockPos;
        this.updateRedstone = updateRedstone;
    }

    public abstract void putInTag(CompoundTag nbt);

    public abstract void updateWithTag(CompoundTag nbt);

    // Sends packet updates and tells server to save changed data.
    public void sendUpdate(boolean sendRedstoneUpdate) {
        CompoundTag nbtToSend = new CompoundTag();
        putInTag(nbtToSend);
        switch (this.trackedMethod) {

            case BLOCK_ENTITY -> {
                if (this.trackedType == TrackedType.SAVE_AND_SYNC) {
                    PacketHandler.sendToPlayersTrackingBlock(this.level.get(), this.blockPos, new CWBlockEntityUpdatePacket(CWBlockEntityUpdatePacket.UpdateType.ADD_CW_DATA, this.blockPos, nbtToSend));
                }

                if (this.level.get() != null) {
                    if (sendRedstoneUpdate && this.updateRedstone) {
                        // Note that this is not very fine to do multiple times per tick, involves many immediate actions.
                        BlockEntity targetBlockEntity = this.level.get().getBlockEntity(this.blockPos);
                        if (targetBlockEntity != null) {
                            targetBlockEntity.setChanged();
                        }
                    } else {
                        // Note that this should be fine to do multiple times per tick, as all it really does is check that the chunk exists and then sets a flag to refer back to later.
                        this.level.get().blockEntityChanged(this.blockPos);
                    }
                }
            }

        }
    }

    public void sendUpdate() {
        sendUpdate(true);
    }

    public String getKey() {
        return key;
    }

    public TrackedType getTrackedType() {
        return this.trackedType;
    }
}
