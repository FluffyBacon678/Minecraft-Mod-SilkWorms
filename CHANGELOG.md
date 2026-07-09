# Silkworms Changelog

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
