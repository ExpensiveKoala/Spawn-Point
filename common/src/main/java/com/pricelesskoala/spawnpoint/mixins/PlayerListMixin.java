package com.pricelesskoala.spawnpoint.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.Optional;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    
    
    
    @ModifyReturnValue(method = "load", at = @At("RETURN"))
    public Optional<CompoundTag> load(Optional<CompoundTag> original, ServerPlayer serverPlayer) {
    
        return original;
    }
}
