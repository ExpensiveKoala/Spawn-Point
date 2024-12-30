package koala.spawnpoint.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import koala.spawnpoint.SpawnPointMod;
import koala.spawnpoint.configs.CommonConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export=true)
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    
    @Shadow
    private static void setInitialSpawn(ServerLevel pLevel, ServerLevelData pLevelData, boolean pGenerateBonusChest, boolean pDebug) {}
    
    /**
     * This forces the game to choose where "spawn" is on levels other than the overworld.
     */
    @Inject(method="createLevels", at= @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    public void setInitialSpawn(ChunkProgressListener pListener, CallbackInfo ci, @Local boolean debug, @Local WorldOptions worldOptions, @Local(ordinal = 1) ResourceKey<Level> resourceKey, @Local(ordinal = 1) ServerLevel serverLevel, @Local DerivedLevelData derivedLevelData){
        if (resourceKey == CommonConfig.CONFIG.getSpawnDimension((MinecraftServer)(Object)this)) {
            setInitialSpawn(serverLevel, derivedLevelData, worldOptions.generateBonusChest(), debug);
        }
    }
    
    /**
     * If we're not spawning in the overworld, then we don't want to invoke the setInitialSpawn method.
     */
    @Redirect(method="createLevels", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;setInitialSpawn(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/storage/ServerLevelData;ZZ)V"))
    public void preventSetInitialSpawn(ServerLevel level, ServerLevelData levelData, boolean generateBonusChest, boolean debug){
        if(CommonConfig.CONFIG.getSpawnDimension((MinecraftServer)(Object)this) == Level.OVERWORLD) {
            setInitialSpawn(level, levelData, generateBonusChest, debug);
        }
    }
    
    /**
     * This makes the game load spawn chunks in the correct dimension we want.
     * This also has the effect of making the spawn chunks in the correct dimension load like vanilla spawn chunks do.
     *
     * @return
     */
    @Redirect(method="prepareLevels", at= @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"))
    public ServerLevel prepareLevels(MinecraftServer instance) {
        ServerLevel level = instance.getLevel(CommonConfig.CONFIG.getSpawnDimension(instance));
        if(level == null) {
            SpawnPointMod.LOGGER.error("MinecraftServer#prepareLevels: {} did not exist! Falling back to minecraft:overworld!", CommonConfig.CONFIG.spawnDimension.get());
            return instance.overworld();
        }
        return level;
    }
}
