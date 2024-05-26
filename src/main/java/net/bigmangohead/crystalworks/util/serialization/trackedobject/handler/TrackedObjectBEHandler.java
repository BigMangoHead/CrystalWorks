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

    // Will be the same length as the tracked objects, storing whether
    // each object is awaiting an update.
    protected ArrayList<HandlerProcessState> queuedTrackedObjectUpdates;
    public boolean queuedUpdate = false;

    protected ArrayList<Player> playersInMenu = new ArrayList<>();


    public TrackedObjectBEHandler(Supplier<Level> level, BlockPos blockPos) {
        this.level = level;
        this.blockPos = blockPos;
    }


    // Register tracked objects and then store them in flaggable tracked objects if flaggable.
    public void register(TrackedObject<?> trackedObject) {
        trackedObject.declareHandler(this, trackedObjects.size());
        trackedObjects.add(trackedObject);
    }

    public void finishRegistration() {
        this.queuedTrackedObjectUpdates = new ArrayList<>(trackedObjects.size());
        for (int i = 0; i < trackedObjects.size(); i++) {
            this.queuedTrackedObjectUpdates.add(HandlerProcessState.NONE);
        }
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
        this.queuedTrackedObjectUpdates.set(trackedObject.getHandlerIndex(), HandlerProcessState.ALL);
        this.queuedUpdate = true;
    }

    // TODO: Check that the syncing data stuff is working fully (currently most worried about the on-update things)
    public void sendQueuedUpdates() {
        CompoundTag tagForUpdateSync = new CompoundTag();
        CompoundTag tagForMenuSync = new CompoundTag();
        boolean queueBlockUpdate = false;
        boolean queueBlockUpdateWithoutRedstone = false;
        boolean doProcessesRemain = false;


        // Iterate through tracked objects and see if they should be updated based on time in tick
        // Note that this currently just sends the update periodically in the tick -
        // this means a new update after a long delay could still be pushed back, rather than being
        // sent immediately. This might be nice to change, though it means packets will be lined up together
        for (int i = 0; i < trackedObjects.size(); i++) {
            if (queuedTrackedObjectUpdates.get(i) == HandlerProcessState.NONE) {
                continue;
            }

            TrackedObject<?> queuedTrackedObject = trackedObjects.get(i);
            HandlerProcessState newProcessState = HandlerProcessState.ALL;

            if (queuedTrackedObjectUpdates.get(i) == HandlerProcessState.ALL && queuedTrackedObject.getTrackedType().shouldSave()) {
                if (queuedTrackedObject.shouldUpdateRedstone()) {
                    queueBlockUpdate = true;
                } else {
                    queueBlockUpdateWithoutRedstone = true;
                }
                newProcessState = HandlerProcessState.NONE;
            }

            // Note that the processed tag must be removed if syncing has not occurred yet
            // This means saving could occur multiple times over while waiting to sync - may want to fix this
            if (queuedTrackedObject.getTrackedType().shouldSyncOnUpdate()) {
                if (ServerEvents.tick % queuedTrackedObject.getTicksBetweenCheckForSync() == 0) {
                    queuedTrackedObject.putInTag(tagForUpdateSync);
                    newProcessState = HandlerProcessState.NONE;
                } else {
                    newProcessState = HandlerProcessState.TO_SYNC;
                }
            }

            if (!playersInMenu.isEmpty() && queuedTrackedObject.getTrackedType().shouldSyncOnMenu()) {
                queuedTrackedObject.putInTag(tagForMenuSync);
                newProcessState = HandlerProcessState.NONE;
            }

            if (newProcessState != HandlerProcessState.NONE) {
                doProcessesRemain = true;
            }
            queuedTrackedObjectUpdates.set(i, newProcessState);
        }
        this.queuedUpdate = doProcessesRemain;

        // Add tags for syncing to the tags for menu
        if (!playersInMenu.isEmpty()) {
            tagForMenuSync.merge(tagForUpdateSync);
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
