# tank-software-design

Run one of these commands:

```sh
./gradlew run                          # Load the default level
./gradlew run --args=random            # Make a random level
./gradlew run --args=path/to/level.txt # Load a level from a file
```

In a level file, `T` is a tree, `X` is the tank start position, and `_` is an
empty cell. Each line is one row. All rows must have the same length. Use one
`X`.

Move with the arrow keys or WASD. Press Space to shoot once; release it before
the next shot. AI tanks randomly choose between four movement directions and
shooting.

Set the AI tank count and movement duration in `src/main/resources/game.properties`.

Run tests with `./gradlew test`.

Default tanks have 100 HP. A standard bullet deals 30 HP of damage and travels
one cell in 0.1 seconds. Four standard hits destroy a full-health tank.
A tank at 0 HP stays alive and is removed when HP becomes negative.
Bullets disappear on hitting trees, tanks, other bullets, or the field boundary.
Both bullets disappear when they collide. A bullet cannot damage its shooter;
other tanks can be hit in either cell they occupy during movement.

The `setup` package loads or generates a level and places its initial tanks.
The logical `model.Level` owns the objects and their lifecycle. `CollisionHandler`
checks movement and resolves bullet contacts; tanks own health, and bullets own
flight. General registration is internal to the model; clients create tanks
through `Level.spawnTank`. Removing a tank or bullet ends its activity, so queued
commands cannot act on a removed tank. `LevelGraphics` subscribes to object additions and removals, receiving
initial objects through the same notifications. `graphics.ObjectViewFactory`
selects views and owns the shared tank, tree, and bullet renderers. Their textures
are disposed when the game closes; the game loop does not dispatch on entity types.

Each frame collects commands, executes them, updates the logical level, then
renders. Random AI decisions use the same move/shoot commands as keyboard input.
The level advances all objects to each next cell transition before resolving
contacts. Bullet contacts include opposite projectiles swapping cells in the
same step; a long frame does not let one projectile catch a frozen peer.

Verify the complete project with `./gradlew test build` and `./gradlew run`.
