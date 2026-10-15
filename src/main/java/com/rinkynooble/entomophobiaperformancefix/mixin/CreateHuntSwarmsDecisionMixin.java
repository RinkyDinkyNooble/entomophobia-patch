package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.Nests;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.Decision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.StimulantPackage;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.idle.CreateHuntSwarmsDecision;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The Hive's hunt swarms: 8 to 15 stored mobs sent out at the surface above the Heart every 5 to 15 minutes. */
@Mixin(value = CreateHuntSwarmsDecision.class, remap = false)
public abstract class CreateHuntSwarmsDecisionMixin {
    /**
     * The hunt timer only runs while the Heart is near a player. The Hive kept the Heart it first
     * saw after its chunk unloaded, so hunts went on there: the surface search loaded the chunks on
     * the server thread, and the swarm appeared where nothing ticks, emptying the storage into
     * frozen mobs above a nest nobody was near.
     */
    @Inject(method = "condition", at = @At("HEAD"), cancellable = true)
    private void entomophobiaperformancefix$onlyNearPlayers(StimulantPackage stimulus, CallbackInfoReturnable<Boolean> cir) {
        HiveHeartEntity heart = ((Decision) (Object) this).accessHiveHeart();
        if (heart != null && heart.level() instanceof ServerLevel level && !Nests.isActive(level, heart.blockPosition())) {
            cir.setReturnValue(false);
        }
    }
}
