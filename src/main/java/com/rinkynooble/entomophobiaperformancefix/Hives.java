package com.rinkynooble.entomophobiaperformancefix;

import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** What the Hive mixins share: one brain per Hive Heart, the storage Entomophobia saves, the Heart loaded now. */
public final class Hives {
    /** The brain of each Hive Heart this session, by the Heart's id. Server thread only. */
    private static final Map<UUID, HiveNervousSystem> BRAINS = new HashMap<>();

    private Hives() {
    }

    public static HiveNervousSystem brain(UUID heart) {
        return BRAINS.get(heart);
    }

    public static void rememberBrain(UUID heart, HiveNervousSystem brain) {
        BRAINS.put(heart, brain);
    }

    /** When the server stops, as Entomophobia drops every brain then. */
    static void forgetBrains() {
        BRAINS.clear();
    }

    /**
     * The storage Entomophobia saves for this Hive Heart: the packet with its id in
     * {@code HiveSaveData.packets} (the newest, should there be two), or null if there is none yet.
     */
    public static HiveSaveData.Packet savedPacket(UUID heart) {
        HiveSaveData.Packet found = null;
        // A copy: in singleplayer the client's network thread adds to this list too.
        for (Object o : HiveSaveData.packets.toArray()) {
            if (o instanceof HiveSaveData.Packet packet && heart.equals(packet.hiveHeart)) {
                found = packet;
            }
        }
        return found;
    }

    /**
     * The Hive Heart object loaded now in place of this one, once this one has unloaded (a Heart whose
     * chunk loads again is a new object with the same id). The same one while it's loaded, and while
     * it's unloaded or dead.
     */
    public static HiveHeartEntity current(HiveHeartEntity heart) {
        if (heart.isRemoved() && heart.level() instanceof ServerLevel level
                && level.getEntity(heart.getUUID()) instanceof HiveHeartEntity loaded) {
            return loaded;
        }
        return heart;
    }
}
