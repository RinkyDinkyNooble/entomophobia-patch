# Entomophobia Performance Fix (Unofficial Patch)

A companion mod for Entomophobia 1.1.1 (Forge 1.20.1) that stops it keeping chunks loaded forever and building nests where no player is, stops its nests replacing chests and other protected blocks, fixes its Hives' brains, storage and mob count, and makes its commands operator-only. Entomophobia itself is still required.

## Before And After

The same test on a server with no player online, without the patch and then with it.

- A swarm captain killed after crossing two chunks: **9 chunks** stayed loaded for good without the patch, **0** with it. A Hive Heart kept **1 more** near the world's origin. Loading that world with the patch released all **10**.
- A nest 3,000 blocks from everything: without the patch it built anyway, generating **2.4 MB** of terrain across 4 region files in 11 minutes; with it, **nothing**, it waits for a player.
- A chest and a furnace in a nest's wall, and a chest in its hollow: without the patch **all three** were replaced; with it **all three** stayed.
- Four myiatics whose chunk unloaded: without the patch the Hive stored **4 copies** and sent them out again while the originals stayed in their chunk; with it, **0**.
- A Hive Heart at 15 health, hit once a second: without the patch, 3 hits called **3 swarms** (23 of its 24 stored mobs); with it, 4 hits called **1 swarm**.
- A new Hive: without the patch it had **2 brains**, each breeding and hunting on its own, and **4** after its chunk unloaded and loaded once; with it, **1**.
- A Hive holding 10 mobs whose chunk unloaded and loaded again: without the patch its saved storage went back to **0**, the next mobs vanished into a copy that was never saved, and after two restarts it held **0**. With it, the Hive kept all **23** it gathered, through both restarts, one of them with the Hive unloaded the whole session.
- 10 myiatics loaded when the server stopped, then loaded again: without the patch the mob cap counted them **twice** (20), so adding 41 more culled **11** real ones (cap 50); with it the count was 10 and **1** was culled.

## Faster

- Swarm captains no longer force-load the 3×3 chunks around them. Entomophobia never gave them back when a captain died or its swarm ended, and Forge saves them with the world, so every captain killed in a fight left up to 9 chunks loaded for good, with the mobs in them ticking.
- A Hive Heart no longer keeps a chunk near the world's origin loaded (it meant to load its own chunk but worked out the wrong one).
- Worlds played without the patch are cleaned when they load: the chunks Entomophobia left loaded are released, and the log says how many.
- A nest only builds while a player is near its Hive Heart. Before, a nest far from every player kept building every 2.5 seconds, loading (or generating) each chunk it touched on the server thread. It carries on when a player comes back.
- A Hive only sends out hunt swarms while a player is near its Heart. Before, it kept sending them above nests nobody was near, loading those chunks and leaving the mobs frozen there.

## Bug Fixes

- Nests no longer replace protected blocks: blocks that hold a block entity (chests, Lootr chests, furnaces, spawners, machines, SecurityCraft's keypads), blocks that can't be broken, and blocks at least as blast-proof as obsidian (SecurityCraft's reinforced blocks). Ordinary blocks and Entomophobia's own flesh are replaced as before. A datapack can spare more blocks with the block tag `entomophobiaperformancefix:nest_immune`.
- The Hive only stores myiatics that despawn. Before, it also stored every myiatic whose chunk unloaded, while that mob stayed saved in its chunk, so the Hive gained a copy each time a player walked away. It no longer stores the Celestial Carrion, and the config can list more mobs it never stores (`never_stored`).
- Each Hive has one brain. Entomophobia gave a new Hive two, one more each time its chunk loaded again, and in single player one more each time the player came near it; each bred and sent out hunts on its own timer, so Hives bred and hunted several times as often as intended until the next restart.
- A Hive keeps its storage when its chunk unloads and loads again, and when it stays unloaded for a whole session. Before, a reload reset it to what it was when the world loaded (nothing, for a Hive born that session) and the mobs that vanished into it afterwards were lost; a Hive not loaded during a session was dropped from the save.
- Entomophobia's myiatic count (behind its mob cap and the hunt limit) only counts the myiatics loaded. Before, those loaded when the game closed were counted again every session, so the count crept up: myiatics far from players were culled early and hunts stopped.
- A hurt Hive Heart sends at most one swarm every 5 seconds (config `retaliation_cooldown_ticks`, 0 for every hit as before). Before, it sent one on every hit, so a fast weapon emptied its storage in a moment.
- `/entomophobia:nest`, `/entomophobia:nest_actions` and `/entomophobia:thanks` need operator rights. Before, any player could run them, even with cheats off.

Swarms, their glowing outlines and mobs vanishing back into the Hive work as before.

The log has one line for each patched class, saying how many changes applied. Only needed on the server (or in single player).

### Credit

This is an unofficial patch, not affiliated with Entomophobia. [Entomophobia](https://www.curseforge.com/minecraft/mc-mods/entomophobia) is made by Dr. Pilot.

## Building

Copy `entomophobia-1.1.1_cow_rework.jar` to `libs/entomophobia-1.1.1.jar` and `geckolib-forge-1.20.1-4.8.3.jar` to `libs/geckolib-4.8.3.jar` (both gitignored), and run `gradlew build`.

`gradlew readSave -Pworld=<world folder>` is a dev-only tool (`src/verify`, never shipped). It prints the chunk tickets each mod saved in a world and every Hive's storage, which is how the numbers above were read. `gradlew verifyJar` builds the dev-only `/entoverify` commands (a separate jar, never shipped) that the Hive tests load into the test server, with or without the patch, to print each Hive's brains and storage and the myiatic count.

## License

0BSD for this mod's code. Entomophobia itself is All Rights Reserved and isn't included, nor are any of its assets. The icon is drawn from scratch by `curseforge/MakeIcon.java` (`java curseforge/MakeIcon.java curseforge/icon.png`).
