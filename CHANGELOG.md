# Silkworms Changelog

## v0.6.0 — Survival Beta
- Release-prep milestone on the v0.5.8 feature baseline: refreshed README,
  Modrinth page draft, release test checklist, media shot list and GitHub
  release notes. No gameplay, art, sound, spawning or cleanup changes.
- The mod is now considered feature-complete for survival beta play:
  lifecycle, variants, bucket, taming, riding, combat assist, lush-cave
  spawning, config, and safe-removal tooling.

## v0.5.8
- Removed the legacy classic moth entirely: every moth renders the approved
  refined look; old `Style` NBT is ignored safely (any value, no crash).
  Renderer simplified to a single model; classic texture removed from the jar.

## v0.5.7
- The refined moth became the **default** adult moth (all old saved moths
  upgrade automatically; nothing in gameplay produces the classic look).
- Natural silkworm spawning in **lush caves**: packs of 2–5 near moss and
  lush vegetation, never in water. Spawn group moved to AMBIENT so
  underground spawning actually works; forest spawning preserved.

## v0.5.6
- Refined ("royal") moth beauty pass: broader swept wings (10×7 / 8×6),
  bolder layered eye-spot pattern, fuller feather antennae, and a head/neck
  fix (closed a 1-unit gap; added a subtle fluffy collar).

## v0.5.5
- Hotfix: classic moths rendered garbled when a refined moth was on screen —
  the model swap now happens per-entity at render time (vanilla cow pattern).

## v0.5.4
- Added the refined silk moth as a summon-only test style (`{Style:1}`),
  fully additive alongside the then-default classic moth.

## v0.5.3
- Ride polish: mounted flight aims 8° above the exact look pitch (no more
  accidental descent); other tamed moths keep up with a riding owner via
  faster chase, quicker re-pathing and target leading — teleport is now an
  emergency fallback only. Sprint auto-cruise investigated and deferred
  (vanilla sprint input cannot carry a hands-free cruise).

## v0.5.2 — Safety Release
- **New admin commands** (op level 2+): `/silkworms safety_audit`,
  `/silkworms removal_check` (dry run), `/silkworms prepare_removal` +
  `confirm` — cleanup-friendly removal while the mod is installed: tamed
  moths convert to named, tagged vanilla Allays; wild moths/worms/cocoons are
  removed; silkworm buckets become vanilla buckets + 1 string per stored worm.
- **Data hardening**: bucket variant lists clamp to capacity even if item data
  is edited/corrupted; legacy count-only buckets covered; all NBT reads
  already default safely (verified in audit).
- **Phase-out config toggles** (file-only, all default on):
  `enableSilkwormGrowth`, `enableCocoonHatching`, `enableMothTaming`,
  `enableMothRiding`.
- **Docs**: new `COMPATIBILITY_AND_REMOVAL.md` (registry stability rules,
  honest removal guide, Allay conversion note, loaded-chunk limitation),
  README compatibility/removal sections.
- **Metadata**: Mod Menu declared as `suggests`; dedicated-server startup
  added to the release test matrix.
- No gameplay, texture, model, riding or combat changes.

## v0.5.1
- Tamed moths grow to 2× size (vanilla SCALE attribute; model, hitbox and
  rider seat scale together).
- Wolf-style pet combat: defends the owner and assists on the owner's target;
  hostile mobs only, never creepers, never while ridden.

## v0.5.0
- Rideable tamed silk moth (owner-only, empty-hand mount, one passenger).
  Vanilla Happy Ghast-style controlled flight: look-steered, jump ascends,
  look-down descends, idle hover, sneak safe-dismount. ~2× Happy Ghast speed.

## v0.4.2
- Feeding/taming items are consumed only when they actually do something
  (growth, taming progress, or healing).

## v0.4.1
- Silkworm buckets remember each stored worm's variant; cocoons inherit the
  worm's variant with subtle tints. Legacy buckets keep working.

## v0.4.0
- Silkworm Bucket (holds up to 5 worms). Eight silkworm color variants with
  weighted natural spawning.

## v0.3.x
- Tameable silk moth companion (cherry leaves), harness visual, better-than-
  Allay following with elytra catch-up and air relocation, defensive combat,
  hanging cocoons, hand-feeding, config screen via Mod Menu.

## v0.1.0 – v0.2.x
- Core lifecycle (silkworm → cocoon → silk moth), concept-art models and
  textures, spawn eggs, natural spawning, Mod Menu config.
