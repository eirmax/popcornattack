package com.eirmax.network;

import com.eirmax.PopcornAttack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class ModNetworking {
    public static void registerPayloads() {
        PayloadTypeRegistry.playC2S().register(PopcornKeyPressedPayload.ID, PopcornKeyPressedPayload.CODEC);
    }
    private static final Random RANDOM = new Random();

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(
                PopcornKeyPressedPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    if (player.getWorld() instanceof ServerWorld serverWorld) {
                        Vec3d look = player.getRotationVector();
                        Vec3d startPos = player.getEyePos();

                        // 3. Рандомное расстояние броска
                        double distance = 5 + player.getRandom().nextDouble() * 5;

                        // Запоминаем, кто уже получил урон (чтобы не нанести несколько раз)
                        Set<PlayerEntity> hitPlayers = new HashSet<>();

                        // 1. Частицы летят вперед по взгляду игрока, а не спавнятся в голове
                        int particleSteps = 20; // Чем больше, тем "гущще" линия частиц
                        for (int i = 0; i < particleSteps; i++) {
                            double t = (double) i / (particleSteps - 1);
                            Vec3d particlePos = startPos.add(look.multiply(distance * t));

                            // 1. Спавним POOF прямо по траектории (по линии взгляда)
                            serverWorld.spawnParticles(
                                    ParticleTypes.POOF,
                                    particlePos.x, particlePos.y, particlePos.z,
                                    1, // count
                                    0, 0, 0, // velocity: частица почти статична
                                    0.0 // spread
                            );

                            // 2. Проверяем, есть ли игроки в радиусе "попадания" конкретной частицы
                            double hitRadius = 0.35; // радиус попадания одной частицы (можно уменьшить для большей кучности)
                            Box hitBox = new Box(
                                    particlePos.x - hitRadius, particlePos.y - hitRadius, particlePos.z - hitRadius,
                                    particlePos.x + hitRadius, particlePos.y + hitRadius, particlePos.z + hitRadius
                            );

                            // Проверяем только других игроков
                            List<PlayerEntity> entities = serverWorld.getEntitiesByClass(PlayerEntity.class, hitBox, e -> e != player && !hitPlayers.contains(e));
                            for (PlayerEntity entity : entities) {
                                hitPlayers.add(entity); // чтобы не нанести урон дважды за бросок
                                entity.damage(entity.getDamageSources().thrown(player, player), 0.1f);
                            }
                        }
                    }
                }
        );
    }
}