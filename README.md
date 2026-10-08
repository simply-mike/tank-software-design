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
