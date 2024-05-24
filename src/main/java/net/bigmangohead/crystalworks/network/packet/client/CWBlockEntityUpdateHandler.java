package net.bigmangohead.crystalworks.network.packet.client;

import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.network.packet.server.CWBlockEntityUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class CWBlockEntityUpdateHandler {
    private static Level level;

    public static void updateLevel(Level newLevel) {
        level = newLevel;
    }

    public static void handlePacket(CWBlockEntityUpdatePacket updatePacket, NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            BlockPos blockPos = updatePacket.getBlockPos();

            if (level != null && level.hasChunkAt(blockPos)) {
                BlockEntity blockEntity = level.getExistingBlockEntity(blockPos);
                level.getBlockEntity(blockPos);
                if (blockEntity instanceof CWBlockEntity cwBlockEntity) {
                    updateBlockEntity(cwBlockEntity, updatePacket);
                }
            }
        });
    }

    // Note that this currently doesn't update nested NBT data efficiently.
    private static void updateBlockEntity(CWBlockEntity blockEntity, CWBlockEntityUpdatePacket updatePacket) {
        CompoundTag blockNBTData = blockEntity.serializeNBT();

        switch (updatePacket.getUpdateType()) {
            case ADD_CW_DATA -> {

                // Add NBT data to the crystalworks tag
                CompoundTag CWNBTData = blockNBTData.getCompound("crystalworks");
                CompoundTag NBTDataToAdd = updatePacket.getNBTData();
                for (String key : NBTDataToAdd.getAllKeys()) {
                    CWNBTData.put(key, NBTDataToAdd.get(key));
                }
                blockNBTData.put("crystalworks", CWNBTData);


                blockEntity.updateSuperTag(blockNBTData);
                blockEntity.updateCWNBTData(NBTDataToAdd);
            }


        }
    }
}
