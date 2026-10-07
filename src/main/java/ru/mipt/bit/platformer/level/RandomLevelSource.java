package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class RandomLevelSource implements LevelSource {

    private static final int MIN_TILES_FOR_PLAYER_MOVE_AND_TREE = 3;

    private final int width;
    private final int height;
    private final Random random;

    public RandomLevelSource(int width, int height, Random random) {
        if (width <= 0 || height <= 0
                || (long) width * height < MIN_TILES_FOR_PLAYER_MOVE_AND_TREE) {
            throw new IllegalArgumentException("Level needs room for a player, a move, and a tree");
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

        int treeCount = Math.max(1, tiles.size() / 5);
        GridPoint2 player = tiles.remove(0);
        // Leave room for the tank to move.
        for (int i = 0; i < tiles.size(); i++) {
            GridPoint2 tile = tiles.get(i);
            if (Math.abs(tile.x - player.x) + Math.abs(tile.y - player.y) == 1) {
                tiles.remove(i);
                break;
            }
        }

        List<Tree> trees = new ArrayList<>();
        for (int i = 0; i < treeCount; i++) {
            trees.add(new Tree(tiles.get(i)));
        }
        return new Level(width, height, trees, player);
    }
}
