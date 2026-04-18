# CS5004 HW9 — Adventure Game Engine with Swing GUI

**Team:** NO BUG
**Members:** Yu Chen, MinHsun Hsieh, Ying-Lou Lu, Yenyu Liu, Shelly (Beiyi) Xu

---

## UML Models & Written Scenarios

See `HW9_UML models.pdf` for the updated UML class diagram, object diagrams, sequence diagrams, and written scenarios for the HW9 (MVC + Graphics) design.

---

## How to Run

1. Open the project in IntelliJ IDEA.
2. Ensure `lib/json-20251224.jar` is on the classpath (Project Structure → Libraries).
3. Run `enginedriver.GameEngineApp` with command-line arguments:

```
java -jar game_engine.jar <gameFile> -text
java -jar game_engine.jar <gameFile> -graphics
java -jar game_engine.jar <gameFile> -batch <sourceFile> [targetFile]
```

- `-text` — console mode (read from `System.in`, write to `System.out`).
- `-graphics` — Swing GUI mode (window is launched on the Swing Event Dispatch Thread).
- `-batch <src> [tgt]` — reads commands from `src`; if `tgt` is provided, output is written to it, otherwise output goes to `System.out`.

Default game files live in `resources/` (`alignquest.json`, `museum.json`, `simple_hallway.json`, `empty_rooms.json`).

---

## Design Overview

The system follows the **Model-View-Controller (MVC)** pattern with four layers:

- **Engine Driver** (`GameEngineApp`) — application entry point; parses the mode flag, loads the JSON game file, wires the model, view (if any), and controller, and hands off control.
- **Controllers** — two interchangeable controllers behind the same `IGameModel` contract:
  - `GameController` drives the **text / batch** loop (reads from a `Readable`, writes to an `Appendable`).
  - `GraphicsController` drives the **Swing GUI**; it implements `ViewListener` to receive events from the View and calls `IGameView` methods to refresh the display after each model change.
- **View** (`IGameView` + `MainFrame` + panels) — purely presentational Swing layer. Holds no game state; every gesture is forwarded through `ViewListener` and every update arrives through `IGameView`.
- **Model** (`GameModel` + domain classes) — owns all game state and enforces gameplay rules. Returns `String` messages and exposes read-only query methods for the graphics layer; performs no I/O.

---

## Class / Interface Descriptions

### enginedriver

| Class | Purpose |
|-------|---------|
| `GameEngineApp` | Entry point. Dispatches to `-text`, `-graphics`, or `-batch` mode; loads JSON, wires model / view / controller, and starts the appropriate loop. |

### controller

| Class | Purpose |
|-------|---------|
| `GameController` | Console / batch game loop. Parses commands (move, look, take, drop, use, examine, answer, save, restore, quit) and delegates to `IGameModel`. |

### view

| Class / Interface | Purpose |
|-------------------|---------|
| `IGameView` | Interface describing every update and prompt the Controller needs from the View (update room image / description / nav buttons / health / inventory, show message / inspect / about / game-over, prompt input / selection). |
| `ViewListener` | Callback interface implemented by the Controller; the View calls these methods when the user clicks buttons or selects menu items. |
| `GraphicsController` | Implements `ViewListener`. Mediates between user events and `IGameModel`, refreshing the View and checking for game-over after each action. |
| `MainFrame` | Swing `JFrame` implementing `IGameView`. Hosts a 2×2 layout of panels and a File menu (About / Save / Restore / Exit). |
| `ViewPanel` | Top-left panel — renders the current room / monster / puzzle image. |
| `NavigationPanel` | Top-right panel — directional buttons (N/S/E/W) and action buttons (Take, Examine, Answer). |
| `DescriptionPanel` | Bottom-left panel — scrollable narrative text for the current room. |
| `InventoryPanel` | Bottom-right panel — inventory list, health status, and inventory actions (Inspect, Use, Drop). |
| `ModalDialogs` | Shared helpers for themed message / confirm / input / content dialogs. |
| `ImageUtils` | Loads and scales images from `resources/images/` (with safe fallbacks). |
| `RoundedButton` | Custom rounded Swing button used across the panels. |
| `TeamUiTheme` | Centralized colors, fonts, borders, and menu-highlight theming. |
| `GradientOutlineBorder` | Reusable gradient outline border used by panels and dialogs. |

### model

