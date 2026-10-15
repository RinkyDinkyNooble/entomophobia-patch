# Entomophobia Performance Fix (Unofficial Patch)

A companion mod for Entomophobia 1.1.1 (Forge 1.20.1) that stops it keeping chunks loaded forever and building nests where no player is, stops its nests replacing chests and other protected blocks, fixes its Hive storing copies of mobs, and makes its commands operator-only. Entomophobia itself is still required.

## Before And After

The same test on a server with no player online, without the patch and then with it.

- A swarm captain killed after crossing two chunks: **9 chunks** stayed loaded for good without the patch, **0** with it. A Hive Heart kept **1 more** near the world's origin. Loading that world with the patch released all **10**.
- A nest 3,000 blocks from everything: without the patch it built anyway, generating **2.4 MB** of terrain across 4 region files in 11 minutes; with it, **nothing**, it waits for a player.
- A chest and a furnace in a nest's wall, and a chest in its hollow: without the patch **all three** were replaced; with it **all three** stayed.
- Four myiatics whose chunk unloaded: without the patch the Hive stored **4 copies** and sent them out again while the originals stayed in their chunk; with it, **0**.
- A Hive Heart at 15 health, hit once a second: without the patch, 3 hits called **3 swarms** (23 of its 24 stored mobs); with it, 4 hits called **1 swarm**.

## Faster

- Swarm captains no longer force-load the 3×3 chunks around them. Entomophobia never gave them back when a captain died or its swarm ended, and Forge saves them with the world, so every captain killed in a fight left up to 9 chunks loaded for good, with the mobs in them ticking.
- A Hive Heart no longer keeps a chunk near the world's origin loaded (it meant to load its own chunk but worked out the wrong one).
- Worlds played without the patch are cleaned when they load: the chunks Entomophobia left loaded are released, and the log says how many.
- A nest only builds while a player is near its Hive Heart. Before, a nest far from every player kept building every 2.5 seconds, loading (or generating) each chunk it touched on the server thread. It carries on when a player comes back.
- A Hive only sends out hunt swarms while a player is near its Heart. Before, it kept sending them above nests nobody was near, loading those chunks and leaving the mobs frozen there.

## Bug Fixes

- Nests no longer replace protected blocks: blocks that hold a block entity (chests, Lootr chests, furnaces, spawners, machines, SecurityCraft's keypads), blocks that can't be broken, and blocks at least as blast-proof as obsidian (SecurityCraft's reinforced blocks). Ordinary blocks and Entomophobia's own flesh are replaced as before. A datapack can spare more blocks with the block tag `entomophobiaperformancefix:nest_immune`.
- The Hive only stores myiatics that despawn. Before, it also stored every myiatic whose chunk unloaded, while that mob stayed saved in its chunk, so the Hive gained a copy each time a player walked away. It no longer stores the Celestial Carrion, and the config can list more mobs it never stores (`never_stored`).
- A hurt Hive Heart sends at most one swarm every 5 seconds (config `retaliation_cooldown_ticks`, 0 for every hit as before). Before, it sent one on every hit, so a fast weapon emptied its storage in a moment.
- `/entomophobia:nest`, `/entomophobia:nest_actions` and `/entomophobia:thanks` need operator rights. Before, any player could run them, even with cheats off.

Swarms, their glowing outlines and mobs vanishing back into the Hive work as before.

The log has one line for each patched class, saying how many changes applied. Only needed on the server (or in single player).

### Credit

This is an unofficial patch, not affiliated with Entomophobia. [Entomophobia](https://www.curseforge.com/minecraft/mc-mods/entomophobia) is made by Dr. Pilot.
