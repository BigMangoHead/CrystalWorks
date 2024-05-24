package net.bigmangohead.crystalworks.util.serialization.trackedobject;

import net.bigmangohead.crystalworks.util.serialization.trackedobject.handler.TrackedObjectHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

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

    // Note that this value does not matter if syncing is disabled for the particular tracked object.
    private final int ticksBetweenCheckForSync;

    protected TrackedObjectHandler handler;

    // Used for block entity updates
    protected final boolean updateRedstone;

    // Constructor for block entities
    public TrackedObject(T obj, String key, TrackedType trackedType, int ticksBetweenCheckForSync, Boolean updateRedstone) {
        this.obj = obj;
        this.key = key;
        this.trackedType = trackedType;
        this.ticksBetweenCheckForSync = ticksBetweenCheckForSync;

        this.trackedMethod = TrackedMethod.BLOCK_ENTITY;

        this.updateRedstone = updateRedstone;
    }

    public void declareHandler(TrackedObjectHandler handler) {
        this.handler = handler;
    }

    public abstract void putInTag(CompoundTag nbt);

    public abstract void updateWithTag(CompoundTag nbt);

    // TODO: Consider making syncing data more efficient by changing out nbt tags.
    // Could also maybe use shortened nbt tags or something. I'd need to look
    // into the nbt tag code to see if this would be more efficient though.
    public abstract void writeToByteBuffer(FriendlyByteBuf buf);

    public abstract void updateFromByteBuffer(FriendlyByteBuf buf);

    // Sends packet updates and tells server to save changed data.
    public void queueUpdate() {
        handler.queueUpdate(this);
    }

    public String getKey() {
        return key;
    }

    public TrackedType getTrackedType() {
        return this.trackedType;
    }

    public int getTicksBetweenCheckForSync() {
        return ticksBetweenCheckForSync;
    }

    public boolean shouldUpdateRedstone() {
        return this.updateRedstone;
    }
}
