package cn.ksmcbrigade.si.mixin;

import cn.ksmcbrigade.si.ItemProjectile;
import cn.ksmcbrigade.si.ShootIt;
import cn.ksmcbrigade.si.network.NetworkMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.Predicate;

@Mixin(CrossbowItem.class)
public abstract class CrossbowMixin {

    @Inject(method = "getAllSupportedProjectiles",at = @At("RETURN"),cancellable = true)
    public void allSupported(CallbackInfoReturnable<Predicate<ItemStack>> cir){
        cir.setReturnValue((stack)->!stack.is(Items.BOW) && !stack.is(Items.CROSSBOW));
    }

    @Inject(method = "shootProjectile",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;getArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/projectile/AbstractArrow;",shift = At.Shift.BEFORE), cancellable = true)
    private static void shoot(Level p_40895_, LivingEntity p_40896_, InteractionHand p_40897_, ItemStack p_40898_, ItemStack p_40899_, float p_40900_, boolean p_40901_, float p_40902_, float p_40903_, float p_40904_, CallbackInfo ci){
        if(!(p_40899_.getItem() instanceof ArrowItem)){
            Projectile projectile = ShootIt.getProjectile(p_40899_,p_40895_,p_40896_);
            projectile.setOwner(p_40896_);

            if (p_40896_ instanceof CrossbowAttackMob crossbowattackmob) {
                crossbowattackmob.shootCrossbowProjectile(Objects.requireNonNull(crossbowattackmob.getTarget()), p_40898_, projectile, p_40904_);
            } else {
                Vec3 vec31 = p_40896_.getUpVector(1.0F);
                Quaternionf quaternionf = (new Quaternionf()).setAngleAxis(p_40904_ * ((float)Math.PI / 180F), vec31.x, vec31.y, vec31.z);
                Vec3 vec3 = p_40896_.getViewVector(1.0F);
                Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
                projectile.shoot(vector3f.x(), vector3f.y(), vector3f.z(), p_40902_, p_40903_);
            }

            p_40898_.hurtAndBreak(1, p_40896_, (p_40858_) -> p_40858_.broadcastBreakEvent(p_40897_));
            if(projectile instanceof ItemProjectile itemProjectile && p_40896_ instanceof ServerPlayer serverPlayer) ShootIt.CHANNEL.sendTo(new NetworkMessage(itemProjectile.getItemA()),serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            p_40895_.addFreshEntity(projectile);
            p_40895_.playSound(null, p_40896_.getX(), p_40896_.getY(), p_40896_.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, p_40900_);

            ci.cancel();
        }
    }

}
