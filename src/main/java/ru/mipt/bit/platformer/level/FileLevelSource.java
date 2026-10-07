package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FileLevelSource implements LevelSource {

    private final URL source;

    public FileLevelSource(URL source) {
        this.source = Objects.requireNonNull(source, "Level file not found");
    }

    @Override
    public Level load() throws IOException {
        List<String> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(source.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rows.add(line);
            }
        }
        if (rows.isEmpty() || rows.get(0).isEmpty()) {
            throw new IllegalArgumentException("Level must contain at least one tile");
        }

        int width = rows.get(0).length();
        int height = rows.size();
        List<Tree> trees = new ArrayList<>();
        GridPoint2 playerPosition = null;
        for (int row = 0; row < height; row++) {
            String line = rows.get(row);
            if (line.length() != width) {
                throw new IllegalArgumentException("Level rows must have equal width");
            }
            for (int x = 0; x < width; x++) {
                GridPoint2 position = new GridPoint2(x, height - 1 - row);
                switch (line.charAt(x)) {
                    case 'T':
                        trees.add(new Tree(position));
                        break;
                    case 'X':
                        if (playerPosition != null) {
                            throw new IllegalArgumentException("Level must have exactly one player");
                        }
                        playerPosition = position;
                        break;
                    case '_':
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown level tile at row " + (row + 1)
                                + ", column " + (x + 1));
                }
            }
        }
        if (playerPosition == null) {
            throw new IllegalArgumentException("Level must have exactly one player");
        }
        return new Level(width, height, trees, playerPosition);
    }
}
