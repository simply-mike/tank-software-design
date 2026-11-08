# Tank game

```sh
./gradlew run                          # Default level
./gradlew run --args=random            # Random level
./gradlew run --args=path/to/level.txt  # Level from a file
```

Use arrow keys or WASD to move, Space to shoot, and L to toggle health bars.

Level files use `T` for trees, `_` for empty cells, and one `X` for the player's
starting position. All rows must have the same width.

Set the AI tank count, starting HP, damage, and movement times in
[game.properties](src/main/resources/game.properties).

Run `./gradlew test build` to test and build.
