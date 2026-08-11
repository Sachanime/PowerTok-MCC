# PowerTok-MCC
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/Sachanime/PowerTok-MCC)<br>
[![](https://jitpack.io/v/Sachanime/PowerTok-MCC.svg)](https://jitpack.io/#Sachanime/PowerTok-MCC)

PowerTok-MCC is a core library plugin for Minecraft servers running on PaperMC. It provides a suite of convenient manager classes designed to simplify common plugin development tasks such as world management, UI creation, and command handling. This plugin is intended to be used as a dependency for other PowerTok plugins.

## Features

This plugin offers several manager classes to streamline development:

*   **`AdvancedWorldManager`**: A powerful world manager leveraging the [AdvancedSlimePaper API](https://github.com/infernalsuite/AdvancedSlimePaper). It allows for the creation, deletion, and conversion of worlds from templates. It's optimized for creating temporary minigame worlds efficiently.
*   **`BossbarManager`**: Simplifies the creation, display, and modification of BossBars for players.
*   **`ScoretabManager`**: Provides utilities for creating and managing player scoreboards and their objectives.
*   **`TitleManager`**: A helper class to easily create and display titles and subtitles to players.
*   **`CommandManager`**: A handler for registering and executing custom commands using Paper's Brigadier command API.
*   **`WorldManager` (Deprecated)**: The legacy world manager. It is recommended to use `AdvancedWorldManager` for all new development.

## Installation

1.  Download the latest release from the repository's releases page.
2.  Place the `mcc-*.jar` file into the `plugins` directory of your PaperMC server.
3.  Restart the server. The plugin will be enabled automatically.

## Commands

The plugin includes a utility command for world management:

*   `/awm convert <worldname> <slimename>`
    *   **Description**: Converts a world from the standard Anvil format to the Slime format used by AdvancedSlimePaper. The Anvil world should be located in the `anvilStorage/<worldname>` directory relative to the server root.
    *   **Permission**: `mcc.command.awm`

## API Usage

To use PowerTok-MCC as a dependency in your Maven project, add the following to your `pom.xml`:

1) Add the JitPack repository to your build file

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

2) Add the dependency

```xml
<dependency>
    <groupId>com.github.Sachanime</groupId>
    <artifactId>PowerTok-MCC</artifactId>
    <version>Tag</version>
</dependency>
```

### Examples

Here are some examples of how to use the manager classes provided by PowerTok-MCC.

#### AdvancedWorldManager

Create a new minigame world from a slime template for a player.

```java
import com.skl.powertok.mcc.managers.AdvancedWorldManager;
import org.bukkit.entity.Player;

// ...

Player player = /* ... */;
AdvancedWorldManager worldManager = new AdvancedWorldManager();
worldManager.createNewWorld(player, "skywars", 0.5, 100.0, 0.5);
```

#### BossbarManager

Display a custom boss bar to a player.

```java
import com.skl.powertok.mcc.managers.BossbarManager;
import org.bukkit.entity.Player;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.format.NamedTextColor;

// ...

Player player = /* ... */;
BossbarManager bossbarManager = new BossbarManager();
BossBar gameTimerBar = bossbarManager.displayBossBar(
    player,
    "Time Remaining: 5:00",
    NamedTextColor.GREEN,
    1.0f,
    BossBar.Color.GREEN,
    BossBar.Overlay.PROGRESS
);
```

#### ScoretabManager

Create and display a scoreboard to a player.

```java
import com.skl.powertok.mcc.managers.ScoretabManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Objective;
import net.kyori.adventure.text.Component;

// ...

Player player = /* ... */;
ScoretabManager scoretabManager = new ScoretabManager();
Scoreboard scoreboard = scoretabManager.newScoreboard();
Objective objective = scoretabManager.setScoreboardObjective(scoreboard, "game_info", Component.text("Game Info"));

scoretabManager.editScoreboardObjective(objective, "§aPlayers: §f8/16", 3);
scoretabManager.editScoreboardObjective(objective, "§eKills: §f0", 2);
scoretabManager.editScoreboardObjective(objective, " ", 1); // Spacer
scoretabManager.editScoreboardObjective(objective, "§7powertok.server", 0);

player.setScoreboard(scoreboard);
```

#### TitleManager

Show a title and subtitle to a player.

```java
import com.skl.powertok.mcc.managers.TitleManager;
import org.bukkit.entity.Player;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.text.format.NamedTextColor;

// ...

Player player = /* ... */;
TitleManager titleManager = new TitleManager();
Title victoryTitle = titleManager.createTitle(
    "VICTORY!",
    NamedTextColor.GOLD,
    "You are the last one standing!",
    NamedTextColor.YELLOW
);

player.showTitle(victoryTitle);
```

## Building from Source

To build the project from source, you will need Java 26 and Maven.

1.  Clone the repository:
    ```sh
    git clone https://github.com/sachanime/PowerTok-MCC.git
    cd PowerTok-MCC
    ```
2.  Build the project using Maven:
    ```sh
    mvn clean package
    ```
3.  The compiled JAR will be located in the `target` directory.
