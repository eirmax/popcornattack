package com.eirmax.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class PopcornProjectilesManager {
    private static final List<PopcornProjectile> projectiles = new LinkedList<>();

    public static void add(PopcornProjectile proj) {
        projectiles.add(proj);
    }

    public static void registerTickHandler() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Iterator<PopcornProjectile> it = projectiles.iterator();
            while (it.hasNext()) {
                PopcornProjectile p = it.next();
                if (p.tick())
                    it.remove();
            }
        });
    }
}