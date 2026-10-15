package com.rinkynooble.entomophobiaperformancefix.verify;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import mod.pilot.entomophobia.data.worlddata.EntoGeneralSaveData;
import mod.pilot.entomophobia.data.worlddata.HiveSaveData;
import mod.pilot.entomophobia.entity.celestial.HiveHeartEntity;
import mod.pilot.entomophobia.entity.myiatic.MyiaticBase;
import mod.pilot.entomophobia.entity.truepest.PestBase;
import mod.pilot.entomophobia.systems.nest.Nest;
import mod.pilot.entomophobia.systems.nest.NestManager;
import mod.pilot.entomophobia.systems.nest.hivenervoussystem.HiveNervousSystem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Dev-only check commands for the headless server tests (research/server/run_test.py). Never shipped.
 * They print Entomophobia's Hive state from its fields, without calling the methods that change it,
 * so the same commands work with and without the patch. Every line starts with VERIFY.
 */
@Mod("entomophobiaperformancefix_verify")
public class EntoVerify {
    public EntoVerify() {
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("entoverify")
                .then(Commands.literal("brains").executes(safe(EntoVerify::brains)))
                .then(Commands.literal("storage").executes(safe(EntoVerify::storage)))
                .then(Commands.literal("count").executes(safe(EntoVerify::count))));
    }

    @FunctionalInterface
    private interface Check {
        void run(CommandSourceStack source) throws Exception;
    }

    private static Command<CommandSourceStack> safe(Check check) {
        return (CommandContext<CommandSourceStack> ctx) -> {
            try {
                check.run(ctx.getSource());
            } catch (Exception e) {
                say(ctx.getSource(), "ERROR " + e);
            }
            return 1;
        };
    }

    private static void say(CommandSourceStack source, String text) {
        source.sendSuccess(() -> Component.literal("VERIFY " + text), false);
    }

    /** Hive brains (nervous systems) the server ticks, per Hive Heart. */
    private static void brains(CommandSourceStack source) throws Exception {
        List<HiveNervousSystem> all = new ArrayList<>();
        all.addAll(staticList(HiveNervousSystem.Manager.class, "activeNervousSystems"));
        all.addAll(staticList(HiveNervousSystem.Manager.class, "que"));
        Map<String, Integer> perHeart = new TreeMap<>();
        for (HiveNervousSystem brain : all) {
            perHeart.merge(String.valueOf(brain.hiveHeartUUID), 1, Integer::sum);
        }
        say(source, "brains: " + all.size() + " in total");
        perHeart.forEach((heart, n) -> say(source, "brains: heart " + heart + " has " + n));
    }

    /**
     * For each Hive Heart loaded: the storage it holds, the storage Entomophobia saves for its id, and
     * whether its nest still points at an older copy of the Heart (one that unloaded).
     */
    private static void storage(CommandSourceStack source) throws Exception {
        List<UUID> loaded = new ArrayList<>();
        for (ServerLevel level : source.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof HiveHeartEntity heart) {
                    loaded.add(heart.getUUID());
                    HiveSaveData.Packet own = (HiveSaveData.Packet) field(HiveHeartEntity.class, "data").get(heart);
                    HiveSaveData.Packet saved = savedPacket(heart.getUUID());
                    say(source, "storage: heart " + heart.getUUID() + " loaded, holds " + describe(own) + ", saved " + describe(saved)
                            + (own == null || own == saved ? "" : " (NOT the saved one)"));
                }
            }
        }
        for (HiveSaveData.Packet packet : new ArrayList<>(HiveSaveData.packets)) {
            if (!loaded.contains(packet.hiveHeart)) {
                say(source, "storage: heart " + packet.hiveHeart + " not loaded, saved " + describe(packet));
            }
        }
        for (Nest nest : NestManager.getActiveNests()) {
            UUID id = nest.mainChamber.getHiveHeartUUID();
            HiveHeartEntity known = (HiveHeartEntity) field(Nest.Chamber.class, "hiveHeart").get(nest.mainChamber);
            String state;
            if (known == null) {
                state = "no Heart object yet";
            } else if (!known.isRemoved()) {
                state = "the loaded Heart";
            } else {
                Entity current = known.level() instanceof ServerLevel level ? level.getEntity(known.getUUID()) : null;
                state = (current != null ? "an OLD copy (the Heart is loaded again as another object)" : "an old copy (the Heart is unloaded)")
                        + ", which holds " + describe((HiveSaveData.Packet) field(HiveHeartEntity.class, "data").get(known));
            }
            say(source, "storage: nest of heart " + id + " points at " + state);
        }
    }

    /** Entomophobia's myiatic count (mob cap and hunts) and the myiatics actually loaded, counted its way. */
    private static void count(CommandSourceStack source) {
        int loaded = 0;
        Map<String, Integer> kinds = new TreeMap<>();
        for (ServerLevel level : source.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof MyiaticBase && !(entity instanceof PestBase) && !(entity instanceof HiveHeartEntity)) {
                    loaded++;
                    kinds.merge(entity.getEncodeId() + (entity.isAlive() ? "" : " (dying)"), 1, Integer::sum);
                }
            }
        }
        say(source, "count: Entomophobia counts " + EntoGeneralSaveData.getMyiaticCount() + ", loaded " + loaded + (kinds.isEmpty() ? "" : " " + kinds));
    }

    private static HiveSaveData.Packet savedPacket(UUID heart) {
        for (HiveSaveData.Packet packet : new ArrayList<>(HiveSaveData.packets)) {
            if (packet.hiveHeart.equals(heart)) {
                return packet;
            }
        }
        return null;
    }

    private static String describe(HiveSaveData.Packet packet) {
        if (packet == null) {
            return "nothing";
        }
        return packet.getTotalInStorage() + " mobs " + new TreeMap<>(packet.storedEntities) + " [#" + Integer.toHexString(System.identityHashCode(packet)) + "]";
    }

    @SuppressWarnings("unchecked")
    private static List<HiveNervousSystem> staticList(Class<?> owner, String name) throws Exception {
        return (List<HiveNervousSystem>) field(owner, name).get(null);
    }

    private static Field field(Class<?> owner, String name) throws NoSuchFieldException {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}
