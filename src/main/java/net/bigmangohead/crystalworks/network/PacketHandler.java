package net.bigmangohead.crystalworks.network;

import net.bigmangohead.crystalworks.CrystalWorksMod;
import net.bigmangohead.crystalworks.network.packet.server.CWBlockEntityUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketHandler {
    private static int ID = 0;

    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CrystalWorksMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerPacketHandling() {

        INSTANCE.registerMessage(ID, CWBlockEntityUpdatePacket.class, CWBlockEntityUpdatePacket::encode, CWBlockEntityUpdatePacket::new,
                CWBlockEntityUpdatePacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));

    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer serverPlayer) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }

    public static void sendToPlayersInChunk(LevelChunk chunk, Object msg) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), msg);
    }

    public static void sendToPlayersTrackingBlock(Level level, BlockPos blockPos, Object msg) {
        sendToPlayersInChunk(level.getChunkAt(blockPos), msg);
    }
}
