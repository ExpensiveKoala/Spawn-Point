package koala.spawnpoint;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import koala.spawnpoint.configs.CommonConfig;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;

public final class SpawnPointModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        SpawnPointMod.init();
        NeoForgeConfigRegistry.INSTANCE.register(SpawnPointMod.MOD_ID, ModConfig.Type.COMMON, CommonConfig.CONFIG_SPEC);
    }
}
