# CS5004 Capstone HW8 Documentation

This repository implements a console-based adventure game engine using an MVC-style structure. For HW8, the documentation set now includes refined static UML, dynamic UML, refined textual scenarios, and a design summary explaining how the current implementation evolved.

## Documentation Set

- UML diagrams: [docs/UML.md](/Users/yenyu/Desktop/CS education/5004/CS5004_capstone/docs/UML.md)
- Refined scenarios: [docs/SCENARIOS.md](/Users/yenyu/Desktop/CS education/5004/CS5004_capstone/docs/SCENARIOS.md)

## Current Design

The system is organized into four main areas:

1. Application bootstrap
   `GameEngineApp` loads the JSON game definition, creates the model and controller, and starts the game loop.

2. Controller layer
   `GameController` is responsible for console input/output and command parsing. It depends only on `IGameModel`, which keeps the controller decoupled from concrete model classes.

3. Model and domain layer
   `GameModel` owns the active game state and enforces the rules for movement, monster attacks, puzzle solving, item usage, scoring, and end-game checks. The domain objects include `GameWorld`, `Room`, `Player`, `Inventory`, `Item`, `Fixture`, `Puzzle`, and `Monster`.

4. Persistence and loading
   `JsonGameLoader` constructs the initial `GameWorld` from JSON. `GameSaveManager` captures and restores mutable runtime state such as player stats, inventory, room exits, and active puzzle or monster status. In the current implementation, persistence uses the fixed file `./savegame.json`.

## Design Rationale

- `IGameModel` provides a narrow contract between controller and model. This keeps the controller simple and makes the design cleaner for testing and future UI changes.
- `GameWorld` acts as the registry of loaded game content, while `GameModel` manages session-specific state and rules.
- `Room` aggregates the content the player directly experiences: exits, items, fixtures, an optional puzzle, and an optional monster.
- `Inventory` isolates weight-limit logic and item lookup from the player object.
- Save and restore are separated into `GameSaveManager` so persistence concerns do not spread across the controller or core game-rule logic.

## Evolution From HW7

The repository does not include a separate HW7 snapshot or branch, so this section is based on the current code structure and the available Git history.

From the commit history, the design appears to have evolved in these steps:

1. Core domain objects were introduced first.
   Early commits added `Player`, `Inventory`, `HealthStatus`, `Room`, `Item`, and `Fixture`. This established the basic game state and object model.

2. The model contract and rule engine were added next.
   Later commits introduced `GameModel` and `IGameModel`. This is the major architectural refinement because it separates command handling from rule enforcement and makes the controller depend on an interface rather than concrete classes.

3. Persistence support was added after the core gameplay model.
   `GameSaveManager` and item restore support were added to preserve mutable session state. This extends the design beyond simple in-memory gameplay and is an important HW8-level refinement.

4. Controller and app wiring were finalized afterward.
   Subsequent commits introduced and refined `GameController` and `GameEngineApp`, giving the system a clearer MVC-style flow with explicit startup, I/O control, and model interaction.

5. The current codebase reflects a more refined decomposition than a typical earlier milestone.
   In particular, responsibilities are now separated across bootstrap, controller, model, domain objects, loading, and persistence rather than being concentrated in a smaller number of classes.

## How the UML Matches the Code

- The static domain diagram shows the long-lived structural relationships among the game classes.
- The architecture diagram isolates the controller, bootstrap, loading, and save/restore responsibilities so the design is easier to read than a single dense diagram.
- The sequence diagrams show two required dynamic views: normal gameplay and save/restore behavior.

## Notes for Submission

- If your instructor wants image exports instead of Mermaid source, the diagrams in [docs/UML.md](/Users/yenyu/Desktop/CS education/5004/CS5004_capstone/docs/UML.md) can be rendered and exported as PNG or PDF.
- If you want the documentation to describe a specific HW7 baseline more precisely, add that baseline branch, commit, or prior README and update the evolution section to reference it directly.
