package com.eirmax;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class PopcornCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("popcorn")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("parameter", StringArgumentType.word())
                                .then(CommandManager.argument("value", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> {
                                            String param = StringArgumentType.getString(ctx, "parameter");
                                            double value = DoubleArgumentType.getDouble(ctx, "value");
                                            boolean ok = false;
                                            switch (param) {
                                                case "gravity":
                                                    PopcornConfig.INSTANCE.gravity = value;
                                                    ok = true;
                                                    break;
                                                case "speed":
                                                    PopcornConfig.INSTANCE.speed = value;
                                                    ok = true;
                                                    break;
                                                case "particleDensity":
                                                    PopcornConfig.INSTANCE.particleDensity = (int) value;
                                                    ok = true;
                                                    break;
                                                case "hitRadius":
                                                    PopcornConfig.INSTANCE.hitRadius = value;
                                                    ok = true;
                                                    break;
                                                case "damage":
                                                    PopcornConfig.INSTANCE.damage = (float) value;
                                                    ok = true;
                                                    break;
                                                case "cooldownTicks": // ← Новая ветка
                                                    PopcornConfig.INSTANCE.cooldownTicks = (int) value;
                                                    ok = true;
                                                    break;
                                            }
                                            if (ok) {
                                                PopcornConfig.save();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Set " + param + " = " + value), true);
                                                return 1;
                                            } else {
                                                ctx.getSource().sendError(Text.literal("Unknown parameter: " + param));
                                                return 0;
                                            }
                                        })
                                )
                        )
                )
                .then(CommandManager.literal("get")
                        .executes(ctx -> {
                            PopcornConfig c = PopcornConfig.INSTANCE;
                            ctx.getSource().sendFeedback(
                                    () -> Text.literal(
                                            String.format(
                                                    "Popcorn config:\ngravity=%.5f\nspeed=%.5f\nparticleDensity=%d\nhitRadius=%.3f\ndamage=%.2f\ncooldownTicks=%d",
                                                    c.gravity, c.speed, c.particleDensity, c.hitRadius, c.damage, c.cooldownTicks)
                                    ),
                                    false
                            );
                            return 1;
                        })
                )
                .then(CommandManager.literal("help")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(
                                    () -> Text.literal(
                                            "Доступные команды Popcorn:\n" +
                                                    "/popcorn get — показать текущие параметры\n" +
                                                    "/popcorn set <parameter> <value> — изменить параметр (gravity, speed, particleDensity, hitRadius, damage)\n" +
                                                    "Параметры: \n" +
                                                    "gravity - Гравитация частиц\n" +
                                                    "speed - Скорость полета частиц\n" +
                                                    "particleDensity - Плотность шлейфа частиц при броске\n" +
                                                    "hitRadius - Радиус относительно головы частиц в котором наносится урон игроку\n" +
                                                    "damage - Количество урона, наносимое игроку при попадании\n" +
                                                    "cooldownTicks - Время перезарядки перед следующим использованием\n"
                                    ),
                                    false
                            );
                            return 1;
                        })
                )
        );
    }
}