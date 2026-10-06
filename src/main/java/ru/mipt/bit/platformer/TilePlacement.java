package ru.mipt.bit.platformer;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

interface TilePlacement {

    void placeAtTileCenter(Rectangle rectangle, GridPoint2 coordinates);

    void placeBetweenTileCenters(Rectangle rectangle, GridPoint2 from,
                                 GridPoint2 to, float progress);
}
