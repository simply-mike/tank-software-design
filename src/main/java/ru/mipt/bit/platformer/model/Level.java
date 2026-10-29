package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Level implements TileAvailability {

    private final int width;
    private final int height;
    private final GridPoint2 playerPosition;
    private final List<LevelObject> objects = new ArrayList<>();
    private final CollisionHandler collisions;

    public Level(int width, int height, List<Tree> trees, GridPoint2 playerPosition) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level dimensions must be positive");
        }
        this.width = width;
        this.height = height;
        this.playerPosition = new GridPoint2(playerPosition);
        objects.addAll(List.copyOf(trees));
        collisions = new CollisionHandler(width, height, Collections.unmodifiableList(objects));
        if (!isFree(playerPosition)) {
            throw new IllegalArgumentException("Player must start on a free tile");
        }
        for (Tree tree : trees) {
            if (!collisions.isInside(tree.getCoordinates())) {
                throw new IllegalArgumentException("Tree lies outside the level");
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<LevelObject> getObjects() {
        return List.copyOf(objects);
    }

    public List<Tree> getTrees() {
        return objects.stream().filter(Tree.class::isInstance).map(Tree.class::cast)
                .collect(Collectors.toUnmodifiableList());
    }

    public GridPoint2 getPlayerPosition() {
        return new GridPoint2(playerPosition);
    }

    public Tank spawnTank(GridPoint2 coordinates, float movementDuration) {
        return spawnTank(coordinates, movementDuration, 3, new BulletSettings(1, 0.1f));
    }

    public Tank spawnTank(GridPoint2 coordinates, float movementDuration,
                          int health, BulletSettings bulletSettings) {
        if (!isFree(coordinates)) {
            throw new IllegalArgumentException("Tank must start on a free tile");
        }
        Tank tank = new Tank(coordinates, collisions, movementDuration,
                health, bulletSettings, this::add, collisions::resolve);
        add(tank);
        return tank;
    }

    @Override
    public boolean isFree(GridPoint2 coordinates) {
        return collisions.isFree(coordinates);
    }

    public void update(float deltaTime) {
        if (deltaTime < 0f || !Float.isFinite(deltaTime)) {
            throw new IllegalArgumentException("Tick duration must be non-negative and finite");
        }
        List<LevelObject> tickObjects = List.copyOf(objects);
        // Resolve spawn contacts before tanks can leave their starting cells.
        for (LevelObject object : tickObjects) {
            if (object instanceof Bullet) {
                collisions.resolve((Bullet) object);
            }
        }
        for (LevelObject object : tickObjects) {
            if (!object.isDestroyed() && objects.contains(object)) {
                object.update(deltaTime);
            }
        }
        objects.removeIf(LevelObject::isDestroyed);
    }

    public void add(LevelObject object) {
        Objects.requireNonNull(object);
        if (!object.isDestroyed() && !objects.contains(object)) {
            objects.add(object);
        }
    }
}
