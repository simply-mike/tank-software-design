package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

final class Field {

    private final int width;
    private final int height;
    private final List<Obstacle> obstacles;

    Field(int width, int height, List<Obstacle> obstacles) {
        this.width = width;
        this.height = height;
        this.obstacles = new ArrayList<>(obstacles);
    }

    boolean isFree(GridPoint2 coordinates) {
        if (coordinates.x < 0 || coordinates.x >= width
                || coordinates.y < 0 || coordinates.y >= height) {
            return false;
        }

        for (Obstacle obstacle : obstacles) {
            if (obstacle.occupies(coordinates)) {
                return false;
            }
        }
        return true;
    }

}
