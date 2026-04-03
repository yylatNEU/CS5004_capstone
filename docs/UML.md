# HW8 UML

This document provides both refined static UML and dynamic UML for the current app.

## 1. Static UML: Domain and Model

```mermaid
classDiagram
direction LR

class IGameModel {
  <<interface>>
  +setPlayerName(String) void
  +move(String) String
  +look() String
  +takeItem(String) String
  +dropItem(String) String
  +useItem(String) String
  +examine(String) String
  +getInventoryString() String
  +answerPuzzle(String) String
  +isGameOver() boolean
  +getEndGameSummary() String
  +save() String
  +restore() String
}

class GameModel {
  -GameWorld world
  -Player player
  -GameSaveManager saveManager
  +setPlayerName(String) void
  +move(String) String
  +look() String
  +takeItem(String) String
  +dropItem(String) String
  +useItem(String) String
  +examine(String) String
  +getInventoryString() String
  +answerPuzzle(String) String
  +isGameOver() boolean
  +getEndGameSummary() String
  +save() String
  +restore() String
}

class GameWorld {
  -String gameName
  -String version
  -Map~Integer, Room~ rooms
  -Map~String, Item~ items
  -Map~String, Fixture~ fixtures
  -Map~String, Puzzle~ puzzles
  -Map~String, Monster~ monsters
  +addRoom(Room) void
  +addItem(Item) void
  +addFixture(Fixture) void
  +addPuzzle(Puzzle) void
  +addMonster(Monster) void
  +getRoom(int) Room
  +getItem(String) Item
  +getFixture(String) Fixture
  +getPuzzle(String) Puzzle
  +getMonster(String) Monster
}

class Room {
  -int roomNumber
  -String roomName
  -String description
  -Map~Direction, Integer~ exits
  -String picture
  -List~Item~ items
  -List~Fixture~ fixture
  -Puzzle puzzle
  -Monster monster
  +getExit(Direction) int
  +setExit(Direction, int) void
  +addItem(Item) void
  +removeItem(Item) void
  +addFixture(Fixture) void
  +setPuzzle(Puzzle) void
  +setMonster(Monster) void
}

class Player {
  -String playerName
  -int health
  -int totalScore
  -int roomNumber
  -Inventory bag
  +takeDamage(int) void
  +addScore(int) void
  +restoreHealth() void
  +getHealthStatus() HealthStatus
  +getInventory() Inventory
}

class Inventory {
  -List~Item~ storage
  -double totalWeight
  +canAdd(Item) boolean
  +addItem(Item) boolean
  +removeItem(String) boolean
  +getItem(String) Item
  +listItems() String
}

class Item {
  -String name
  -int weight
  -int maxUses
  -int usesRemaining
  -int value
  -String whenUsed
  -String description
  -String picture
  +isUsable() boolean
  +use() String
  +setUsesRemaining(int) void
}

class Fixture {
  -String name
  -int weight
  -String description
  -String puzzle
  -String states
  -String picture
  +isMovable() boolean
}

class Puzzle {
  -String name
  -boolean active
  -boolean affectsTarget
  -boolean affectsPlayer
  -String solution
  -int value
  -String description
  -String effects
  -String target
  +isActive() boolean
  +isAnswerSolution() boolean
  +solve(String) boolean
  +solveWithItem(String) boolean
  +deactivate() void
}

class Monster {
  -String name
  -boolean active
  -boolean affectsTarget
  -boolean affectsPlayer
  -String solution
  -int value
  -String description
  -String effects
  -int damage
  -String target
  -boolean canAttack
  -String attack
  +isActive() boolean
  +canAttack() boolean
  +deactivate() void
}

class Direction {
  <<enumeration>>
  NORTH
  SOUTH
  EAST
  WEST
}

class HealthStatus {
  <<enumeration>>
  AWAKE
  FATIGUED
  WOOZY
  SLEEP
}

GameModel ..|> IGameModel
GameModel --> "1" GameWorld : maintains
GameModel *-- "0..1" Player : current player
GameModel *-- "1" GameSaveManager : save support
GameModel ..> Direction : movement parsing
Player *-- "1" Inventory
Player --> HealthStatus : derives
Inventory o-- "0..*" Item : stores
GameWorld *-- "1..*" Room : contains
GameWorld o-- "0..*" Item : registers
GameWorld o-- "0..*" Fixture : registers
GameWorld o-- "0..*" Puzzle : registers
GameWorld o-- "0..*" Monster : registers
Room *-- "0..*" Item : contains
Room *-- "0..*" Fixture : contains
Room --> "0..1" Puzzle : active puzzle
Room --> "0..1" Monster : active monster
Room --> "4" Direction : exit map keys
```

