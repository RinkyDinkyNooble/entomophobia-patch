package com.rinkynooble.entomophobiaperformancefix.mixin;

import com.rinkynooble.entomophobiaperformancefix.ProtectedBlocks;
import mod.pilot.entomophobia.systems.PolyForged.shapes.ChamberGenerator;
import mod.pilot.entomophobia.systems.PolyForged.shapes.HollowWeightedCircleLineGenerator;
import mod.pilot.entomophobia.systems.PolyForged.shapes.abstractshapes.ShapeGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The shape generators that build nests: every shape, a chamber's hollow inside and a corridor's
 * carved path each have their own replacement check, and all of them spare protected blocks.
 */
@Mixin(value = {ShapeGenerator.class, ChamberGenerator.class, HollowWeightedCircleLineGenerator.class}, remap = false)
public abstract class ShapeGeneratorMixin {
    @Inject(method = "canThisBeReplaced", at = @At("HEAD"), cancellable = true)
    private void entomophobiaperformancefix$spareProtected(BlockState state, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (ProtectedBlocks.spares(state)) {
            cir.setReturnValue(false);
        }
    }
}
