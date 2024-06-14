package com.pricelesskoala.spawnpoint.fabric;

import net.fabricmc.api.ModInitializer;

import com.pricelesskoala.spawnpoint.SpawnPointMod;

public final class SpawnPointModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        SpawnPointMod.init();
    }
}
