package com.pricelesskoala.spawnpoint.neoforge;

import net.neoforged.fml.common.Mod;

import com.pricelesskoala.spawnpoint.SpawnPointMod;

@Mod(SpawnPointMod.MOD_ID)
public final class SpawnPointModNeoForge {
    public SpawnPointModNeoForge() {
        // Run our common setup.
        SpawnPointMod.init();
    }
}
