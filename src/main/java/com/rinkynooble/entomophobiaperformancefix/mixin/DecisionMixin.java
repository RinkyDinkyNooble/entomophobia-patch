package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.Hives;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.Decision;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Something a Hive brain does (breeding, hunts). */
@Mixin(value = Decision.class, remap = false)
public abstract class DecisionMixin {
    @Shadow
    protected HiveHeartEntity hh;

    /** A decision keeps the Heart object it first saw; once that one unloaded and the Heart loaded again, it follows the loaded one. */
    @Inject(method = "accessHiveHeart", at = @At("HEAD"))
    private void entomophobiaperformancefix$loadedHeart(CallbackInfoReturnable<HiveHeartEntity> cir) {
        if (hh != null) {
            hh = Hives.current(hh);
        }
    }
}
