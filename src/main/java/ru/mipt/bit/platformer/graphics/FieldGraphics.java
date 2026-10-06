package ru.mipt.bit.platformer.graphics;

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
import ru.mipt.bit.platformer.graphics.util.TileMovement;

import java.util.NoSuchElementException;

public final class FieldGraphics implements Disposable, TilePlacement {

    private final TiledMap level;
    private final MapRenderer renderer;
    private final TileMovement tileMovement;

    public FieldGraphics(String levelPath, Batch batch, int width, int height) {
        level = new TmxMapLoader().load(levelPath);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        if (groundLayer.getWidth() != width || groundLayer.getHeight() != height) {
            TiledMapTileLayer resized = new TiledMapTileLayer(
                    width, height, groundLayer.getTileWidth(), groundLayer.getTileHeight());
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    resized.setCell(x, y, groundLayer.getCell(
                            x % groundLayer.getWidth(), y % groundLayer.getHeight()));
                }
            }
            level.getLayers().remove(groundLayer);
            level.getLayers().add(resized);
            groundLayer = resized;
        }
        renderer = createRenderer(level, groundLayer, batch);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    @Override
    public void placeAtTileCenter(Rectangle rectangle, GridPoint2 coordinates) {
        tileMovement.moveRectangleAtTileCenter(rectangle, coordinates);
    }

    @Override
    public void placeBetweenTileCenters(Rectangle rectangle, GridPoint2 from,
                                        GridPoint2 to, float progress) {
        tileMovement.moveRectangleBetweenTileCenters(rectangle, from, to, progress);
    }

    public void render() {
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
