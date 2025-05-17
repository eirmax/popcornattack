package com.eirmax.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class PopcornProjectile extends ProjectileEntity {
    private static final TrackedData<Float> SPEED = DataTracker.registerData(PopcornProjectile.class, TrackedDataHandlerRegistry.FLOAT);

    public PopcornProjectile(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        this.getDataTracker().set(SPEED, 0.0F);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (this.getOwner() instanceof LivingEntity) {
                    LivingEntity owner = (LivingEntity) this.getOwner();
                    livingEntity.damage(livingEntity.getDamageSources().thrown(this, owner), 1.0F);
                }
            }
        }

        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void tick() {
        super.tick();
    }
}
