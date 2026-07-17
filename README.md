# Silkworms

A gentle, vanilla-friendly life-cycle and companion mod for **Minecraft Fabric 1.21.11**.

```
Silkworm  →  Cocoon  →  Silk Moth
```

Raise tiny silkworms into elegant silk moths — then tame one and fly it.

- **Silkworm** – a tiny passive grazer with 8 natural colour morphs. Eats grass
  *vegetation* (never grass blocks), or hand-feed it grass/leaves. When full it
  seeks a leafy ceiling and pupates, hanging from the underside.
- **Cocoon** – a hanging pupa that inherits the worm's colour with a subtle
  tint, incubates on a timer, then hatches a moth.
- **Silk Moth** – an elegant cream-and-tan flyer with layered eye-spot wings.
  Wild moths live out a natural lifespan; tamed ones are forever.

## Features (v0.6.0 Survival Beta)

- **Full lifecycle** with persistent timers, colour-variant inheritance
  (worm → cocoon) and hanging pupation under leaves and ceilings.
- **Natural spawning**: forest surfaces, plus discoverable little colonies in
  **lush caves** (packs of 2–5 on the moss — never in water).
- **Silkworm Bucket**: carry up to 5 worms in one bucket; it remembers each
  worm's colour and releases them one at a time.
- **Tameable companion**: feed a moth cherry leaves ×3. Tamed moths grow to
  2× size, wear a harness, never die of old age, follow better than an Allay
  (elytra catch-up + air relocation), and defend you wolf-style — hostile
  mobs only, never creepers, never while ridden.
- **Rideable**: owner-only, empty-hand mount. Look-steered flight (aimed
  slightly above your look so glancing down doesn't dive), jump to climb,
  hover on idle, sneak to dismount safely. ~2× Happy Ghast speed.
- **Mod Menu config**: lifecycle timings, spawn toggle, phase-out switches.
- **Honest safe-removal tooling**: `/silkworms removal_check` and
  `/silkworms prepare_removal confirm` convert tamed moths to named vanilla
  Allays and clean up everything else before you uninstall.

## Environment

| | |
|---|---|
| Minecraft | 1.21.11 |
| Loader | Fabric (≥ 0.16.0) |
| Fabric API | required |
| Mod Menu | optional |
| Java | 21 |
| Server/client | required on **both** in multiplayer |

## Building

```sh
./gradlew build
```

The remapped mod jar is written to `build/libs/silkworms-<version>.jar`
(use the file **without** the `-sources` suffix). Requires a JDK 21
(`JAVA_HOME=/path/to/jdk-21 ./gradlew build` if your default differs).

## Getting started in-game

1. Drop the jar into `.minecraft/mods` alongside Fabric API.
2. **Survival**: explore a lush cave — silkworms live on the moss. Scoop some
   up with a bucket, feed them grass or leaves, and watch the cycle.
3. **Creative**: the Spawn Eggs tab has Silkworm, Cocoon and Silk Moth eggs
   for testing each stage; the Tools tab has the Silkworm Bucket.
4. Tame a hatched moth with **cherry leaves ×3**, then right-click it with an
   empty hand to fly.

## Balance

All tuning values live in one place:
[`SilkwormsBalance.java`](src/main/java/com/fluffybacon/silkworms/SilkwormsBalance.java)
(plants required, timers, spawn weights, flight speeds, follow/teleport
distances, …). Player-facing timings are also in the Mod Menu config.

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

## Roadmap (post-beta)

Subtle particles, adult moth micro-variants, silk economy (shears on
cocoons), breeding, and an optional auto-cruise flight key. See
[CHANGELOG.md](CHANGELOG.md) for the full version history.
