package com.rinkynooble.entomophobiaperformancefix;

import mod.pilot.entomophobia.data.worlddata.EntoGeneralSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.truepest.PestBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;

/**
 * Entomophobia's myiatic count (behind its mob cap and the hunt limit) holds only the myiatics loaded.
 *
 * <p>Entomophobia adds one when a myiatic loads (once the server is ready) and removes one when it
 * unloads or dies while the server runs. The myiatics loaded when the game closes were never removed,
 * and were added again when they loaded next time, so the count crept up with each session (it's saved
 * as {@code myiatic_mobcap}). Forge's load and unload events don't always come in pairs either. So once
 * the server is ready, and then once a minute, the count is set to the myiatics actually loaded,
 * counted the way Entomophobia counts them (not the true pests, not the Hive Heart).
 */
public final class MyiaticCount {
    private static final int RECOUNT_TICKS = 1200;
    private static int nextRecount;

    private MyiaticCount() {
    }

    static void onServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = event.getServer();
        if (event.phase != TickEvent.Phase.START || server == null || !server.isReady() || server.getTickCount() < nextRecount) {
            return;
        }
        nextRecount = server.getTickCount() + RECOUNT_TICKS;
        int loaded = 0;
        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof MyiaticBase && !(entity instanceof PestBase) && !(entity instanceof HiveHeartEntity)) {
                    loaded++;
                }
            }
        }
        int counted = EntoGeneralSaveData.getMyiaticCount();
        if (loaded > counted) {
            EntoGeneralSaveData.activeData().addToMyiaticCount(loaded - counted);
        } else if (loaded < counted) {
            EntoGeneralSaveData.activeData().removeFromMyiaticCount(counted - loaded);
        } else {
            return;
        }
        EntomophobiaPerformanceFix.LOGGER.info("Set Entomophobia's myiatic count from {} to {}, the myiatics loaded.", counted, loaded);
    }

    /** When the server stops, so the next one (singleplayer) counts as soon as it's ready. */
    static void reset() {
        nextRecount = 0;
    }
}
