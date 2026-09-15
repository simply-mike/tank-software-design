package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.List;

final class Field {

    private final List<Obstacle> obstacles;

    Field(List<Obstacle> obstacles) {
        this.obstacles = new ArrayList<>(obstacles);
    }

    boolean isFree(GridPoint2 coordinates) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.occupies(coordinates)) {
                return false;
            }
        }
        return true;
    }

}
