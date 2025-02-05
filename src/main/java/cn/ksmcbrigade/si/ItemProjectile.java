package cn.ksmcbrigade.si;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static cn.ksmcbrigade.si.ShootIt.TYPE_REGISTRY_OBJECT;

public class ItemProjectile extends ThrowableItemProjectile {

    private ItemStack st = ItemStack.EMPTY;
    private float damage = 4;
    private Entity entity;
    private Block block;

    public ItemProjectile(Level p_37399_, LivingEntity p_37400_) {
        super(TYPE_REGISTRY_OBJECT.get(), p_37400_, p_37399_);
    }

    public ItemProjectile(Level p_37394_, ItemStack itemStack, @Nullable Entity entity, @Nullable Block block,LivingEntity owner) {
        super(TYPE_REGISTRY_OBJECT.get(), owner.getX(),owner.getY(),owner.getZ(), p_37394_);
        this.setOwner(owner);
        this.setItemA(itemStack);
        this.entity = entity;
        this.block = block;
    }

    public ItemProjectile(EntityType<ItemProjectile> itemProjectileEntityType, Level level) {
        super(itemProjectileEntityType,level);
    }

    protected @NotNull Item getDefaultItem() {
        return this.st.getItem();
    }

    @Override
    protected ItemStack getItemRaw() {
        return this.st;
    }

    protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {
        if(entityHitResult.getEntity() instanceof LivingEntity living){
            living.hurt(living.damageSources().thrown(this,this.getOwner()),this.damage);
        }
    }

    
    public void setAddDamage(float damage) {
        this.damage+=damage;
    }

    
    public float getDamage() {
        return this.damage;
    }

    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    
    public Entity getEntity() {
        return this.entity;
    }

    
    public void setItemA(@NotNull ItemStack stack){
        this.st = stack;
        this.setItem(this.st);
    }
    
    public ItemStack getItemA(){
        return this.st;
    }

    public void setBlock(@NotNull Block block){
        this.block = block;
    }

    public Block getBlock(){
        return this.block;
    }

    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        if(this.entity!=null){
            this.entity.setPos(hitResult.getLocation());
            if(this.entity instanceof Projectile projectile) projectile.setOwner(this.getOwner());
            this.level().addFreshEntity(this.entity);
        }
        BlockPos pos = new BlockPos((int) hitResult.getLocation().x, (int) hitResult.getLocation().y, (int) hitResult.getLocation().z).above();
        if(this.block!=null && this.level().getBlockState(pos).isAir()){
            this.level().setBlockAndUpdate(pos,this.block.defaultBlockState());
        }
        this.discard();
    }

    @Override
    public String toString() {
        return "ItemProjectile{" +
                "st=" + st +
                ", damage=" + damage +
                ", entity=" + entity +
                ", block=" + block +
                '}';
    }
}
