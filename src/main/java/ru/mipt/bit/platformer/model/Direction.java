package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/** Domain direction with a cell offset and a rotation angle */
public enum Direction {

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

    public GridPoint2 calculateDestinationFrom(GridPoint2 origin) {
        return new GridPoint2(origin).add(vector);
    }

    public float getRotationDegrees() {
        return rotation;
    }
}
