package mctmods.smelteryio.entity;

import mctmods.smelteryio.SmelteryIO;
import mctmods.smelteryio.registry.Registry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityIceball extends EntityThrowable {

    @SuppressWarnings("unused")
    public EntityIceball(World worldIn) {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
    }

    public EntityIceball(World worldIn, EntityLivingBase throwerIn) {
        super(worldIn);
        this.setSize(0.25F, 0.25F);
        this.setPosition(throwerIn.posX, throwerIn.posY + (double)throwerIn.getEyeHeight() - 0.10000000149011612D, throwerIn.posZ);
        this.thrower = throwerIn;
    }

    public static void registerEntity() {
        EntityRegistry.registerModEntity(new ResourceLocation(SmelteryIO.MODID, "iceball"), EntityIceball.class, "iceball", 200, SmelteryIO.instance, 64, 10, true);
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityIceball.class, renderManager -> new RenderSnowball<>(renderManager, Registry.ICEBALL, Minecraft.getMinecraft().getRenderItem()));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            int itemId = Item.getIdFromItem(Registry.ICEBALL);
            for (int i = 0; i < 8; ++i) {
                double vx = ((double)this.rand.nextFloat() - 0.5D) * 0.08D;
                double vy = ((double)this.rand.nextFloat() - 0.5D) * 0.08D + 0.2D;
                double vz = ((double)this.rand.nextFloat() - 0.5D) * 0.08D;
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, vx, vy, vz, itemId);
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (result.entityHit != null) {
            int damage = 4;
            if (result.entityHit instanceof EntityBlaze) damage = 24;
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float)damage);
        }
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte)3);
            this.setDead();
        }
    }
}
