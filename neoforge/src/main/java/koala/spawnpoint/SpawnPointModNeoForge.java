package koala.spawnpoint;

import koala.spawnpoint.configs.CommonConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(SpawnPointMod.MOD_ID)
public final class SpawnPointModNeoForge {
    public SpawnPointModNeoForge(ModContainer modContainer) {
        // Run our common setup.
        SpawnPointMod.init();
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.CONFIG_SPEC);
    }
}
