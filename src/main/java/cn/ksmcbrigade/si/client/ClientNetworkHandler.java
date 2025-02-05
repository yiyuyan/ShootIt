package cn.ksmcbrigade.si.client;

import cn.ksmcbrigade.si.network.NetworkMessage;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientNetworkHandler {

    public static ItemStack stack = ItemStack.EMPTY;

    public static void handle(NetworkMessage message){
        stack = message.stack();
    }
}
