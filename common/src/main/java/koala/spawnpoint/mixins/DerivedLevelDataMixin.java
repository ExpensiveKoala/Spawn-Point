package koala.spawnpoint.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = true)
@Mixin(DerivedLevelData.class)
public class DerivedLevelDataMixin {
    
    @Shadow
    private ServerLevelData wrapped;
    
    /**
     * We want to be setting the spawn pos even on DerivedLevelData.
     */
    @Inject(method = "setSpawn", at = @At("HEAD"))
    public void setSpawn(BlockPos pSpawnPoint, float pAngle, CallbackInfo ci) {
        wrapped.setSpawn(pSpawnPoint, pAngle);
    }
}
