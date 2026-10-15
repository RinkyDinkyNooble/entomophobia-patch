package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.Hives;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/** The Hives' storage, saved in world/data/entomophobia_hive_world_data.dat. */
@Mixin(value = HiveSaveData.class, remap = false)
public abstract class HiveSaveDataMixin {
    /**
     * Every Hive in the file is read when the world loads. Entomophobia only read a Hive's storage when
     * its Heart was first used in a session, and saves only what it has read, so a Hive whose Heart
     * stayed unloaded for a whole session lost its storage at that session's next save.
     */
    @Inject(method = "load", at = @At("RETURN"))
    private static void entomophobiaperformancefix$keepEveryHive(CompoundTag tag, CallbackInfoReturnable<HiveSaveData> cir) {
        for (String key : tag.getAllKeys()) {
            if (!key.endsWith("_corpsedew")) {
                continue;
            }
            UUID heart;
            try {
                heart = UUID.fromString(key.substring(0, key.length() - "_corpsedew".length()));
            } catch (IllegalArgumentException e) {
                continue;
            }
            if (Hives.savedPacket(heart) == null) {
                HiveSaveData.Packet.unpack(tag, heart);
            }
        }
    }
}
