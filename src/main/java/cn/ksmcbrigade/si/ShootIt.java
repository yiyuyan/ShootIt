package cn.ksmcbrigade.si;

import cn.ksmcbrigade.si.client.ClientNetworkHandler;
import cn.ksmcbrigade.si.network.NetworkMessage;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ShootIt.MODID)
public class ShootIt {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "si";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,MODID);
    public static final RegistryObject<EntityType<ItemProjectile>> TYPE_REGISTRY_OBJECT = ENTITY_TYPE_DEFERRED_REGISTER.register("item_projectile",
            ()->EntityType.Builder.<ItemProjectile>of(ItemProjectile::new, MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10)
                    .build(new ResourceLocation(MODID,"item_projectile").toString()));

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID,"sync"),()->"340",(s)->true,(s)->true);

    public ShootIt() {
        ENTITY_TYPE_DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
        MinecraftForge.EVENT_BUS.register(this);
        CHANNEL.registerMessage(0, NetworkMessage.class,NetworkMessage::encode,NetworkMessage::decode,(msg,ctx)->{
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT,()->()-> ClientNetworkHandler.handle(msg));
            ctx.get().setPacketHandled(true);
        });
        LOGGER.info("Shoot it mod loaded.");
    }

    public static Projectile getProjectile(ItemStack itemStack, Level level, LivingEntity owner){
        Item item = itemStack.getItem();
        if(item.getClass().equals(ArrowItem.class)) return ((ArrowItem)item).createArrow(level,itemStack,owner);
        if(item.getClass().equals(SnowballItem.class)) return new Snowball(level,owner);
        if(item.getClass().equals(FireChargeItem.class)) return new SmallFireball(level,owner,owner.getX(),owner.getY(),owner.getZ());
        if(item.getClass().equals(FireworkRocketItem.class)) return new FireworkRocketEntity(level,itemStack,owner);
        if(item.getClass().equals(FishingRodItem.class)) return new FishingHook(EntityType.FISHING_BOBBER,level);
        if(item.getClass().equals(EggItem.class)) return new ThrownEgg(level,owner);
        if(item.getClass().equals(EnderpearlItem.class)) return new ThrownEnderpearl(level,owner);
        if(item instanceof SpawnEggItem spawnEggItem) return new ItemProjectile(level,itemStack,spawnEggItem.createEntity(level,owner,itemStack),null,owner);
        if(item.equals(Items.ENDER_EYE)) return new ItemProjectile(level,itemStack,new EyeOfEnder(level,owner.getX(),owner.getY(),owner.getZ()),null,owner);
        if(item.equals(Items.TNT) || item.equals(Items.TNT_MINECART)) return new ItemProjectile(level,itemStack,new PrimedTnt(level,owner.getX(),owner.getY(),owner.getZ(),owner),null,owner);
        if(item.equals(Items.DRAGON_BREATH)) return new DragonFireball(level,owner,owner.getX(),owner.getY(),owner.getZ());
        if(item.equals(Items.EXPERIENCE_BOTTLE)) return new ThrownExperienceBottle(level,owner);
        if(item.equals(Items.END_CRYSTAL)) return new ItemProjectile(level,itemStack,new EndCrystal(level,owner.getX(),owner.getY(),owner.getZ()),null,owner);
        if(item instanceof PotionItem){
            ThrownPotion thrownPotion = new ThrownPotion(level,owner);
            thrownPotion.setItem(itemStack);
            return thrownPotion;
        }
        if(item instanceof BlockItem blockItem){
            return new ItemProjectile(level,itemStack,null,blockItem.getBlock(),owner);
        }
        else{
            return new ItemProjectile(level,itemStack,null,null,owner);
        }
    }
}
