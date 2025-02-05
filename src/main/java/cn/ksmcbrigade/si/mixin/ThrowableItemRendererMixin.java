package cn.ksmcbrigade.si.mixin;

import cn.ksmcbrigade.si.ItemProjectile;
import cn.ksmcbrigade.si.client.ClientNetworkHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownItemRenderer.class)
public abstract class ThrowableItemRendererMixin<T extends Entity & ItemSupplier> extends EntityRenderer<T> {

    @Unique
    private boolean shootit$itemProjectile;

    protected ThrowableItemRendererMixin(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Inject(method = "render",at = @At("HEAD"),remap = false)
    public void render(T p_116085_, float p_116086_, float p_116087_, PoseStack p_116088_, MultiBufferSource p_116089_, int p_116090_, CallbackInfo ci){
        shootit$itemProjectile = p_116085_ instanceof ItemProjectile;
    }

    @ModifyArg(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"),index = 0)
    public ItemStack render(ItemStack stack){
        return shootit$itemProjectile ? ClientNetworkHandler.stack : stack;
    }

}
