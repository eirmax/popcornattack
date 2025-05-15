package com.eirmax.registry;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
public class PopcornParticleEntity extends ProjectileEntity {
    private int timer = 0;
    public PopcornParticleEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (!this.getWorld().isClient) {
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                if (entityHitResult.getEntity() instanceof LivingEntity) {
                    LivingEntity target = (LivingEntity) entityHitResult.getEntity();
                    LivingEntity owner = (LivingEntity) this.getOwner();
                    target.damage(target.getDamageSources().thrown(this, owner), 0.0F);
                }
            }
        }
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
        timer++;

        if (timer >= 100) {
            this.remove(RemovalReason.KILLED);
        }
    }
}