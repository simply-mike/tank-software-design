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

public final class Field implements Disposable {

    private final TiledMap level;
    private final MapRenderer renderer;
    private final TileMovement tileMovement;
    private final Tree tree;

    public Field(String levelPath, Batch batch, Tree tree) {
        level = new TmxMapLoader().load(levelPath);
        renderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
        this.tree = tree;
        tree.placeOn(groundLayer);
    }

    public boolean isFree(GridPoint2 coordinates) {
        return !tree.occupies(coordinates);
    }

    void placeBetweenTileCenters(Rectangle rectangle, GridPoint2 from,
                                 GridPoint2 to, float progress) {
        tileMovement.moveRectangleBetweenTileCenters(rectangle, from, to, progress);
    }

    public void render() {
        renderer.render();
    }

    public void drawObstacles(Batch batch) {
        tree.draw(batch);
    }

    @Override
    public void dispose() {
        tree.dispose();
        level.dispose();
    }
}
