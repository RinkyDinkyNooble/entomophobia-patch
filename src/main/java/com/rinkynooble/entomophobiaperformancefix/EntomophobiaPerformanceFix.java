package com.rinkynooble.entomophobiaperformancefix;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

@Mod(EntomophobiaPerformanceFix.MODID)
public class EntomophobiaPerformanceFix {
    public static final String MODID = "entomophobiaperformancefix";
    public static final Logger LOGGER = LogManager.getLogger("Entomophobia Performance Fix");
    static final String ENTOMOPHOBIA = "entomophobia";

    public EntomophobiaPerformanceFix() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::onLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::onReload);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        // Forge keeps the callbacks in a plain map, so register on the main thread.
        event.enqueueWork(() -> ForgeChunkManager.setForcedChunkLoadingCallback(ENTOMOPHOBIA, EntomophobiaPerformanceFix::releaseTickets));
    }

    /**
     * Called by Forge for each level as it loads, before the saved chunk tickets are put back.
     * Entomophobia no longer forces any chunk (see EntoForgeEventsMixin), so every ticket it saved
     * is a leftover from an unpatched game: a dead swarm captain's 3×3 chunks, or the chunk near the
     * world's origin that a Hive Heart held. They're all removed, so those chunks load normally again.
     */
    private static void releaseTickets(ServerLevel level, ForgeChunkManager.TicketHelper tickets) {
        int owners = 0;
        int chunks = 0;
        for (Map.Entry<UUID, Pair<LongSet, LongSet>> entry : tickets.getEntityTickets().entrySet()) {
            owners++;
            chunks += entry.getValue().getFirst().size() + entry.getValue().getSecond().size();
            tickets.removeAllTickets(entry.getKey());
        }
        for (Map.Entry<BlockPos, Pair<LongSet, LongSet>> entry : tickets.getBlockTickets().entrySet()) {
            owners++;
            chunks += entry.getValue().getFirst().size() + entry.getValue().getSecond().size();
            tickets.removeAllTickets(entry.getKey());
        }
        LOGGER.info("Released {} chunk tickets that Entomophobia left behind in {} ({} owners).",
                chunks, level.dimension().location(), owners);
    }
}
