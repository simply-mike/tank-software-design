package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.moveRectangleAtTileCenter;

final class FieldGraphics implements Disposable {

    private final TiledMap level;
    private final MapRenderer renderer;
    private final TiledMapTileLayer groundLayer;
    private final TileMovement tileMovement;

    FieldGraphics(String levelPath, Batch batch) {
        level = new TmxMapLoader().load(levelPath);
        renderer = createSingleLayerMapRenderer(level, batch);
        groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    void placeAtTileCenter(Rectangle rectangle, GridPoint2 coordinates) {
        moveRectangleAtTileCenter(groundLayer, rectangle, coordinates);
    }

    void placeBetweenTileCenters(Rectangle rectangle, GridPoint2 from,
                                 GridPoint2 to, float progress) {
        tileMovement.moveRectangleBetweenTileCenters(rectangle, from, to, progress);
    }

    void render() {
        renderer.render();
    }

    @Override
    public void dispose() {
        level.dispose();
    }
}
