package cn.ksmcbrigade.si.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record NetworkMessage(ItemStack stack) {
    public static void encode(NetworkMessage msg, FriendlyByteBuf buf){
        buf.writeItem(msg.stack);
    }

    public static NetworkMessage decode(FriendlyByteBuf buf){
        return new NetworkMessage(buf.readItem());
    }
}
