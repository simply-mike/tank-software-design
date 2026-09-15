package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

interface Obstacle {

    boolean occupies(GridPoint2 coordinates);
}
