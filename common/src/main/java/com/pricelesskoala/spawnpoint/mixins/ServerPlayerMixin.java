package com.pricelesskoala.spawnpoint.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    
    @Shadow @Final public MinecraftServer server;
    @Shadow private float respawnAngle;
    @Shadow private boolean respawnForced;
    @Shadow @Nullable private BlockPos respawnPosition;
    @Shadow private ResourceKey<Level> respawnDimension;
    
    // Override the spawn location used in PlayerList#respawn. We are tricking the game to think we're respawning from a bed/respawn anchor/set spawn command.
    @Inject(method = "getRespawnPosition", at = @At("HEAD"), cancellable = true)
    public void getRespawnPosition(CallbackInfoReturnable<BlockPos> cir) {
        if (shouldOverrideRespawn()) {
            cir.setReturnValue();
        }
    }
    
    // Override the spawn dimension used in PlayerList#respawn. We are tricking the game to think we're respawning from a bed/respawn anchor/set spawn command.
    @Inject(method = "getRespawnDimension", at = @At("HEAD"), cancellable = true)
    public void getRespawnDimension(CallbackInfoReturnable<ResourceKey<Level>> cir) {
        if (shouldOverrideRespawn()) {
            cir.setReturnValue();
        }
    }
    
    // Force respawn used in PlayerList#respawn. We are tricking the game to think we're respawning from a bed/respawn anchor/set spawn command.
    @Inject(method = "isRespawnForced", at = @At("HEAD"), cancellable = true)
    public void isRespawnForced(CallbackInfoReturnable<Boolean> cir) {
        if (shouldOverrideRespawn()) {
            cir.setReturnValue(true);
        }
    }
    
    // Funny story... Minecraft moves all players joining the game to spawn and then updates position based on stored player .dat info in PlayerList#placeNewPlayer
    // So that means if we inject at the start here, we set the starting position for players joining for the first time since they don't have any stored positional data
    @Inject(method = "fudgeSpawnLocation", at = @At("HEAD"), cancellable = true)
    private void fudgeSpawnLocation(ServerLevel level, CallbackInfo callbackInfo) {
        // Move player to new spawn location
        callbackInfo.cancel();
    }
    
    /**
     * Checks if player has valid respawn point set. (I.E. Bed, Respawn Beacon, etc...)
     * @return true if we don't have a spawn point set (bed, spawn beacon, command)
     */
    private boolean shouldOverrideRespawn() {
        BlockPos blockPos = this.respawnPosition;
        ServerLevel level = this.server.getLevel(this.respawnDimension);
        // findRespawnPositionAndUseSpawnBlock parameters: ServerLevel, Spawn Position, Angle, Forced respawn (setspawn command), penalty-less respawn (don't consume respawn beacon charge)
        Optional<Vec3> optional = level != null && blockPos != null ? Player.findRespawnPositionAndUseSpawnBlock(level, blockPos, this.respawnAngle, this.respawnForced, true) : Optional.empty();
        return optional.isEmpty();
    }
}
