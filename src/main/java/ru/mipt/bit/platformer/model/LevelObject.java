package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface LevelObject {

    boolean occupies(GridPoint2 coordinates);

    default void update(double deltaTime) {
    }

    default double timeToNextTransition() {
        return Double.POSITIVE_INFINITY;
    }

    default void onRemoved() {
    }

    default boolean isDestroyed() {
        return false;
    }
}
