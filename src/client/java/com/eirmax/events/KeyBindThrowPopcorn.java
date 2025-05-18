package com.eirmax.events;

import com.eirmax.Items.ModItems;
import com.eirmax.Items.PopcornItem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

import static com.eirmax.keybinds.KeyBinds.throwPopcornKey;

public class KeyBindThrowPopcorn {
    private static final int COOLDOWN_TICKS = 20 * 15;
    private static final Random RANDOM = new Random();

    private static boolean isKeyPressed = false;
    public static void Init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player != null) {
                ItemStack stack = player.getMainHandStack();
                if (stack.getItem() instanceof PopcornItem) {
                    if (throwPopcornKey.isPressed()) {
                        isKeyPressed = true;
                    }
                    if (throwPopcornKey.wasPressed()) {
                        isKeyPressed = false;
                    }
                    if (isKeyPressed) {
                        if (!player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                            throwPopcorn(player, player.getWorld());
                            stack.damage(1, player, EquipmentSlot.MAINHAND);
                            player.getItemCooldownManager().set(stack.getItem(), COOLDOWN_TICKS);
                        }
                    }
                }
            }
        });
    }

    private static void throwPopcorn(PlayerEntity player, World world) {
        Vec3d look = player.getRotationVector();
        Vec3d startPos = player.getEyePos();

        double distance = 8 + RANDOM.nextDouble() * 7;

        for (int i = 0; i < 8; i++) {
            double t = (double) i / 7;
            Vec3d particlePos = startPos.add(look.multiply(distance * t));

            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(
                        ParticleTypes.END_ROD,
                        startPos.x,
                        startPos.y,
                        startPos.z,
                        1,
                        look.x * 0.5 + (Math.random() - 0.5) * 0.1,
                        look.y * 0.5 + (Math.random() - 0.5) * 0.1,
                        look.z * 0.5 + (Math.random() - 0.5) * 0.1,
                        0.0
                );
            }

            for(int i2 = 0; i2 < 8; i2++) {
                world.addParticle(ParticleTypes.CLOUD,
                        startPos.x,
                        startPos.y,
                        startPos.z,
                        look.x * 0.5 + (Math.random() - 0.5) * 0.1,
                        look.y * 0.5 + (Math.random() - 0.5) * 0.1,
                        look.z * 0.5 + (Math.random() - 0.5) * 0.1);
            }


            Box area = new Box(player.getBlockPos()).expand(4, 4, 4);
            List<PlayerEntity> entities = world.getEntitiesByClass(PlayerEntity.class, area, e -> e != player);

            for (PlayerEntity entity : entities) {
                if (entity.squaredDistanceTo(startPos) <= distance * distance) {
                    entity.damage(entity.getDamageSources().thrown(player, player), 0.1f);
                }
            }
        }
    }
}
