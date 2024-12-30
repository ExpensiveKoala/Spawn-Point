package koala.spawnpoint.mixins;

import koala.spawnpoint.SpawnPointMod;
import koala.spawnpoint.configs.CommonConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug(export=true)
@Mixin(PlayerList.class)
public class PlayerListMixin {
    
    @Shadow
    private MinecraftServer server;
    
    /**
     * Change the level a new player is placed in.
     */
    @Redirect(method="placeNewPlayer", at = @At(value="FIELD", target="Lnet/minecraft/world/level/Level;OVERWORLD:Lnet/minecraft/resources/ResourceKey;"))
    private ResourceKey<Level> placeNewPlayer()
    {
        return CommonConfig.CONFIG.getSpawnDimension(server);
    }
    
    /**
     * Spawn calculations are fired when the ServerPlayer constructor is called.
     * We change the level passed here so the spawn locations are properly calculated.
     * It will get overwritten once player data loads if not a new player.
     */
    @Redirect(method="getPlayerForLogin", at=@At(value="INVOKE", target="Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"))
    private ServerLevel getPlayerForLogin(MinecraftServer instance)
    {
        ServerLevel level = instance.getLevel(CommonConfig.CONFIG.getSpawnDimension(instance));
        if(level == null) {
            SpawnPointMod.LOGGER.error("GetPlayerForLogin: {} did not exist! Falling back to minecraft:overworld!", CommonConfig.CONFIG.spawnDimension.get());
            return instance.overworld();
        }
        return level;
    }
}
