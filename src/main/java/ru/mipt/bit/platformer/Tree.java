package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

final class Tree implements Obstacle {

    private final GridPoint2 coordinates;

    Tree(GridPoint2 coordinates) {
        this.coordinates = new GridPoint2(coordinates);
    }

    @Override
    public boolean occupies(GridPoint2 tileCoordinates) {
        return coordinates.equals(tileCoordinates);
    }

    GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }
}
