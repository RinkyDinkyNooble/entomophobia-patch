package com.rinkynooble.entomophobiaperformancefix;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Blocks an Entomophobia nest can't replace while it builds.
 *
 * <p>The same rule as Prion Performance Fix's: a block is spared when it holds a block entity
 * (chests, furnaces, spawners, keypads, machines), can't be broken, resists explosions at least as
 * well as obsidian (SecurityCraft's reinforced blocks), or is in the
 * {@code entomophobiaperformancefix:nest_immune} tag. Entomophobia's own blocks stay replaceable,
 * so a nest still builds over its own flesh as before.
 */
public final class ProtectedBlocks {
    // The constructor works on every Forge 47; fromNamespaceAndPath only exists in later builds.
    @SuppressWarnings("removal")
    public static final TagKey<Block> NEST_IMMUNE = TagKey.create(Registries.BLOCK, new ResourceLocation(EntomophobiaPerformanceFix.MODID, "nest_immune"));
    private static final float OBSIDIAN_RESISTANCE = 1200.0F;

    /** Entomophobia's blocks, found once its registrations are in (a level only exists after that). */
    private static volatile Block[] entomophobiaBlocks;

    private ProtectedBlocks() {
    }

    /** Whether a nest must leave this block as it is. */
    public static boolean spares(BlockState state) {
        if (state.is(NEST_IMMUNE)) {
            return true;
        }
        Block block = state.getBlock();
        if (!state.hasBlockEntity() && block.defaultDestroyTime() >= 0 && block.getExplosionResistance() < OBSIDIAN_RESISTANCE) {
            return false;
        }
        return !isEntomophobiaBlock(block);
    }

    private static boolean isEntomophobiaBlock(Block block) {
        Block[] blocks = entomophobiaBlocks;
        if (blocks == null) {
            blocks = BuiltInRegistries.BLOCK.entrySet().stream()
                    .filter(entry -> entry.getKey().location().getNamespace().equals(EntomophobiaPerformanceFix.ENTOMOPHOBIA))
                    .map(entry -> entry.getValue())
                    .toArray(Block[]::new);
            entomophobiaBlocks = blocks;
        }
        for (Block own : blocks) {
            if (own == block) {
                return true;
            }
        }
        return false;
    }
}
