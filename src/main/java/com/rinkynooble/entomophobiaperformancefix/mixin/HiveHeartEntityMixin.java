package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.Hives;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The Hive Heart: its brain (the nervous system that breeds, hunts and retaliates) and its storage. */
@Mixin(value = HiveHeartEntity.class, remap = false)
public abstract class HiveHeartEntityMixin {
    @Shadow
    private HiveSaveData.Packet data;

    /**
     * One brain per Hive Heart. Entomophobia made a new one each time a Heart object needed one (two for a
     * new Heart, one more each time its chunk loaded again, and in singleplayer one more each time the
     * player's client loaded the Heart) and never removed any; each ran its own breeding and hunt timers.
     * A Heart that loads again now takes back the brain it had, and the client's copy of a Heart gets
     * none (the brain it got ran on the server, as an extra one).
     */
    @Inject(method = "constructNervousSystem", at = @At("HEAD"), cancellable = true)
    private void entomophobiaperformancefix$oneBrain(Nest nest, CallbackInfo ci) {
        HiveHeartEntity heart = (HiveHeartEntity) (Object) this;
        if (heart.level().isClientSide()) {
            ci.cancel();
            return;
        }
        HiveNervousSystem brain = Hives.brain(heart.getUUID());
        if (brain != null) {
            heart.nervousSystem = brain;
            ci.cancel();
        }
    }

    @Inject(method = "constructNervousSystem", at = @At("RETURN"))
    private void entomophobiaperformancefix$rememberBrain(Nest nest, CallbackInfo ci) {
        HiveHeartEntity heart = (HiveHeartEntity) (Object) this;
        if (heart.nervousSystem != null && heart.getUUID().equals(heart.nervousSystem.hiveHeartUUID)) {
            Hives.rememberBrain(heart.getUUID(), heart.nervousSystem);
        }
    }

    /**
     * A Heart's storage is the one Entomophobia saves for its id. A Heart that loaded again read its
     * storage as it was when the world loaded (nothing, for a Hive born this session) and replaced the
     * saved one with it, while mobs kept vanishing into the old Heart object's copy, which wasn't saved.
     */
    @Inject(method = "accessData(Z)Lmod/pilot/entomophobia/data/worlddata/HiveSaveData$Packet;", at = @At("HEAD"))
    private void entomophobiaperformancefix$savedStorage(boolean quiet, CallbackInfoReturnable<HiveSaveData.Packet> cir) {
        HiveHeartEntity heart = (HiveHeartEntity) (Object) this;
        if (!heart.level().isClientSide()) {
            HiveSaveData.Packet saved = Hives.savedPacket(heart.getUUID());
            if (saved != null) {
                data = saved;
            }
        }
    }
}
