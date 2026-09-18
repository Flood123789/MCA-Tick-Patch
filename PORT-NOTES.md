# MCA Tick Usage Patch — 1.21.1 port notes
Source: `D:\MC\1.20.1\MCA Tick Rate` — mappings: **Yarn**, lang: Java + Kotlin entry point
Port:   `D:\MC\1.21.1\ports\mca-tick-usage-patch` — clone of the source repo, branch **`1.21.1`**
GitHub: https://github.com/Flood123789/MCA-Tick-Patch (`main` = 1.20.1, `1.21.1` = this port)
Mod id: `mca-tick-usage-patch` 1.1.0 (was 1.0.1 on 1.20.1)

## What it does
Two features for Minecraft Comes Alive villagers:
1. **Navigation mode switch** (`config/mca-tick-usage-patch.json`, `mode`: `MCA` | `VANILLA`). `VANILLA`
   replaces MCA's custom villager navigation with vanilla `MobNavigation` to cut tick cost.
   Applies to villagers created after the change (`/mcapath reload`).
2. **Pathfinding instrumentation** (`instrumentation: true`): counters for path searches, node candidates
   and collision checks, shown by `/mcapath stats` (op level 2), cleared by `/mcapath reset`.

## Bones
- Entry point: `MCATickUsagePatch` (Kotlin object) → loads `PatchConfig`, registers `/mcapath`,
  END_SERVER_TICK → `PathStats.endServerTick()` (per-tick maxima), logs whether MCA's classes exist.
- `PatchConfig` (Java): JSON config at `config/mca-tick-usage-patch.json`.
- `PathStats` (Java): static counters + `Snapshot` record.
- Mixins (both `@Pseudo`, string targets, `remap = false`, so names are **runtime** names):
  - `VillagerEntityMCAMixin` → `net.conczin.mca.entity.VillagerEntityMCA.method_5965`
    (`createNavigation(World)`, intermediary because MCA overrides vanilla). HEAD, cancellable.
  - `MCAWalkNodeEvaluatorMixin` → `net.conczin.mca.entity.ai.navigation.MCAWalkNodeEvaluator`:
    `method_21` (getStart, once per search), `method_17` (getDefaultNodeType, per candidate),
    `hasExactBlockClearance` (MCA's own collision test). All `require = 0`.

## MCA changed under us (7.7.1 → 7.7.36-beta.3)
- **Package moved**: `fabric.net.mca.*` → `net.conczin.mca.*`.
- **Pathfinder rewritten**: `entity.ai.pathfinder.VillagerLandPathNodeMaker` is gone. MCA 1.21.1 has
  `entity.ai.navigation.MCAGroundPathNavigation` + `MCAWalkNodeEvaluator` (extends vanilla
  `LandPathNodeMaker` / `class_14`), plus `PathfindingBlacklist` and a block tag
  `mca:villager_pathfinding_collision_checks`.
- MCA's `method_6100` (setJumping) checks `getNavigation() instanceof MCAGroundPathNavigation` to let the
  navigation control climbing. In `VANILLA` mode that check is false, so villagers jump normally but
  lose MCA's ladder/climb handling. Expected trade-off of vanilla mode.

## Port changes (commit on branch 1.21.1)
- Toolchain: Minecraft 1.21.1, Yarn 1.21.1+build.3, Fabric API 0.116.17+1.21.1, FLK 1.14.1 (Kotlin 2.4.20), Java 21.
- Retargeted the villager mixin to `net.conczin.mca.entity.VillagerEntityMCA`.
- Replaced `VillagerLandPathNodeMakerMixin` with `MCAWalkNodeEvaluatorMixin` (mapping table in its javadoc).
  `landNodes` and `neighborChecks` counters have no MCA 1.21 equivalent (those became vanilla statics);
  `/mcapath stats` now prints them as `n/a` instead of a misleading 0.
- Instrumentation hooks are `require = 0` so a future MCA update can't crash the game through diagnostics.
- `suggests: mca >= 7.7.36`.

## Untested
- Not yet run in game. Test: `/mcapath stats` with `instrumentation: true` near a village → counters rise;
  set `mode: VANILLA`, `/mcapath reload`, spawn a new MCA villager → it still walks to its job site.

## Status: built, installed in the test instance, shipped in the .mrpack, branch pushed to GitHub
