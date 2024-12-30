package koala.spawnpoint.configs;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class CommonConfig {
    public static final CommonConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;
    
    public final ConfigValue<String> spawnDimension;
    
    private CommonConfig(ModConfigSpec.Builder builder) {
        spawnDimension = builder
          .comment("Spawning dimension")
          .comment("Value to be a valid id of a dimension you have in your game.")
          .comment("Vanilla values are: \"minecraft:overworld\", \"minecraft:the_nether\", and \"minecraft:the_end\"")
          .comment("Cave dimensions like the nether have spawn points generated like the overworld,")
          .comment("however, this doesn't make it 100% safe all the time. Even in vanilla you can spawn on top of magma blocks or lava for example (though unlikely).")
          .define("spawnDimension", "minecraft:overworld", CommonConfig::validateSpawnDimension);
    }
    
    public ResourceKey<Level> getSpawnDimension(MinecraftServer server) {
        String key = spawnDimension.get();
        ResourceLocation location = ResourceLocation.parse(key);
        return ResourceKey.create(Registries.DIMENSION, location);
    }
    
    private static boolean validateSpawnDimension(final Object obj) {
        return obj instanceof String input && ResourceLocation.tryParse(input) != null;
    }
    
    static {
        Pair<CommonConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
