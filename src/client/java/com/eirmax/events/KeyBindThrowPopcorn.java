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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

import static com.eirmax.keybinds.KeyBinds.throwPopcornKey;

public class KeyBindThrowPopcorn {
    private static final int COOLDOWN_TICKS = 20 * 15;
    public static void Init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (throwPopcornKey.wasPressed()) {
                PlayerEntity player = MinecraftClient.getInstance().player;
                if (player != null) {
                    ItemStack stack = player.getMainHandStack();
                    if (stack.getItem() instanceof PopcornItem) {
                        throwPopcorn(player, player.getWorld());
                        stack.damage(1, player, EquipmentSlot.MAINHAND);
                        player.getItemCooldownManager().set(ModItems.POPCORN, COOLDOWN_TICKS);
                    }
                }
            }
        });
    }

    private static void throwPopcorn(PlayerEntity player, World world) {
        Vec3d look = player.getRotationVector();
        Vec3d startPos = player.getEyePos();


        for (int i = 0; i < 8; i++) {
            world.addParticle(ParticleTypes.END_ROD,
                    startPos.x,
                    startPos.y,
                    startPos.z,
                    look.x * 0.5 + (Math.random() - 0.5) * 0.1,
                    look.y * 0.5 + (Math.random() - 0.5) * 0.1,
                    look.z * 0.5 + (Math.random() - 0.5) * 0.1);
        }


        Box area = new Box(player.getBlockPos()).expand(4, 4, 4);
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, area, e -> e != player);

        for (LivingEntity entity : entities) {
            if (entity.damage(entity.getDamageSources().thrown(player, player), 1.0f)) {
                entity.hurtTime = 0;
            }
        }
    }
}
