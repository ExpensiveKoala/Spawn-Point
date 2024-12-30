package koala.spawnpoint.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import koala.spawnpoint.SpawnPointMod;
import koala.spawnpoint.configs.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug(export=true)
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    
    @Shadow
    public MinecraftServer server;
    
    /**
     * Changes the level player is to respawn at if bed/anchor don't exist, or exist but is obstructed.
     */
    @Redirect(method="findRespawnPositionAndUseSpawnBlock", at=@At(value="INVOKE", target="Lnet/minecraft/server/MinecraftServer;overworld()Lnet/minecraft/server/level/ServerLevel;"))
    public ServerLevel changeRespawnLevel(MinecraftServer instance) {
        ServerLevel level = instance.getLevel(CommonConfig.CONFIG.getSpawnDimension(instance));
        if(level == null) {
            SpawnPointMod.LOGGER.error("findRespawnPositionAndUseSpawnBlock: {} did not exist! Falling back to minecraft:overworld!", CommonConfig.CONFIG.spawnDimension.get());
            return instance.overworld();
        }
        return level;
    }
    
    /**
     * Mixin to find suitable spawn point within the spawn radius. Vanilla's behavior doesn't cover when the dimensiontype.hasSkyLight is false.
     * Refer to the method being injected to.
     */
    @SuppressWarnings({"ReassignedVariable", "ConstantValue"})
    @ModifyVariable(method="adjustSpawnLocation", at=@At(value="STORE", ordinal = 0), ordinal = 1)
    public BlockPos adjustSpawnLocation(BlockPos pos, @Local ServerLevel level, @Local AABB aabb){
        if (!level.dimensionType().hasSkyLight()) {
            BlockPos result = pos;
            int spawnRadius = Math.max(0, server.getSpawnRadius(level));
            int distToBorder = Mth.floor(level.getWorldBorder().getDistanceToBorder(pos.getX(), pos.getZ()));
            spawnRadius = Math.max(1, Math.min(spawnRadius, distToBorder));
            
            long diameter = spawnRadius * 2L + 1;
            int area = (int) Math.min(diameter * diameter, Integer.MAX_VALUE);
            int coprime = area <= 16 ? area - 1 : 17;
            int offset = RandomSource.create().nextInt(coprime);
            for (int step = 0; step < area; step++) {
                int coord = (offset + (coprime * step)) % area;
                int xOffset = coord % (spawnRadius * 2 + 1);
                int zOffset = coord / (spawnRadius * 2 + 1);
                result = spawnpoint$ServerPlayerMixin$getRespawnPos(level, pos.getX() + xOffset - spawnRadius, pos.getZ() + zOffset - spawnRadius);
                if (result != null && level.noCollision((ServerPlayer)(Object)(this), aabb.move(result.getBottomCenter()))) {
                    break;
                }
            }
            
            return result == null ? pos : result;
        }
        return pos;
    }
    
    /**
     * Find topmost solid block in x z coordinates. Starts from the top and looks down for a solid faced block.
     */
    @Unique
    private BlockPos spawnpoint$ServerPlayerMixin$getRespawnPos(ServerLevel level, int x, int z) {
        boolean hasCeiling = level.dimensionType().hasCeiling();
        LevelChunk levelchunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        int maxHeight = hasCeiling
          ? level.getChunkSource().getGenerator().getSpawnHeight(level)
          : levelchunk.getHeight(Heightmap.Types.MOTION_BLOCKING, x & 15, z & 15);
        if (maxHeight < level.getMinBuildHeight()) {
            return null;
        } else {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
                
            for (int y = maxHeight + 1; y >= level.getMinBuildHeight(); y--) {
                mutablePos.set(x, y, z);
                BlockState blockstate = level.getBlockState(mutablePos);
                if (!blockstate.getFluidState().isEmpty()) {
                    break;
                }
                
                if (Block.isFaceFull(blockstate.getCollisionShape(level, mutablePos), Direction.UP)) {
                    return mutablePos.above().immutable();
                }
            }
            return null;
        }
    }
}
