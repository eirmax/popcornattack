package com.eirmax;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class PopcornConfig {
    public double gravity = -0.03;
    public double speed = 0.1;
    public int particleDensity = 10;
    public double hitRadius = 0.4;
    public float damage = 0.1f;
    public int cooldownTicks = 20 * 15; // ← Новый параметр

    private static final Path CONFIG_PATH = Path.of("config/popcornattack.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static PopcornConfig INSTANCE = new PopcornConfig();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                INSTANCE = GSON.fromJson(reader, PopcornConfig.class);
            } catch (Exception e) {
                PopcornAttack.LOGGER.error("Failed to load Popcorn config", e);
            }
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            PopcornAttack.LOGGER.error("Failed to save Popcorn config", e);
        }
    }
}