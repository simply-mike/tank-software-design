package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

public interface LevelObject {

    boolean occupies(GridPoint2 coordinates);

    default void update(float deltaTime) {
    }

    default boolean isDestroyed() {
        return false;
    }
}
