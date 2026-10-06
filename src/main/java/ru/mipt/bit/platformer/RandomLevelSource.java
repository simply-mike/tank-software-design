package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

final class RandomLevelSource implements LevelSource {

    private final int width;
    private final int height;
    private final Random random;

    RandomLevelSource(int width, int height, Random random) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.random = random;
    }

    @Override
    public Level load() {
        List<GridPoint2> tiles = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles.add(new GridPoint2(x, y));
            }
        }
        Collections.shuffle(tiles, random);

        List<Tree> trees = new ArrayList<>();
        int treeCount = Math.min(tiles.size() - 1, Math.max(1, tiles.size() / 5));
        for (int i = 1; i <= treeCount; i++) {
            trees.add(new Tree(tiles.get(i)));
        }
        return new Level(width, height, trees, tiles.get(0));
    }
}
