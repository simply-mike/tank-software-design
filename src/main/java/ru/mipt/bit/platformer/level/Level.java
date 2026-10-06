package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.model.Field;
import ru.mipt.bit.platformer.model.Obstacle;
import ru.mipt.bit.platformer.model.Tree;

import java.util.ArrayList;
import java.util.List;

public final class Level {

    private final int width;
    private final int height;
    private final List<Tree> trees;
    private final GridPoint2 playerPosition;
    private final Field field;

    Level(int width, int height, List<Tree> trees, GridPoint2 playerPosition) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.trees = List.copyOf(trees);
        this.playerPosition = new GridPoint2(playerPosition);
        field = new Field(width, height, new ArrayList<Obstacle>(trees));
        if (!field.isFree(playerPosition)) {
            throw new IllegalArgumentException("Player must start on a free tile");
        }
        for (Tree tree : trees) {
            if (tree.getCoordinates().x < 0 || tree.getCoordinates().x >= width
                    || tree.getCoordinates().y < 0 || tree.getCoordinates().y >= height) {
                throw new IllegalArgumentException("Tree lies outside the level");
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Tree> getTrees() {
        return trees;
    }

    public GridPoint2 getPlayerPosition() {
        return new GridPoint2(playerPosition);
    }

    public Field getField() {
        return field;
    }
}