## 2. Static UML: App, Controller, Loading, Persistence

```mermaid
classDiagram
direction LR

class GameEngineApp {
  -String gameFileName
  -Readable source
  -Appendable output
  +start() void
  +main(String[] args) void
}

class GameController {
  -IGameModel model
  -Readable source
  -Appendable output
  -boolean hasWon
  +play() void
}

class IGameModel {
  <<interface>>
}

class GameModel

class JsonGameLoader {
  +load(String) GameWorld
}

class GameSaveManager {
  +save(Player, GameWorld) void
  +restore(GameWorld) Player
}

class GameWorld
class Player

GameController --> "1" IGameModel : depends on
GameModel ..|> IGameModel
GameEngineApp ..> JsonGameLoader : uses to load world
GameEngineApp ..> GameController : creates
GameEngineApp ..> GameModel : creates
JsonGameLoader ..> GameWorld : builds
GameSaveManager ..> GameWorld : saves/restores
GameSaveManager ..> Player : saves/restores
GameModel *-- "1" GameSaveManager : owns
```

## 3. Dynamic UML: Start Game and Normal Command Flow

```mermaid
sequenceDiagram
actor User
participant App as GameEngineApp
participant Loader as JsonGameLoader
participant World as GameWorld
participant Controller as GameController
participant Model as GameModel

User->>App: start()
App->>Loader: load(gameFileName)
Loader-->>App: GameWorld
App->>Model: new GameModel(world)
App->>Controller: new GameController(model, source, output)
App->>Controller: play()

Controller->>User: prompt for player name
User-->>Controller: name
Controller->>Model: setPlayerName(name)
Controller->>Model: look()
Model->>World: getRoom(player.currentRoom)
World-->>Model: Room
Model-->>Controller: room description
Controller->>User: display description

loop Each command
  User-->>Controller: command
  alt Move command
    Controller->>Model: move(direction)
    Model->>World: getRoom(currentRoom)
    World-->>Model: Room
    alt Exit blocked
      Model-->>Controller: blocked message (+ monster attack if active)
    else Exit open
      Model->>World: getRoom(newRoom)
      World-->>Model: Room
      Model-->>Controller: new room description (+ monster attack if active)
    end
  else Inventory or interaction command
    Controller->>Model: look / takeItem / dropItem / useItem / examine / answerPuzzle
    Model-->>Controller: result string
  end
  Controller->>User: display result
end
```

## 4. Dynamic UML: Save and Restore Flow

```mermaid
sequenceDiagram
actor User
participant Controller as GameController
participant Model as GameModel
participant SaveMgr as GameSaveManager
participant World as GameWorld
participant Player

User-->>Controller: save <file>
Controller->>Model: save()
Model->>SaveMgr: save(player, world)
SaveMgr->>Player: read stats and inventory
SaveMgr->>World: read rooms, items, puzzles, monsters
Note over SaveMgr: Current implementation writes to ./savegame.json
SaveMgr-->>Model: success/failure
Model-->>Controller: status string
Controller->>User: "Game saved ..."

User-->>Controller: restore <file>
Controller->>Model: restore()
Model->>SaveMgr: restore(world)
SaveMgr->>World: restore mutable room/object state
SaveMgr-->>Model: restored Player
Model->>Model: replace current player
Model->>Model: look()
Model-->>Controller: restore message + room description
Controller->>User: "Game restored ..."
```

## Notes

- The first static diagram focuses on the game domain and model responsibilities.
- The second static diagram focuses on app wiring, controller interaction, loading, and persistence.
- The dynamic diagrams cover the main gameplay path and the save/restore path expected for HW8.
- The save and restore flow currently persists to a fixed file path, `./savegame.json`, even though the controller reads a file-name argument from the user.
