package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.rinkynooble.entomophobiaperformancefix.Nests;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.event.EntoForgeEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Entomophobia's Forge event handlers. */
@Mixin(value = EntoForgeEvents.class, remap = false)
public abstract class EntoForgeEventsMixin {
    /**
     * No chunk forcing. A Hive Heart forced a chunk near the world's origin (it shifted by 16 bits
     * instead of 4), and a swarm captain forced the 3×3 chunks around it and never gave them back
     * when it died or its swarm ended. Releasing (add = false) still goes through.
     */
    @Redirect(method = {"onLivingSpawned", "onEntityLeave", "loadCaptain"},
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/common/world/ForgeChunkManager;forceChunk(Lnet/minecraft/server/level/ServerLevel;Ljava/lang/String;Lnet/minecraft/world/entity/Entity;IIZZ)Z"))
    private static boolean entomophobiaperformancefix$noForcing(ServerLevel level, String modId, Entity owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        if (add) {
            return false;
        }
        return ForgeChunkManager.forceChunk(level, modId, owner, chunkX, chunkZ, false, ticking);
    }

    /**
     * A myiatic leaving the level alive is stored in the closest Hive only when it despawned or was
     * discarded, can join swarms and isn't listed in the config. The original stores it unless it's
     * dead; answering "dead" skips the storing and keeps the rest (the myiatic count) as it was.
     */
    @Redirect(method = "onEntityLeave",
            at = @At(value = "INVOKE", target = "Lmod/pilot/entomophobia/entity/myiatic/MyiaticBase;isDeadOrDying()Z", remap = true))
    private static boolean entomophobiaperformancefix$storeOnlyDespawned(MyiaticBase mob) {
        return mob.isDeadOrDying() || !Nests.mayStore(mob);
    }

    /** /entomophobia:nest, /entomophobia:nest_actions and /entomophobia:thanks need operator rights (level 2). */
    @Redirect(method = "registerCommands",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;literal(Ljava/lang/String;)Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", remap = true))
    private static LiteralArgumentBuilder<CommandSourceStack> entomophobiaperformancefix$operatorsOnly(String name) {
        return Commands.literal(name).requires(source -> source.hasPermission(2));
    }
}
