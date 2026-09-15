package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;

interface Obstacle extends Disposable {

    boolean occupies(GridPoint2 coordinates);

    void placeOn(TiledMapTileLayer layer);

    void draw(Batch batch);
}
