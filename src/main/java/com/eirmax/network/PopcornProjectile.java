package com.eirmax.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
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
    private double traveled = 0;
    private final double gravity = -0.04; // Сила гравитации (можно подобрать)
    private final double speed = 0.7; // Начальная скорость (скаляр)

    public PopcornProjectile(ServerWorld world, PlayerEntity owner, Vec3d start, Vec3d direction) {
        this.world = world;
        this.owner = owner;
        this.currentPos = start;
        // Velocity — это direction, умноженный на speed
        this.velocity = direction.normalize().multiply(speed);
    }

    /**
     * @return true если снаряд завершён (столкнулся с игроком или долетел до maxDistance)
     */
    public boolean tick() {
        // Применяем гравитацию
        velocity = new Vec3d(velocity.x, velocity.y + gravity, velocity.z);

        // Передвигаем снаряд
        Vec3d nextPos = currentPos.add(velocity);
        traveled += velocity.length();

        // Густой след частиц
        world.spawnParticles(ParticleTypes.POOF, nextPos.x, nextPos.y, nextPos.z, 10, 0, 0, 0, 0);

        // Проверяем попадание по другим игрокам (в радиусе 0.4 блока)
        double hitRadius = 0.4;
        Box hitBox = new Box(
                nextPos.x - hitRadius, nextPos.y - hitRadius, nextPos.z - hitRadius,
                nextPos.x + hitRadius, nextPos.y + hitRadius, nextPos.z + hitRadius
        );
        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, hitBox, e -> e != owner && !hitPlayers.contains(e) && !e.isSpectator());
        for (PlayerEntity target : players) {
            hitPlayers.add(target);
            target.damage(target.getDamageSources().thrown(owner, owner), 0.1f);
            return true;
        }

        // Проверяем столкновение с блоком
        if (!world.isAir(net.minecraft.util.math.BlockPos.ofFloored(nextPos))) {
            return true; // Прекращаем полет, если попали в блок
        }

        // Проверяем выход за пределы мира
        if (nextPos.y < world.getBottomY()) {
            return true;
        }

        currentPos = nextPos;
        return false;
    }
}