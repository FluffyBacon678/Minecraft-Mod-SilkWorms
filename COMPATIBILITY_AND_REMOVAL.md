# Silkworms — Compatibility & Removal Guide

This document is the honest contract between the mod and your world save.

## What this mod adds to your save

Silkworms adds **persistent entities, items, and item data**:

| Kind | IDs (never renamed — see stability rules) |
|---|---|
| Entities | `silkworms:silkworm`, `silkworms:cocoon`, `silkworms:silk_moth` |
| Items | `silkworms:silkworm_spawn_egg`, `silkworms:cocoon_spawn_egg`, `silkworms:silk_moth_spawn_egg`, `silkworms:silkworm_bucket` |
| Item data components | `silkworms:silkworm_count`, `silkworms:silkworm_variants` |
| Config file | `config/silkworms.json` (plain JSON, harmless if left behind) |

There are **no custom blocks, block entities, dimensions, world data, mixins,
access wideners, or networking** — the save-surface is exactly the table above.

## Registry stability rules (save-file API)

Registry IDs are treated as **public save-file API**:

1. The IDs above are never renamed casually.
2. If a rename is ever required, the old ID stays registered and content is
   migrated; the old ID is deprecated, not deleted.
3. An old ID is never deleted in a minor release while existing worlds may
   still contain it.
4. The entity NBT keys (`EatenPlants`, `EatCooldown`, `GrowthTimer`, `Hanging`,
   `LifeTicks`, `TameFeeds`, `Variant`) and config keys follow the same rule:
   readers always tolerate missing/invalid values (safe defaults, clamping).

## Honesty statement

> Silkworms adds persistent entities, items, and item data. Removing the jar
> without cleanup may leave missing content or lost entities/items. The
> recommended removal path is to run the cleanup command while the mod is
> still installed.

This mod is **not** "safe to remove" in the magical sense — no content mod is.
It is **cleanup-friendly**: it ships tools to convert or remove its content
before you uninstall.

## Safe removal steps

1. **Back up the world.** Really.
2. Start the world/server **with Silkworms still installed**.
3. Run `/silkworms removal_check` (dry run — counts content, changes nothing).
4. Run `/silkworms prepare_removal confirm`.
5. Visit/load any important areas where moths, cocoons or worms live, and
   repeat steps 3–4 there.
6. Move mod items out of placed chests into your inventory first — placed
   containers are **not** scanned (see limitations).
7. Save and stop the world/server.
8. Remove the Silkworms jar.
9. Reopen the world.

## What the cleanup does

| Content | Cleanup result |
|---|---|
| **Tamed silk moths** | Converted to **vanilla Allays** at the same position, keeping their custom name, marked persistent, tagged `converted_from_silk_moth` and `silkworms_cleanup_v052`. The moth is only removed after the Allay spawned successfully. |
| Wild silk moths | Removed (not converted — avoids flooding the world with Allays). |
| Silkworms | Removed, no drops. |
| Cocoons | Removed, no drops. |
| Silkworm buckets (player inventories, ender chests, dropped items) | Converted to **vanilla buckets**, plus 1 string per stored worm (max 5 — legacy count-only buckets handled safely). |
| Spawn eggs | Removed and counted in the summary. |

### Allay conversion note — read this

The Allay is a **vanilla-safe friendly stand-in, not the same pet**. It keeps
the position and name of your tamed moth. It does **not** keep riding, the
harness, the 2× size, color variants, combat assistance, or owner behavior.

### Loaded chunk limitation

> The cleanup command can only reliably process loaded areas and accessible
> inventories. If you have silkworm content in unloaded chunks, load those
> areas and run cleanup again before removing the mod.

Additionally, items inside **placed containers** (chests, barrels, shulkers in
item form, etc.) are not scanned — carry them in a player inventory or ender
chest when you run cleanup.

## What happens if you remove the jar WITHOUT cleanup

Tested honestly on a copied world:

- All silkworms, cocoons and silk moths — **including tamed pet moths** — are
  silently discarded by Minecraft as chunks load. They are gone permanently.
- Silkworm buckets (including stored worms) and spawn eggs vanish from
  inventories and containers as they load.
- **No crash or world corruption is expected**: the mod has no custom blocks or
  world data, so nothing invalid lingers. You lose content, not the world.
- `config/silkworms.json` remains as a harmless leftover text file.

## Phase-out config

`config/silkworms.json` includes toggles to wind the mod down gradually before
removal (all default `true`):

- `naturalSilkwormSpawning` (needs restart)
- `enableSilkwormGrowth` — worms stop pupating/growing from feeding
- `enableCocoonHatching` — cocoon timers pause
- `enableMothTaming` — cherry leaves stop taming (tamed moths still heal)
- `enableMothRiding` — mounting disabled

Cleanup commands are always available regardless of these toggles.

## Client/server requirements

- Silkworms must be installed on **both server and client** in multiplayer
  (it registers entities and items — vanilla or un-modded clients cannot join
  a Silkworms server, and a Silkworms client cannot expect content on a
  vanilla server).
- All rendering code lives in a split client source set and never loads on a
  dedicated server. Dedicated server startup is part of the release test.
- Commands are server-side only (registered via Fabric's command API,
  operator permission level 2+).
- Mod Menu is optional (`suggests`); its absence is safe.
- Dependencies: Fabric Loader ≥ 0.16.0, Fabric API, Minecraft ~1.21.11, Java 21.
- No known conflicts with other mods; none are declared, on purpose.
