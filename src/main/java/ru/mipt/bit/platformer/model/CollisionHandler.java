package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.List;

final class CollisionHandler implements TileAvailability {

    private final int width;
    private final int height;
    private final List<LevelObject> objects;

    CollisionHandler(int width, int height, List<LevelObject> objects) {
        this.width = width;
        this.height = height;
        this.objects = objects;
    }

    @Override
    public boolean isFree(GridPoint2 coordinates) {
        if (!isInside(coordinates)) {
            return false;
        }
        for (LevelObject object : objects) {
            if (object instanceof Obstacle && object.occupies(coordinates)) {
                return false;
            }
        }
        return true;
    }

    boolean isInside(GridPoint2 coordinates) {
        return coordinates.x >= 0 && coordinates.x < width
                && coordinates.y >= 0 && coordinates.y < height;
    }
}
