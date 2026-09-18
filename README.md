# MCA Tick Usage Patch

A conservative Fabric 1.20.1 compatibility mod for measuring and reducing the cost of Minecraft
Comes Alive (MCA) villager pathfinding.

This first-stage patch does not rewrite MCA's pathfinder. It provides a configurable vanilla
navigation baseline and aggregate instrumentation so the gameplay/performance tradeoff can be
measured before deeper classifier caching is added.

## Modes

The config is created at `config/mca-tick-usage-patch.json`.

- `MCA`: preserves MCA's custom navigation and path/grass preferences. This is the default.
- `VANILLA`: newly created or newly loaded MCA villagers use vanilla mob navigation. Relationships,
  dialogue, professions, families, and other MCA systems remain intact, but custom path preference
  and some custom door/hazard behavior are lost.
- `instrumentation`: enables aggregate hot-path counters. Set it to `false` for final benchmarks
  after gathering diagnostic counts.

Changing navigation mode requires villagers to be reloaded. Restarting the server/world is the
clearest benchmark procedure.

## Commands

Operator permission level 2 is required.

- `/mcapath stats`: show totals and recent per-tick maxima.
- `/mcapath reset`: clear all counters.
- `/mcapath reload`: reload the JSON config. Navigation mode applies to newly created/loaded MCA
  villagers.

## Build

```powershell
.\gradlew.bat build
```

The distributable jar is written to `build/libs/mca-tick-usage-patch-1.0.1.jar`.

To validate a dev server against local MCA and Architectury jars:

```powershell
.\gradlew.bat runServer "-PmcaJar=C:\path\to\minecraft-comes-alive.jar" "-ParchitecturyJar=C:\path\to\architectury.jar"
```

## Benchmark

Use a copy of the same world and observe the same populated village without traveling:

1. Run stock MCA without this patch.
2. Run this patch in `MCA` mode with instrumentation enabled.
3. Run it in `VANILLA` mode.
4. Repeat the useful mode with instrumentation disabled.

Let each run settle for at least one minute, reset counters, then capture two to three minutes of
Spark data. Compare median, p95, and maximum MSPT as well as `/mcapath stats`.

## License

CC0-1.0. This implementation uses focused Mixins and does not copy MCA implementation code.
