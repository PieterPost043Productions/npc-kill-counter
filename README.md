# NPC Kill Counter

A RuneLite plugin that tracks how many of each NPC type you've killed. Simple kill counter, so you can keep track of how many NPCs are left to kill for getting a drop (statistically).

## Features

- Counts kills per NPC type, tracked via `NpcLootReceived` (fires whenever you get credit for a kill, including empty drops), so kills from other players aren't counted.
- On-screen overlay showing your kill counts while you play (can be toggled off in the plugin settings).
- Sidebar panel listing every NPC you've killed and its kill count.
- Reset button per NPC, plus a "Reset all" button to clear everything at once.
- Kill counts persist across client restarts (stored via RuneLite's `ConfigManager`).

## Building

Requires JDK 11+ and Gradle.

```
git clone https://github.com/PieterPost043Productions/npc-kill-counter.git
cd npc-kill-counter
gradle build
```

The built jar will be at `build/libs/npc-kill-counter-1.1.0.jar`.

## Installing locally (sideloading)

The official RuneLite launcher doesn't load arbitrary plugins from disk — developer mode is required, and developer mode only works when the client is launched directly (not through the RuneLite launcher or the Jagex Launcher). The supported way to test plugins locally is to open this project in IntelliJ IDEA using the [RuneLite example-plugin](https://github.com/runelite/example-plugin) workflow and run it via the Gradle `run` task.

## License

BSD 2-Clause, see [LICENSE](LICENSE).

## Author

PieterPost43
