# Silkworms — Release Test Checklist (v0.6.0 Survival Beta)

Automated gates (run per release):
- [ ] `./gradlew build` — BUILD SUCCESSFUL
- [ ] Client smoke: dev client reaches title screen, mod init logged, no
      missing textures / model-layer errors
- [ ] Dedicated server: `runServer` reaches `Done (…)!`, no client-class leak
- [ ] Cleanup commands compile/register

## Lifecycle
- [ ] Silkworm spawn egg works; worm grazes vegetation only (ground never
      converts to dirt); `mobGriefing false` stops grazing
- [ ] Hand-feed grass/leaves ×3 → worm seeks a hang spot → hanging cocoon
- [ ] Full-health, full-grown worm does NOT consume food
- [ ] Cocoon hangs under leaves/ceilings; support broken → drops, keeps growing
- [ ] Cocoon hatches into the refined moth; tint matches worm variant
- [ ] Wild moth flies gently, stays near ground, dies of old age
- [ ] Save/reload mid-cycle: timers resume, no duplicate moths

## Variants & bucket
- [ ] Mixed worm colours spawn; colours persist across save/reload
- [ ] Bucket holds 5, tooltip counts, variants remembered in order
- [ ] Legacy count-only buckets (pre-0.4.1) still work
- [ ] Cleanup converts buckets → vanilla bucket + string per worm

## Moth default look (post-0.5.8)
- [ ] `/summon silkworms:silk_moth ~ ~1 ~` → refined moth
- [ ] `{Style:1}`, `{Style:2}`, `{Style:999}` → refined moth, no crash
- [ ] No harness on untamed moths

## Taming & companion
- [ ] Cherry leaves ×3 tames (smoke → hearts); harness appears; 2× size
- [ ] Tamed + full health: cherry leaves NOT consumed; damaged: heals
- [ ] Follows owner; elytra catch-up; air-teleport only when far behind
- [ ] Other tamed moths keep up while owner rides one
- [ ] Defends owner vs hostile mobs; never creepers; never passive mobs
- [ ] Tamed moth never dies of old age

## Riding
- [ ] Owner-only, empty-hand mount; leashed moth refuses; can't leash ridden
- [ ] Look-steer (slight look-down stays level; steep dives), jump climbs,
      idle hovers, sneak dismounts safely (never inside blocks)
- [ ] AI paused while ridden; resumes on dismount; rider sits on harness

## Natural spawning
- [ ] Forest surface worms (uncommon)
- [ ] Lush caves: packs ~2–5 on moss/near azalea/cave vines; none in water
- [ ] `naturalSilkwormSpawning=false` (+ restart) stops both

## Config
- [ ] Mod Menu gear opens screen; sliders persist to `config/silkworms.json`
- [ ] Phase-out toggles work (growth, hatching, taming, riding)

## Safe removal (on a COPY)
- [ ] `removal_check` counts correctly with loaded-chunk warnings
- [ ] `prepare_removal confirm`: tamed → named tagged Allays; wild/worms/
      cocoons removed; buckets/eggs converted/removed; summary correct
- [ ] Remove jar → world reopens clean, Allays intact

## Modpack
- [ ] Loads in the full 119-mod pack; mobs render; no log spam
