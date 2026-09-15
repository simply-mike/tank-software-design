package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

enum Direction {

    UP(0, 1, 90f),
    LEFT(-1, 0, -180f),
    DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f);

    private final int deltaX;
    private final int deltaY;
    private final float rotation;

    Direction(int deltaX, int deltaY, float rotation) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.rotation = rotation;
    }

    GridPoint2 calculateDestinationFrom(GridPoint2 origin) {
        return new GridPoint2(origin).add(deltaX, deltaY);
    }

    float getRotationDegrees() {
        return rotation;
    }
}
