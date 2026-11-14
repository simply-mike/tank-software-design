package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

/** Domain query for checking whether a cell is free */
public interface TileAvailability {

    boolean isFree(GridPoint2 coordinates);
}
