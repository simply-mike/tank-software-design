package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;

interface TileAvailability {

    boolean isFree(GridPoint2 coordinates);
}
