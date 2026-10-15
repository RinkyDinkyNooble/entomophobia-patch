package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.ProtectedBlocks;
import mod.pilot.entomophobia.systems.nest.features.Feature;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** A nest feature (flesh clumps, blood pits, combs), placed from a structure template inside a finished chamber. */
@Mixin(value = Feature.class, remap = false)
public abstract class FeatureMixin {
    /** A feature whose template would cover a protected block isn't placed; the chamber tries another one. */
    @Redirect(method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeInWorld(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructurePlaceSettings;Lnet/minecraft/util/RandomSource;I)Z", remap = true))
    private boolean entomophobiaperformancefix$spareProtected(StructureTemplate template, ServerLevelAccessor level, BlockPos pos, BlockPos pivot,
                                                              StructurePlaceSettings settings, RandomSource random, int flags) {
        BoundingBox box = template.getBoundingBox(settings, pos);
        for (BlockPos inside : BlockPos.betweenClosed(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ())) {
            if (ProtectedBlocks.spares(level.getBlockState(inside))) {
                return false;
            }
        }
        return template.placeInWorld(level, pos, pivot, settings, random, flags);
    }
}
