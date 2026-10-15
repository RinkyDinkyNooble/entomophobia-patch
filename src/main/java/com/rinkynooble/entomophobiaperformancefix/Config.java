package com.rinkynooble.entomophobiaperformancefix;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** {@code config/entomophobiaperformancefix-common.toml}. */
public final class Config {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> NEVER_STORED;
    private static final ForgeConfigSpec.IntValue RETALIATION_COOLDOWN;

    private static volatile Set<EntityType<?>> neverStored = Set.of();
    private static volatile int retaliationCooldown = 100;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("hive");
        NEVER_STORED = builder
                .comment("Mobs a Hive never takes into its storage when they despawn, so its swarms never bring them back.",
                        "The Hive Heart and the Celestial Carrion are never stored anyway (they can't join swarms).",
                        "Example: [\"entomophobia:myiatic_cow\"]")
                .defineListAllowEmpty("never_stored", List.of(), o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
        RETALIATION_COOLDOWN = builder
                .comment("Ticks between two swarms a hurt Hive Heart sends out (20 ticks = 1 second).",
                        "Entomophobia sends one on every hit while the Heart is under a fifth of its health or has fewer than 3 swarmers near it.",
                        "0 = no cooldown, like Entomophobia without this patch.")
                .defineInRange("retaliation_cooldown_ticks", 100, 0, 72000);
        builder.pop();
        SPEC = builder.build();
    }

    private Config() {
    }

    public static boolean isNeverStored(EntityType<?> type) {
        return neverStored.contains(type);
    }

    public static int retaliationCooldown() {
        return retaliationCooldown;
    }

    static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            refresh();
        }
    }

    static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            refresh();
        }
    }

    private static void refresh() {
        Set<EntityType<?>> types = new HashSet<>();
        for (String id : NEVER_STORED.get()) {
            ResourceLocation key = ResourceLocation.tryParse(id);
            EntityType<?> type = key == null ? null : ForgeRegistries.ENTITY_TYPES.getValue(key);
            // getValue falls back to the default type (pig) for unknown ids.
            if (type != null && key.equals(ForgeRegistries.ENTITY_TYPES.getKey(type))) {
                types.add(type);
            } else {
                EntomophobiaPerformanceFix.LOGGER.warn("Unknown mob in never_stored, ignored: {}", id);
            }
        }
        neverStored = Set.copyOf(types);
        retaliationCooldown = RETALIATION_COOLDOWN.get();
    }
}
