package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.Hives;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.Nest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** A nest's chamber; the main one holds the Hive Heart. */
@Mixin(value = Nest.Chamber.class, remap = false)
public abstract class NestChamberMixin {
    @Shadow
    private HiveHeartEntity hiveHeart;

    /**
     * The nest keeps the Heart object it first saw; once that one unloaded and the Heart loaded again,
     * it follows the loaded one. Mobs that vanish near the nest went into the old object's storage.
     */
    @Inject(method = "getHiveHeart", at = @At("HEAD"))
    private void entomophobiaperformancefix$loadedHeart(CallbackInfoReturnable<HiveHeartEntity> cir) {
        if (hiveHeart != null) {
            hiveHeart = Hives.current(hiveHeart);
        }
    }
}
