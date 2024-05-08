package net.bigmangohead.crystalworks.network.packet.abstraction;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class CustomPacket {

    public abstract void encode(FriendlyByteBuf friendlyByteBuf);

    public abstract void handle(Supplier<NetworkEvent.Context> contextSupplier);

}
