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

import java.util.ArrayList;
import java.util.List;

import static ru.mipt.bit.platformer.util.GdxGameUtils.createSingleLayerMapRenderer;
import static ru.mipt.bit.platformer.util.GdxGameUtils.getSingleLayer;

final class Field implements Disposable {

    private final TiledMap level;
    private final MapRenderer renderer;
    private final TileMovement tileMovement;
    private final List<Obstacle> obstacles;

    Field(String levelPath, Batch batch, List<Obstacle> obstacles) {
        level = new TmxMapLoader().load(levelPath);
        renderer = createSingleLayerMapRenderer(level, batch);
        TiledMapTileLayer groundLayer = getSingleLayer(level);
        tileMovement = new TileMovement(groundLayer, Interpolation.smooth);
        this.obstacles = new ArrayList<>(obstacles);
        for (Obstacle obstacle : this.obstacles) {
            obstacle.placeOn(groundLayer);
        }
    }

    boolean isFree(GridPoint2 coordinates) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.occupies(coordinates)) {
                return false;
            }
        }
        return true;
    }

    void placeBetweenTileCenters(Rectangle rectangle, GridPoint2 from,
                                 GridPoint2 to, float progress) {
        tileMovement.moveRectangleBetweenTileCenters(rectangle, from, to, progress);
    }

    void render() {
        renderer.render();
    }

    void drawObstacles(Batch batch) {
        for (Obstacle obstacle : obstacles) {
            obstacle.draw(batch);
        }
    }

    @Override
    public void dispose() {
        for (Obstacle obstacle : obstacles) {
            obstacle.dispose();
        }
        level.dispose();
    }
}
