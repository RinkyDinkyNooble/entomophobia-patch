## 1.1.0 (2026-10-15)

For Minecraft 1.20.1, Forge 47 and Entomophobia 1.1.1.

- Fixed: each Hive has one brain. Entomophobia gave a new Hive two, one more each time its chunk loaded again, and in single player one more each time the player came near it. Each bred and sent out hunts on its own timer, so Hives bred and hunted several times as often as intended until the next restart.
- Fixed: a Hive keeps its stored mobs when its chunk unloads and loads again. Before, its storage went back to what it was when the world loaded (nothing, for a Hive born that session), and mobs vanishing into it afterwards were lost at the next save.
- Fixed: a Hive whose Heart stays unloaded for a whole session keeps its storage. Before, it was dropped from the save.
- Fixed: the myiatic count behind the mob cap and the hunt limit only counts the myiatics loaded. Before, those loaded when the game closed were counted again every session, so the count crept up, myiatics far from players were culled early and hunts stopped.

Only needed on the server (or in single player).

## 1.0.0 (2026-10-15)

First version, for Minecraft 1.20.1, Forge 47 and Entomophobia 1.1.1.

- Faster: swarm captains and Hive Hearts no longer force-load chunks. Chunks an unpatched game left loaded are released when the world loads.
- Faster: nests only build, and Hives only send out hunt swarms, while a player is near the Hive Heart.
- Fixed: nests no longer replace chests, furnaces, machines, keypads, unbreakable blocks or blocks as blast-proof as obsidian. More can be spared with the block tag `entomophobiaperformancefix:nest_immune`.
- Fixed: the Hive no longer stores a copy of every myiatic whose chunk unloads, nor mobs that can't swarm (the Celestial Carrion). The config's `never_stored` lists more.
- Fixed: a hurt Hive Heart sends at most one swarm every 5 seconds (config `retaliation_cooldown_ticks`).
- Fixed: Entomophobia's commands need operator rights.

Only needed on the server (or in single player); a client without it can join.
