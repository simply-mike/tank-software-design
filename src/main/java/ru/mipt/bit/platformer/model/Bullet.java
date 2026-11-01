package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;

import java.util.Objects;

public final class Bullet implements LevelObject {

    private final GridPoint2 coordinates;
    private final GridPoint2 previousCoordinates;
    private final Direction direction;
    private final BulletSettings settings;
    private final Tank shooter;
    private double movementRemaining;
    private boolean destroyed;

    Bullet(GridPoint2 coordinates, Direction direction, BulletSettings settings,
           Tank shooter) {
        this.coordinates = new GridPoint2(coordinates);
        previousCoordinates = new GridPoint2(coordinates);
        this.direction = Objects.requireNonNull(direction);
        this.settings = Objects.requireNonNull(settings);
        this.shooter = Objects.requireNonNull(shooter);
        movementRemaining = settings.getMovementDuration();
    }

    @Override
    public void update(double deltaTime) {
        if (deltaTime < 0d || !Double.isFinite(deltaTime)) {
            throw new IllegalArgumentException("Tick duration must be non-negative and finite");
        }
        if (destroyed) {
            return;
        }
        previousCoordinates.set(coordinates);
        while (deltaTime >= movementRemaining) {
            deltaTime -= movementRemaining;
            coordinates.set(direction.calculateDestinationFrom(coordinates));
            movementRemaining = settings.getMovementDuration();
        }
        movementRemaining -= deltaTime;
    }

    @Override
    public double timeToNextTransition() {
        return movementRemaining;
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
        return (float) (1d - movementRemaining / settings.getMovementDuration());
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

    boolean crossed(Bullet other) {
        return previousCoordinates.equals(other.coordinates)
                && coordinates.equals(other.previousCoordinates);
    }

    void destroy() {
        destroyed = true;
    }

    @Override
    public void onRemoved() {
        destroy();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }
}
