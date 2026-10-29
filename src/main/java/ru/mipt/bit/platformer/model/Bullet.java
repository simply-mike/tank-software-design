package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Objects;
import java.util.function.Consumer;

public final class Bullet implements LevelObject {

    private final GridPoint2 coordinates;
    private final Direction direction;
    private final BulletSettings settings;
    private final Tank shooter;
    private final Consumer<Bullet> checkCollision;
    private double movementProgress;
    private boolean destroyed;

    Bullet(GridPoint2 coordinates, Direction direction, BulletSettings settings,
           Tank shooter, Consumer<Bullet> checkCollision) {
        this.coordinates = new GridPoint2(coordinates);
        this.direction = Objects.requireNonNull(direction);
        this.settings = Objects.requireNonNull(settings);
        this.shooter = Objects.requireNonNull(shooter);
        this.checkCollision = Objects.requireNonNull(checkCollision);
    }

    @Override
    public void update(float deltaTime) {
        if (deltaTime < 0f || !Float.isFinite(deltaTime)) {
            throw new IllegalArgumentException("Tick duration must be non-negative and finite");
        }
        if (destroyed) {
            return;
        }
        checkCollision.accept(this);
        movementProgress += (double) deltaTime / settings.getMovementDuration();
        while (!destroyed && movementProgress >= 1d) {
            coordinates.set(direction.calculateDestinationFrom(coordinates));
            movementProgress -= 1d;
            checkCollision.accept(this);
        }
    }

    @Override
    public boolean occupies(GridPoint2 coordinates) {
        return this.coordinates.equals(coordinates);
    }

    public GridPoint2 getCoordinates() {
        return new GridPoint2(coordinates);
    }

    public GridPoint2 getDestinationCoordinates() {
        return direction.calculateDestinationFrom(coordinates);
    }

    public float getMovementProgress() {
        return (float) movementProgress;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getDamage() {
        return settings.getDamage();
    }

    boolean wasFiredBy(LevelObject object) {
        return shooter == object;
    }

    void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
