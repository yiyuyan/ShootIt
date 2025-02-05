package cn.ksmcbrigade.si.client;

import cn.ksmcbrigade.si.ShootIt;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ShootIt.MODID,value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ShootIt.TYPE_REGISTRY_OBJECT.get(), ThrownItemRenderer::new);
        ShootIt.LOGGER.info("Entity Renderer registered.");
    }
}
