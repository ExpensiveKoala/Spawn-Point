package com.pricelesskoala.spawnpoint;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;

public class SpawnPointSolver {
    
    public static BlockPos findSpawnPoint(ServerLevel serverLevel, ServerPlayer player) {
        BlockPos sharedSpawnPos = serverLevel.getSharedSpawnPos();
        BlockPos spawnPos = sharedSpawnPos;
        // Overworld spawn
        if (serverLevel.dimensionType().hasSkyLight() && serverLevel.getServer().getWorldData().getGameType() != GameType.ADVENTURE) {
            int spawnRadius = Math.max(0, player.server.getSpawnRadius(serverLevel));
            int spawnDistanceToWorldBorder = Mth.floor(serverLevel.getWorldBorder().getDistanceToBorder(sharedSpawnPos.getX(), sharedSpawnPos.getZ()));
            if (spawnDistanceToWorldBorder < spawnRadius) {
                spawnRadius = spawnDistanceToWorldBorder;
            }
            if (spawnDistanceToWorldBorder <= 1) {
                spawnRadius = 1;
            }
            long spawnRange = spawnRadius * 2L + 1;
            long spawnPositions = spawnRange * spawnRange;
            int intSpawnPositions = spawnPositions > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)spawnPositions;
            int coprime = getCoprime(intSpawnPositions);
            int randomOffset = RandomSource.create().nextInt(intSpawnPositions);
            for (int p = 0; p < intSpawnPositions; p++) {
                int q = (randomOffset + coprime * p) % intSpawnPositions;
                int xOffset = q % (spawnRadius * 2 + 1);
                int zOffset = q / (spawnRadius * 2 + 1);
                BlockPos blockPos2 = PlayerRespawnLogic.getOverworldRespawnPos(serverLevel, sharedSpawnPos.getX() + xOffset - spawnRadius, sharedSpawnPos.getZ() + zOffset - spawnRadius);
                if (blockPos2 == null) continue;
                spawnPos = blockPos2;
                if (!serverLevel.noCollision(player)) {
                    continue;
                }
                break;
            }
        } else {
            spawnPos = sharedSpawnPos;
            while (!serverLevel.noCollision(this) && this.getY() < (double)(serverLevel.getMaxBuildHeight() - 1)) {
                spawnPos = spawnPos.above();
            }
        }
        return spawnPos;
    }
    
    private static int getCoprime(int i) {
        return i <= 16 ? i - 1 : 17;
    }
}
