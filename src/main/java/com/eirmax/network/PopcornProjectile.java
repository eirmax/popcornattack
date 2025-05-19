package com.eirmax.network;

import com.eirmax.PopcornConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PopcornProjectile {
    private final ServerWorld world;
    private final PlayerEntity owner;
    private Vec3d velocity;
    private Vec3d currentPos;
    private final Set<PlayerEntity> hitPlayers = new HashSet<>();

    // Все параметры берутся из PopcornConfig
    private double gravity = PopcornConfig.INSTANCE.gravity;
    private double speed = PopcornConfig.INSTANCE.speed;
    private int particleDensity = PopcornConfig.INSTANCE.particleDensity;
    private double hitRadius = PopcornConfig.INSTANCE.hitRadius;
    private float damage = PopcornConfig.INSTANCE.damage; // ← НОВОЕ

    public PopcornProjectile(ServerWorld world, PlayerEntity owner, Vec3d start, Vec3d direction) {
        this.world = world;
        this.owner = owner;
        this.currentPos = start;
        this.velocity = direction.normalize().multiply(speed);
    }

    /**
     * @return true если снаряд завершён (столкнулся с игроком или блоком, или упал ниже мира)
     */
    public boolean tick() {

        // Применяем гравитацию
        velocity = new Vec3d(velocity.x, velocity.y + gravity, velocity.z);

        // Передвигаем снаряд
        Vec3d nextPos = currentPos.add(velocity);

        // Густой след частиц
        world.spawnParticles(ParticleTypes.POOF, nextPos.x, nextPos.y, nextPos.z, particleDensity, 0, 0, 0, 0);

        // Проверяем попадание по другим игрокам
        Box hitBox = new Box(
                nextPos.x - hitRadius, nextPos.y - hitRadius, nextPos.z - hitRadius,
                nextPos.x + hitRadius, nextPos.y + hitRadius, nextPos.z + hitRadius
        );
        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, hitBox, e -> e != owner && !hitPlayers.contains(e) && !e.isSpectator());
        for (PlayerEntity target : players) {
            hitPlayers.add(target);
            target.damage(target.getDamageSources().thrown(owner, owner), damage); // ← Используем параметр
            return true;
        }

        // Проверяем столкновение с блоком
        if (!world.isAir(BlockPos.ofFloored(nextPos))) {
            return true;
        }

        // Проверяем выход за пределы мира
        if (nextPos.y < world.getBottomY()) {
            return true;
        }

        currentPos = nextPos;
        return false;
    }
}