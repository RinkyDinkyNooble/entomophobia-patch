package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.Config;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.Decision;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.StimulantPackage;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.decisions.pain.RetaliateWithSwarmDecision;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The swarm a hurt Hive Heart sends out of its storage at whoever hit it. */
@Mixin(value = RetaliateWithSwarmDecision.class, remap = false)
public abstract class RetaliateWithSwarmDecisionMixin {
    @Unique
    private long entomophobiaperformancefix$nextWave = Long.MIN_VALUE;

    /** At most one swarm per cooldown (config, 5 seconds by default), so fast weapons don't empty the storage hit by hit. */
    @Inject(method = "activate", at = @At("HEAD"), cancellable = true)
    private void entomophobiaperformancefix$cooldown(StimulantPackage stimulus, CallbackInfo ci) {
        int cooldown = Config.retaliationCooldown();
        ServerLevel level = ((Decision) (Object) this).server();
        if (cooldown <= 0 || level == null) {
            return;
        }
        long now = level.getGameTime();
        if (now < entomophobiaperformancefix$nextWave) {
            ci.cancel();
            return;
        }
        entomophobiaperformancefix$nextWave = now + cooldown;
    }
}
