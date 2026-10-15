package com.rinkynooble.entomophobiaperformancefix.verify;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

/**
 * Prints, from a world folder: the Forge chunk tickets each mod saved in each dimension, and every
 * Hive's storage. Used by the headless server tests in research/server (see research/HANDOFF.md).
 */
public final class ReadSave {
    private ReadSave() {
    }

    public static void main(String[] args) throws IOException {
        File world = new File(args[0]);
        System.out.println("World: " + world);
        printTickets("overworld", new File(world, "data/chunks.dat"));
        printTickets("the_nether", new File(world, "DIM-1/data/chunks.dat"));
        printTickets("the_end", new File(world, "DIM1/data/chunks.dat"));
        printHives(new File(world, "data/entomophobia_hive_world_data.dat"));
        printSwarms(new File(world, "data/entomophobia_swarm_world_data.dat"));
        File misc = new File(world, "data/entomophobia_misc_world_data.dat");
        if (misc.isFile()) {
            System.out.println("Myiatic count: " + NbtIo.readCompressed(misc).getCompound("data").getInt("myiatic_mobcap"));
        }
    }

    private static void printSwarms(File file) throws IOException {
        if (!file.isFile()) {
            System.out.println("Swarms: no entomophobia_swarm_world_data.dat");
            return;
        }
        CompoundTag data = NbtIo.readCompressed(file).getCompound("data");
        int swarms = 0;
        for (int i = 0; data.contains("swarm" + i + "Type"); i++) {
            int units = 0;
            while (data.contains("swarm" + i + "unit" + units)) {
                units++;
            }
            System.out.println("Swarm " + i + ": type " + data.getByte("swarm" + i + "Type") + ", state " + data.getByte("swarm" + i + "State")
                    + ", " + units + " units, captain " + data.getUUID("swarm" + i + "CaptainUUID"));
            swarms++;
        }
        if (swarms == 0) {
            System.out.println("Swarms: none saved");
        }
    }

    private static void printTickets(String dimension, File file) throws IOException {
        if (!file.isFile()) {
            System.out.println(dimension + ": no chunks.dat");
            return;
        }
        CompoundTag data = NbtIo.readCompressed(file).getCompound("data");
        ListTag forced = data.getList("ForgeForced", Tag.TAG_COMPOUND);
        if (forced.isEmpty()) {
            System.out.println(dimension + ": no mod chunk tickets");
        }
        for (int i = 0; i < forced.size(); i++) {
            CompoundTag mod = forced.getCompound(i);
            ListTag chunks = mod.getList("ModForced", Tag.TAG_COMPOUND);
            int tickets = 0;
            StringBuilder where = new StringBuilder();
            for (int j = 0; j < chunks.size(); j++) {
                CompoundTag chunk = chunks.getCompound(j);
                int owners = 0;
                for (String key : new String[]{"Blocks", "TickingBlocks", "Entities", "TickingEntities"}) {
                    owners += chunk.getList(key, key.endsWith("Blocks") ? Tag.TAG_COMPOUND : Tag.TAG_INT_ARRAY).size();
                }
                tickets += owners;
                ChunkPos pos = new ChunkPos(chunk.getLong("Chunk"));
                where.append(' ').append(pos.x).append(',').append(pos.z).append('x').append(owners);
            }
            System.out.println(dimension + ": " + mod.getString("Mod") + " holds " + chunks.size() + " chunks with "
                    + tickets + " tickets:" + where);
        }
    }

    private static void printHives(File file) throws IOException {
        if (!file.isFile()) {
            System.out.println("Hives: no entomophobia_hive_world_data.dat");
            return;
        }
        CompoundTag data = NbtIo.readCompressed(file).getCompound("data");
        TreeSet<String> hearts = new TreeSet<>();
        for (String key : data.getAllKeys()) {
            if (key.endsWith("_corpsedew")) {
                hearts.add(key.substring(0, key.length() - "_corpsedew".length()));
            }
        }
        if (hearts.isEmpty()) {
            System.out.println("Hives: none saved");
        }
        for (String heart : hearts) {
            StringBuilder stored = new StringBuilder();
            for (int i = 0; data.contains(heart + "_stored_" + i); i++) {
                stored.append(' ').append(data.getString(heart + "_stored_" + i)).append('=')
                        .append(data.getInt(heart + "_stored_" + i + "_count"));
            }
            System.out.println("Hive " + heart + ": corpsedew " + data.getInt(heart + "_corpsedew") + ", stored:" + stored);
        }
    }
}