| Class / Interface | Purpose |
|-------------------|---------|
| `IGameModel` | Controller-facing contract. Exposes action methods (`move`, `look`, `takeItem`, `dropItem`, `useItem`, `examine`, `answerPuzzle`, `save`, `restore`) and view-facing queries (`getInventoryItemNames`, `getRoomItemNames`, `getExaminableNames`, `getItemImage`, `getCurrentRoomImage`, `getGameName`, `setPlayerName`). |
| `GameModel` | Implements `IGameModel`. Orchestrates movement, combat, puzzle solving, inventory management, scoring, and save/restore. |
| `GameWorld` | Centralized registry of all `Room`, `Item`, `Fixture`, `Puzzle`, and `Monster` objects loaded from JSON. |
| `JsonGameLoader` | Parses a JSON game file and constructs the `GameWorld`. Enables data-driven game creation. |
| `GameSaveManager` | Serializes/deserializes mutable game state (player stats, inventory, item usage, puzzle/monster status, room exits) to `savegame.json`. |
| `Player` | Tracks player name, health (0–100), score, current room number, and inventory. |
| `Inventory` | Manages collected items with a weight limit of 13. |
| `Room` | A location with directional exits, items, fixtures, an optional puzzle, and an optional monster. |
| `Item` | A collectible object with limited uses, weight, value, and descriptive text. |
| `Fixture` | An immovable object (e.g. desk, bookshelf) that can be examined but not picked up. |
| `Puzzle` | Gates blocked exits. Solved via a text answer or by using a specific item. Awards score on completion. |
| `Monster` | Blocks exits and attacks on movement. Defeated by using the correct item. Awards score on defeat. |
| `Direction` | Enum (`NORTH`, `SOUTH`, `EAST`, `WEST`) with string parsing. |
| `HealthStatus` | Enum (`AWAKE`, `FATIGUED`, `WOOZY`, `SLEEP`) derived from current health. |

---

## Design Evolution from HW8

HW8 delivered a text-only MVC engine. HW9 keeps the Model essentially intact and layers a full graphical front-end on top without disturbing the existing controller or domain classes.

1. **New View layer.** Added the `IGameView` interface and a Swing implementation (`MainFrame`) assembled from four quadrant panels (`ViewPanel`, `NavigationPanel`, `DescriptionPanel`, `InventoryPanel`) plus supporting UI utilities (`ModalDialogs`, `ImageUtils`, `RoundedButton`, `TeamUiTheme`, `GradientOutlineBorder`). The View owns no game state.

2. **New Graphics Controller.** `GraphicsController` implements a new `ViewListener` callback interface. User gestures flow View → Controller via `ViewListener`; display updates flow Controller → View via `IGameView`. The text `GameController` is unchanged, proving that `IGameModel` is UI-agnostic.

3. **Model extended with view-facing queries.** `IGameModel` gained read-only accessors (`getInventoryItemNames`, `getRoomItemNames`, `getExaminableNames`, `getItemImage`, `getCurrentRoomImage`, `getGameName`, `setPlayerName`) so the GUI can render lists and images without the View or Controller touching concrete model classes. The Model still performs no I/O.

4. **Per-entity images.** `Item`, `Fixture`, `Puzzle`, `Monster`, and `Room` may carry an optional picture filename. The Model resolves the current room's image in priority order: active monster → active puzzle → room default.

5. **Three run modes.** `GameEngineApp` now parses a mode flag: `-text` (console), `-graphics` (Swing GUI on the EDT via `SwingUtilities.invokeLater`), and `-batch <source> [target]` (scripted input from a file, optional output to another file).

6. **GUI-oriented interactions.** The GUI offers dialog-based selection for Take / Drop / Use / Inspect / Examine / Answer, a File menu with Save / Restore / About / Exit, a dedicated Game-Over dialog shown centrally from the controller, and dimmed direction buttons for non-passable exits (blocked exits still surface the model's message on click).

7. **Tests extended.** Added `GraphicsControllerTest` (with an `IGameView` stub) under `test/view/` to verify controller↔view interactions without launching a real window.

---

## Project Structure

```
src/
  enginedriver/    GameEngineApp.java
  controller/      GameController.java
  view/            IGameView, ViewListener, GraphicsController, MainFrame,
                   ViewPanel, NavigationPanel, DescriptionPanel, InventoryPanel,
                   ModalDialogs, ImageUtils, RoundedButton, TeamUiTheme,
                   GradientOutlineBorder
  model/           IGameModel, GameModel, GameWorld, JsonGameLoader,
                   GameSaveManager, Player, Inventory, Room, Item,
                   Fixture, Puzzle, Monster, Direction, HealthStatus
test/
  model/           Unit tests for all model classes + TestWorldFactory
  view/            GraphicsControllerTest (with IGameView stub)
resources/         JSON game data + images/ (room / item / monster / puzzle art)
lib/               json-20251224.jar
```

---

## External Dependencies

- `json-20251224.jar` — JSON parsing (included in `lib/`).
- JUnit 5 — unit testing (test scope only).
- Java Swing / AWT — GUI (JDK standard library).
