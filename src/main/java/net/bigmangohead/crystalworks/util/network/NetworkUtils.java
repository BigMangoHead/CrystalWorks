package net.bigmangohead.crystalworks.util.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraftforge.network.NetworkHooks;

import java.util.function.Consumer;

public class NetworkUtils {

    public static void openScreen(ServerPlayer player, MenuProvider containerSupplier, BlockPos pos, Consumer<FriendlyByteBuf> extraDataWriter) {
        NetworkHooks.openScreen(player, containerSupplier, (buf) -> {
            buf.writeBlockPos(pos);
            extraDataWriter.accept(buf);
        });
    }

}
