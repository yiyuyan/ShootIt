package cn.ksmcbrigade.si.mixin;

import cn.ksmcbrigade.si.ItemProjectile;
import cn.ksmcbrigade.si.ShootIt;
import cn.ksmcbrigade.si.network.NetworkMessage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(BowItem.class)
public abstract class BowMixin {

    @Unique
    private ItemStack shootit$itemStack;

    @Shadow
    public static float getPowerForTime(int p_40662_) {
        return 0;
    }

    @Shadow public abstract int getUseDuration(ItemStack p_40680_);

    @Inject(method = "getAllSupportedProjectiles",at = @At("RETURN"),cancellable = true)
    public void allSupported(CallbackInfoReturnable<Predicate<ItemStack>> cir){
        cir.setReturnValue((stack)->!stack.is(Items.BOW) && !stack.is(Items.CROSSBOW));
    }

    @Redirect(method = "releaseUsing",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ArrowItem;createArrow(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/entity/projectile/AbstractArrow;"))
    public AbstractArrow releaseUsing(ArrowItem instance, Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_){
        this.shootit$itemStack = p_40514_;
        return instance.createArrow(p_40513_, p_40514_, p_40515_);
    }

    @Inject(method = "releaseUsing",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BowItem;customArrow(Lnet/minecraft/world/entity/projectile/AbstractArrow;)Lnet/minecraft/world/entity/projectile/AbstractArrow;",shift = At.Shift.BEFORE), cancellable = true)
    public void release(ItemStack p_40667_, Level p_40668_, LivingEntity p_40669_, int p_40670_, CallbackInfo ci){
        if(!(p_40667_.getItem() instanceof ArrowItem)){
            Projectile projectile = ShootIt.getProjectile(this.shootit$itemStack,p_40668_,p_40669_);
            projectile.setOwner(p_40669_);
            projectile.setPos(p_40669_.position().add(0,1,0));
            float f = getPowerForTime(this.getUseDuration(p_40667_)-p_40670_);
            projectile.shootFromRotation(p_40669_, p_40669_.getXRot(), p_40669_.getYRot(), 0.0F, f * 3.0F, 1.0F);
            if(projectile instanceof ItemProjectile itemProjectile){
                itemProjectile.setAddDamage(EnchantmentHelper.getTagEnchantmentLevel(Enchantments.POWER_ARROWS, p_40667_));
            }
            if(projectile instanceof ItemProjectile itemProjectile && p_40669_ instanceof ServerPlayer serverPlayer) ShootIt.CHANNEL.sendTo(new NetworkMessage(itemProjectile.getItemA()),serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            projectile.level().addFreshEntity(projectile);

            Player player = (Player)p_40669_;

            p_40668_.playSound(null, p_40669_.getX(), p_40669_.getY(), p_40669_.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (p_40668_.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            if (!(player.getAbilities().instabuild || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY_ARROWS,p_40669_)>0) && !player.getAbilities().instabuild) {
                this.shootit$itemStack.shrink(1);
                if (this.shootit$itemStack.isEmpty()) {
                    ((Player)p_40669_).getInventory().removeItem(this.shootit$itemStack);
                }
            }

            player.awardStat(Stats.ITEM_USED.get((BowItem)((Object)this)));
            ci.cancel();
        }
    }

}
