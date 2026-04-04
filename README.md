# CS5004 HW8 — Data-Driven Adventure Game Engine

**Team:** NO BUG  
**Members:** Yu Chen, MinHsun Hsieh, Ying-Lou Lu, Yenyu Liu, Shelly (Beiyi) Xu

---

## How to Run

1. Open the project in IntelliJ IDEA.
2. Ensure `lib/json-20251224.jar` is on the classpath (Project Structure → Libraries).
3. Run `GameEngineApp.main()` in `src/enginedriver/`.
4. The default game file is `./resources/align_quest_game_elements.json`.

---

## Design Overview

The system follows the **Model-View-Controller (MVC)** pattern with three layers:

- **Engine Driver** (`GameEngineApp`) — application entry point; loads the JSON game file, wires the model and controller, and starts the game loop.
- **Controller** (`GameController`) — reads user input from a `Readable`, dispatches commands to the model via the `IGameModel` interface, and writes output to an `Appendable`. The controller has no knowledge of concrete model classes.
- **Model** (`GameModel` + domain classes) — owns all game state and enforces gameplay rules. Returns `String` messages to the controller; performs no I/O.

---

## Class / Interface Descriptions

### enginedriver

| Class | Purpose |
|-------|---------|
| `GameEngineApp` | Entry point. Loads JSON, creates model and controller, starts the game loop. |

### controller

| Class | Purpose |
|-------|---------|
| `GameController` | Console game loop. Parses commands (move, look, take, drop, use, examine, answer, save, restore, quit) and delegates to `IGameModel`. |

### model

| Class / Interface | Purpose |
|-------------------|---------|
| `IGameModel` | Interface defining the controller-facing contract. Decouples the controller from all concrete model classes. |
| `GameModel` | Implements `IGameModel`. Orchestrates movement, combat, puzzle solving, inventory management, scoring, and save/restore. |
| `GameWorld` | Centralized registry of all `Room`, `Item`, `Fixture`, `Puzzle`, and `Monster` objects loaded from JSON. |
| `JsonGameLoader` | Parses a JSON game file and constructs the `GameWorld`. Enables data-driven game creation. |
| `GameSaveManager` | Serializes/deserializes mutable game state (player stats, inventory, puzzle/monster status, room exits) to `savegame.json`. |
| `Player` | Tracks player name, health (0–100), score, current room number, and inventory. |
| `Inventory` | Manages collected items with a weight limit of 13. Supports add, remove, and lookup. |
| `Room` | Represents a location. Contains directional exits, items, fixtures, an optional puzzle, and an optional monster. |
| `Item` | A collectible object with limited uses, weight, value, and descriptive text. |
| `Fixture` | A heavy, immovable object (e.g. desk, bookshelf) that can be examined but not picked up. |
| `Puzzle` | Gates blocked exits. Solved via a text answer or by using a specific item. Awards score on completion. |
| `Monster` | Blocks exits and attacks on movement. Defeated by using the correct item. Awards score on defeat. |
| `Direction` | Enum (`NORTH`, `SOUTH`, `EAST`, `WEST`) with string parsing. |
| `HealthStatus` | Enum (`AWAKE`, `FATIGUED`, `WOOZY`, `SLEEP`) derived from current health. |

---

## Design Evolution from HW7

HW7 was a pure analysis phase — UML diagrams and written scenarios describing a proposed design. HW8 is the working implementation. The key changes are:

1. **Architecture: single GameEngine → MVC.** HW7 proposed a monolithic `GameEngine` class. HW8 separates responsibilities into `GameEngineApp` (bootstrap), `GameController` (I/O), and `GameModel` (logic), connected through the `IGameModel` interface.

2. **Combat: Battle class → inline monster attacks.** HW7 designed a standalone `Battle` class with turn-based combat. In implementation, monsters attack the player automatically on move attempts, and the player defeats monsters by using the correct item. A separate Battle class was unnecessary for this interaction model.

3. **Puzzles: password collection → text answers and item usage.** HW7 envisioned collecting clue fragments to assemble a password. HW8 puzzles are solved directly by typing a text answer or by using a specific item, driven by the JSON data.

4. **Economy: Store / ItemToPurchase removed.** HW7 included an in-game store. This was not required by the game data specification, so it was removed to keep the design focused.

5. **Map / Tile system → room-number connections.** HW7 proposed a `Map` class with a tile-based grid. HW8 connects rooms via directional exit values (positive = passable, 0 = wall, negative = blocked), which matches the JSON data format directly.

6. **Player attributes simplified.** HW7 included EXP, level, strength, and coin. HW8 uses health, score, and inventory — sufficient for the game scenarios provided.

7. **New components added.** `JsonGameLoader` (data-driven loading), `GameSaveManager` (persistence), `HealthStatus` (health categorization), and `Direction` (enum with parsing) were introduced during implementation to support required functionality.

---

## Project Structure

```
src/
  enginedriver/    GameEngineApp.java
  controller/      GameController.java
  model/           IGameModel, GameModel, GameWorld, JsonGameLoader,
                   GameSaveManager, Player, Inventory, Room, Item,
                   Fixture, Puzzle, Monster, Direction, HealthStatus
test/
  model/           Unit tests for all model classes + TestWorldFactory
Resources/         JSON game data files
lib/               json-20251224.jar
```

---

## External Dependencies

- `json-20251224.jar` — JSON parsing (included in `lib/`).
- JUnit 5 — unit testing (test scope only).
