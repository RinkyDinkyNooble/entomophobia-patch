package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.Nests;
import mod.pilot.entomophobia.systems.nest.Nest;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A nest. */
@Mixin(value = Nest.class, remap = false)
public abstract class NestMixin {
    /**
     * A nest builds only while its main chamber (where its Hive Heart sits) is near a player. It
     * waits otherwise and carries on where it stopped when one comes back.
     */
    @Inject(method = "nestTick", at = @At("HEAD"), cancellable = true)
    private void entomophobiaperformancefix$onlyNearPlayers(CallbackInfo ci) {
        Nest nest = (Nest) (Object) this;
        if (nest.server != null && nest.mainChamber != null
                && !Nests.isActive(nest.server, BlockPos.containing(nest.mainChamber.getPosition()))) {
            ci.cancel();
        }
    }
}
