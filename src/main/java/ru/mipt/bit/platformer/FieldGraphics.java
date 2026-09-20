package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapRenderer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import ru.mipt.bit.platformer.util.TileMovement;

import java.util.NoSuchElementException;

final class FieldGraphics implements Disposable {

    private final TiledMap level;
    private final MapRenderer renderer;
    private final TileMovement tileMovement;

    FieldGraphics(String levelPath, Batch batch) {
        level = new TmxMapLoader().load(levelPath);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        renderer = createRenderer(level, groundLayer, batch);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    void placeAtTileCenter(Rectangle rectangle, GridPoint2 coordinates) {
        tileMovement.moveRectangleAtTileCenter(rectangle, coordinates);
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

    private static TiledMapTileLayer getSingleLayer(TiledMap map) {
        MapLayers layers = map.getLayers();
        if (layers.size() == 0) {
            throw new NoSuchElementException("Map has no layers");
        }
        if (layers.size() > 1) {
            throw new IllegalArgumentException("Map has more than one layer");
        }
        return (TiledMapTileLayer) layers.iterator().next();
    }

    private static MapRenderer createRenderer(TiledMap map, TiledMapTileLayer layer,
                                              Batch batch) {
        OrthogonalTiledMapRenderer renderer = new OrthogonalTiledMapRenderer(map, batch);
        renderer.getViewBounds().set(
                0f,
                0f,
                layer.getWidth() * layer.getTileWidth(),
                layer.getHeight() * layer.getTileHeight()
        );
        return renderer;
    }
}
