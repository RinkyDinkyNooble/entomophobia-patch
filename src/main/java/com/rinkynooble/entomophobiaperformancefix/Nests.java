package com.rinkynooble.entomophobiaperformancefix;

import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;

/** The rules the mixins share: when a nest may act, and which mobs its Hive stores. */
public final class Nests {
    private Nests() {
    }

    /**
     * Whether a nest whose Hive Heart sits at this position may build or send out a hunt.
     *
     * <p>True when the chunk ticks its mobs (a player is within simulation distance, or another mod
     * keeps it loaded) and its mobs have finished loading, so the Hive Heart is there to be found.
     * Without that check the nest built in chunks nobody had loaded, loading each one on the server
     * thread, and could make a second Heart while the first one's mobs were still loading.
     */
    public static boolean isActive(ServerLevel level, BlockPos heart) {
        return level.isPositionEntityTicking(heart) && level.areEntitiesLoaded(ChunkPos.asLong(heart));
    }

    /**
     * Whether a myiatic that just left the level goes into the closest Hive's storage.
     *
     * <p>Only one that despawned or was discarded (a finished swarm, the myiatic mob cap): not one
     * whose chunk unloaded, which is still saved in that chunk, so storing it made a copy each time
     * a player walked away. Only one that can join a swarm, which leaves out the Celestial Carrion,
     * and none listed in the config's {@code never_stored}.
     */
    public static boolean mayStore(MyiaticBase mob) {
        return mob.getRemovalReason() == Entity.RemovalReason.DISCARDED
                && mob.canSwarm()
                && !Config.isNeverStored(mob.getType());
    }
}
