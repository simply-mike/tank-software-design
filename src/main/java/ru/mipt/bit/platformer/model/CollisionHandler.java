package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.List;
import java.util.stream.Collectors;

final class CollisionHandler implements TileAvailability {

    private final int width;
    private final int height;
    private final List<LevelObject> objects;

    CollisionHandler(int width, int height, List<LevelObject> objects) {
        this.width = width;
        this.height = height;
        this.objects = objects;
    }

    @Override
    public boolean isFree(GridPoint2 coordinates) {
        if (!isInside(coordinates)) {
            return false;
        }
        for (LevelObject object : objects) {
            if (!object.isDestroyed() && object instanceof Obstacle && object.occupies(coordinates)) {
                return false;
            }
        }
        return true;
    }

    boolean isInside(GridPoint2 coordinates) {
        return coordinates.x >= 0 && coordinates.x < width
                && coordinates.y >= 0 && coordinates.y < height;
    }

    void resolve() {
        List<LevelObject> activeObjects = objects.stream().filter(object -> !object.isDestroyed())
                .collect(Collectors.toList());
        for (LevelObject object : activeObjects) {
            if (object instanceof Bullet) {
                resolve((Bullet) object, activeObjects);
            }
        }
    }

    private void resolve(Bullet bullet, List<LevelObject> activeObjects) {
        GridPoint2 position = bullet.getCoordinates();
        if (!isInside(position)) {
            bullet.destroy();
            return;
        }
        for (LevelObject object : activeObjects) {
            if (object == bullet || bullet.wasFiredBy(object)) {
                continue;
            }
            boolean contact = object.occupies(position)
                    || (object instanceof Bullet && bullet.crossed((Bullet) object));
            if (!contact) {
                continue;
            }
            if (object instanceof Tank) {
                ((Tank) object).takeDamage(bullet.getDamage());
            }
            if (object instanceof Bullet) {
                ((Bullet) object).destroy();
            }
            if (object instanceof Obstacle || object instanceof Bullet) {
                bullet.destroy();
            }
        }
    }
}
