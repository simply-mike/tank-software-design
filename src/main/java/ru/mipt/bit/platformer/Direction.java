package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

enum Direction {

    UP(0, 1, 90f),
    LEFT(-1, 0, -180f),
    DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f);

    private final GridPoint2 vector;
    private final float rotation;

    Direction(int deltaX, int deltaY, float rotation) {
        vector = new GridPoint2(deltaX, deltaY);
        this.rotation = rotation;
    }

    GridPoint2 calculateDestinationFrom(GridPoint2 origin) {
        return new GridPoint2(origin).add(vector);
    }

    float getRotationDegrees() {
        return rotation;
    }
}
