# Entomophobia Performance Fix <span style="color:#e03e2d">(Unofficial Patch)</span>

A companion mod for **Entomophobia** 1.1.1 (Forge 1.20.1) that <span style="color:#2dc26b"><strong>stops it keeping chunks loaded forever</strong></span> and <span style="color:#2dc26b"><strong>building nests where no player is</strong></span>, stops its nests replacing chests and other protected blocks, fixes its Hives' brains, storage and mob count, and makes its commands operator-only. <span style="color:#e03e2d"><strong>Entomophobia itself is still required.</strong></span>

***

## Requirements

<table>
  <tr>
    <th align="left">Requirement</th>
    <th align="left">Version</th>
  </tr>
  <tr>
    <td><strong>Minecraft</strong></td>
    <td>1.20.1 (Forge)</td>
  </tr>
  <tr>
    <td><strong>Entomophobia</strong></td>
    <td><a href="https://www.curseforge.com/minecraft/mc-mods/entomophobia">1.1.1</a></td>
  </tr>
</table>

***

## Faster

*   **Swarm captains** no longer force-load the 3×3 chunks around them. Entomophobia never gave them back when a captain died or its swarm ended, and Forge saves them with the world, so every captain killed in a fight left <span style="color:#e03e2d"><strong>up to 9 chunks loaded for good</strong></span>, with the mobs in them ticking.
*   **A Hive Heart** no longer keeps a chunk near the world's origin loaded (it meant to load its own chunk but worked out the wrong one).
*   **Worlds played without the patch are cleaned when they load:** the chunks Entomophobia left loaded are released, and the log says how many.
*   **A nest only builds while a player is near its Hive Heart.** Before, a nest far from every player kept building every 2.5 seconds, loading (or generating) each chunk it touched on the server thread. It carries on when a player comes back.
*   **A Hive only sends out hunt swarms while a player is near its Heart.** Before, it kept sending them above nests nobody was near, loading those chunks and leaving the mobs frozen there.

***

## Credit

This is an **unofficial patch**, not affiliated with Entomophobia. [Entomophobia](https://www.curseforge.com/minecraft/mc-mods/entomophobia) is made by Dr. Pilot.
