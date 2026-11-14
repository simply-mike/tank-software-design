package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

/** Places shapes at or between cell centers. Contract within the graphics layer */
public interface TilePlacement {

    void placeAtTileCenter(Rectangle rectangle, GridPoint2 coordinates);

    void placeBetweenTileCenters(Rectangle rectangle, GridPoint2 from,
                                 GridPoint2 to, float progress);
}
