package net.bigmangohead.crystalworks.util.serialization.trackedobject.handler;

import net.bigmangohead.crystalworks.event.ServerEvents;
import net.bigmangohead.crystalworks.network.PacketHandler;
import net.bigmangohead.crystalworks.network.packet.server.CWBlockEntityUpdatePacket;
import net.bigmangohead.crystalworks.util.serialization.trackedobject.TrackedObject;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.function.Supplier;

public class TrackedObjectBEHandler extends TrackedObjectHandler {
    protected final Supplier<Level> level;
    protected final BlockPos blockPos;
    protected final ArrayList<TrackedObject<?>> trackedObjects = new ArrayList<>();


    protected ArrayList<TrackedObject<?>> queuedTrackedObjectUpdates;
    public boolean queuedUpdate = false;

    protected ArrayList<Player> playersInMenu = new ArrayList<>();


    public TrackedObjectBEHandler(Supplier<Level> level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
    }



    public void register(TrackedObject<?> trackedObject) {
        trackedObject.declareHandler(this);
        trackedObjects.add(trackedObject);
    }

    public void finishRegistration() {
        this.queuedTrackedObjectUpdates = new ArrayList<>(trackedObjects.size());
    }

    public void playerOpenedMenu(Player player) {
        playersInMenu.add(player);
        sendMenuData((ServerPlayer) player);
    }

    public void playerClosedMenu(Player player) {
        playersInMenu.remove(player);
    }



    public void updateTagForSave(CompoundTag nbt) {
        for (TrackedObject<?> trackedObject : this.trackedObjects) {
            if (trackedObject.getTrackedType().shouldSave()) {
                trackedObject.putInTag(nbt);
            }
        }
    }

    public void updateTagForSyncOnUpdate(CompoundTag nbt) {
        for (TrackedObject<?> trackedObject : this.trackedObjects) {
            if (trackedObject.getTrackedType().shouldSyncOnUpdate()) {
                trackedObject.putInTag(nbt);
            }
        }
    }

    public void updateFromTag(CompoundTag nbt) {
        for(TrackedObject<?> trackedObject : this.trackedObjects) {
            if (nbt.contains(trackedObject.getKey())) {
                trackedObject.updateWithTag(nbt);
            }
        }
    }

    public void sendMenuData(ServerPlayer player) {
        CompoundTag nbt = new CompoundTag();
        for (TrackedObject<?> trackedObject : trackedObjects) {
            if (trackedObject.getTrackedType().shouldSyncOnMenu()) {
                trackedObject.putInTag(nbt);
            }
        }
        PacketHandler.sendToPlayer(new CWBlockEntityUpdatePacket(CWBlockEntityUpdatePacket.UpdateType.ADD_CW_DATA, this.blockPos, nbt), player);
    }



    @Override
    public void queueUpdate(TrackedObject<?> trackedObject) {
        this.queuedTrackedObjectUpdates.add(trackedObject);
        this.queuedUpdate = true;
    }

    // TODO: Check that the syncing data stuff is working fully.
    public void sendQueuedUpdates() {
        CompoundTag tagForUpdateSync = new CompoundTag();
        CompoundTag tagForMenuSync = new CompoundTag();
        boolean queueBlockUpdate = false;
        boolean queueBlockUpdateWithoutRedstone = false;
        ArrayList<TrackedObject<?>> newQueuedTrackedObjectUpdates = new ArrayList<>(trackedObjects.size());


        // Iterate through tracked objects and see if they should be updated based on time in tick
        // Note that this currently just sends the update periodically in the tick -
        // this means a new update after a long delay could still be pushed back, rather than being
        // sent immediately. This might be nice to change, though it means packets will be lined up together
        for (TrackedObject<?> queuedTrackedObject : queuedTrackedObjectUpdates) {
            boolean processed = false;
            //System.out.println(queuedTrackedObject.getKey());

            if (queuedTrackedObject.getTrackedType().shouldSave()) {
                if (queuedTrackedObject.shouldUpdateRedstone()) {
                    queueBlockUpdate = true;
                } else {
                    queueBlockUpdateWithoutRedstone = true;
                }
                processed = true;
            }

            if (!playersInMenu.isEmpty() && queuedTrackedObject.getTrackedType().shouldSyncOnMenu()) {
                queuedTrackedObject.putInTag(tagForMenuSync);
                processed = true;
            }

            if (queuedTrackedObject.getTrackedType().shouldSyncOnUpdate() && ServerEvents.tick % queuedTrackedObject.getTicksBetweenCheckForSync() == 0) {
                queuedTrackedObject.putInTag(tagForUpdateSync);
                processed = true;
            }

            if (!processed) {
                newQueuedTrackedObjectUpdates.add(queuedTrackedObject);
            }
        }
        // Add tags for syncing to the tags for menu
        if (!playersInMenu.isEmpty()) {
            tagForMenuSync.merge(tagForUpdateSync);
        }

        // Update queuedTrackObjectUpdates to the new list
        this.queuedTrackedObjectUpdates = newQueuedTrackedObjectUpdates;
        if (newQueuedTrackedObjectUpdates.isEmpty()) {
            this.queuedUpdate = false;
        }

        // Run block update if required
        if (queueBlockUpdate) {
            BlockEntity targetBlockEntity = this.level.get().getBlockEntity(this.blockPos);
            if (targetBlockEntity != null) {
                targetBlockEntity.setChanged();
            }
        } else if (queueBlockUpdateWithoutRedstone) {
            this.level.get().blockEntityChanged(this.blockPos);
        }

        // Send update packets to players that need them, if required
        for (Player player : level.get().players()) {
            if (playersInMenu.contains(player) && !tagForMenuSync.isEmpty()) {
                PacketHandler.sendToPlayer(new CWBlockEntityUpdatePacket(CWBlockEntityUpdatePacket.UpdateType.ADD_CW_DATA, this.blockPos, tagForMenuSync), (ServerPlayer) player);
            } else if (!tagForUpdateSync.isEmpty()) {
                PacketHandler.sendToPlayer(new CWBlockEntityUpdatePacket(CWBlockEntityUpdatePacket.UpdateType.ADD_CW_DATA, this.blockPos, tagForUpdateSync), (ServerPlayer) player);
            }
        }
    }

    public TrackedObject<?> getTrackedObject(String key) {
        for (TrackedObject<?> trackedObject : this.trackedObjects) {
            if (trackedObject.getKey().equals(key)) {
                return trackedObject;
            }
        }
        return null;
    }
}
