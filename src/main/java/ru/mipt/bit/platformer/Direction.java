package ru.mipt.bit.platformer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;

enum Direction {

    UP(0, 1, 90f, Input.Keys.UP, Input.Keys.W),
    LEFT(-1, 0, -180f, Input.Keys.LEFT, Input.Keys.A),
    DOWN(0, -1, -90f, Input.Keys.DOWN, Input.Keys.S),
    RIGHT(1, 0, 0f, Input.Keys.RIGHT, Input.Keys.D);

    private final int deltaX;
    private final int deltaY;
    private final float rotation;
    private final int arrowKey;
    private final int letterKey;

    Direction(int deltaX, int deltaY, float rotation, int arrowKey, int letterKey) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.rotation = rotation;
        this.arrowKey = arrowKey;
        this.letterKey = letterKey;
    }

    GridPoint2 destinationFrom(GridPoint2 origin) {
        return new GridPoint2(origin).add(deltaX, deltaY);
    }

    float rotation() {
        return rotation;
    }

    boolean isPressed(Input input) {
        return input.isKeyPressed(arrowKey) || input.isKeyPressed(letterKey);
    }
}
