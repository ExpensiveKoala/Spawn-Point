package koala.spawnpoint.server;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import koala.spawnpoint.SpawnPointMod;
import koala.spawnpoint.configs.CommonConfig;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.neoforged.fml.config.ModConfig;

public class SpawnPointModFabricServer implements DedicatedServerModInitializer {
    
    @Override
    public void onInitializeServer() {
        //NeoForgeConfigRegistry.INSTANCE.register(SpawnPointMod.MOD_ID, ModConfig.Type.SERVER, CommonConfig.CONFIG_SPEC);
    }
}
