## 1.0.0 (2026-10-15)

First version, for Minecraft 1.20.1, Forge 47 and Entomophobia 1.1.1.

- Faster: swarm captains and Hive Hearts no longer force-load chunks. Chunks an unpatched game left loaded are released when the world loads.
- Faster: nests only build, and Hives only send out hunt swarms, while a player is near the Hive Heart.
- Fixed: nests no longer replace chests, furnaces, machines, keypads, unbreakable blocks or blocks as blast-proof as obsidian. More can be spared with the block tag `entomophobiaperformancefix:nest_immune`.
- Fixed: the Hive no longer stores a copy of every myiatic whose chunk unloads, nor mobs that can't swarm (the Celestial Carrion). The config's `never_stored` lists more.
- Fixed: a hurt Hive Heart sends at most one swarm every 5 seconds (config `retaliation_cooldown_ticks`).
- Fixed: Entomophobia's commands need operator rights.

Only needed on the server (or in single player); a client without it can join.
