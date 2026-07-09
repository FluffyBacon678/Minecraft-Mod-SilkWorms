# Silkworms

A small, vanilla-friendly ambient life cycle mod for **Minecraft Fabric 1.21.11**.

```
Silkworm  →  Cocoon  →  Silk Moth
```

- **Silkworm** – a tiny passive ground creature that crawls slowly and grazes grass
  vegetation. After eating 3 valid plants it becomes a cocoon.
- **Cocoon** – a stationary pupa that incubates on a growth timer, then hatches a moth.
- **Silk Moth** – a gentle, bee-sized flyer that drifts around and eventually dies naturally.

Everything persists across world reloads, and the mod is deliberately lightweight so it
behaves in a heavy modpack.

## Important behaviour

**The silkworm never eats grass *blocks*.** It only removes grass *vegetation* that sits on
top of the ground — short grass, tall grass, fern and large fern — and always leaves the
ground block untouched (no `grass_block → dirt`). It also respects the `mobGriefing` game
rule: with `mobGriefing false`, silkworms don't remove any vegetation.

## Environment

| | |
|---|---|
| Minecraft | 1.21.11 |
| Loader | Fabric |
| Yarn mappings | 1.21.11+build.6 |
| Fabric API | 0.141.3+1.21.11 |
| Loom | 1.14.10 |
| Java | 21 |

## Building

```sh
./gradlew build
```

The remapped mod jar is written to `build/libs/silkworms-<version>.jar`
(use the file **without** the `-sources` suffix).

> This machine's default Java is 17, but the mod targets Java 21. Point Gradle at a JDK 21
> when building, e.g. `JAVA_HOME=/path/to/jdk-21 ./gradlew build`.

## Testing in-game

1. Drop the built jar into your `.minecraft/mods` folder (alongside Fabric API).
2. In a creative world open the **Spawn Eggs** tab — Silkworm, Cocoon and Silk Moth eggs
   are all there for testing each stage independently.
3. Spawn a silkworm on grass with tall/short grass nearby and watch it graze, then pupate.

## Balance

All tuning values live in one place:
[`SilkwormsBalance.java`](src/main/java/com/fluffybacon/silkworms/SilkwormsBalance.java)
(plants required, movement/eat speeds, cocoon growth time, moth lifetime, spawn weight, …).

## Compatibility

Silkworms is deliberately low-risk for modpacks: **no mixins, no access
wideners, no custom networking, no blocks or world data** — just entities,
items and two item data components under the `silkworms:` namespace. It is
verified against a 119-mod Fabric 1.21.11 pack (Sodium/Iris/Lithium etc.).
Multiplayer requires the mod on **both server and client**. Mod Menu is
optional. See [COMPATIBILITY_AND_REMOVAL.md](COMPATIBILITY_AND_REMOVAL.md)
for the full compatibility notes and registry stability rules.

## Removing the mod

Silkworms adds persistent entities, items, and item data. Removing the jar
without cleanup may leave missing content or lost entities/items (including
tamed pet moths). The recommended removal path is to run the cleanup command
**while the mod is still installed**:

1. Back up the world.
2. Run `/silkworms removal_check` (dry run), then
   `/silkworms prepare_removal confirm` (op level 2+).
3. Load any remaining important areas and run it again.
4. Save, stop, remove the jar, reopen.

Tamed moths are converted to vanilla Allays (same spot, same name — a
friendly stand-in, not the same pet). Full details, limitations and honesty
notes: [COMPATIBILITY_AND_REMOVAL.md](COMPATIBILITY_AND_REMOVAL.md).

## Version 1 scope

Included: the three entities, spawn eggs, modest natural silkworm spawning (forest biomes),
the grass-vegetation grazing, the full transform chain, persistent timers and placeholder
art. Intentionally **not** included yet: taming, breeding, silk items/economy, mulberry
trees, custom drops, and complex animation.
